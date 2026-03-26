package com.parth.cloudshare.service;

import com.parth.cloudshare.Documents.PaymentTransactionDocument;
import com.parth.cloudshare.Documents.ProfileDocument;
import com.parth.cloudshare.dto.PaymentDto;
import com.parth.cloudshare.dto.PaymentVerificationDto;
import com.parth.cloudshare.repository.PaymentTransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserCreditService userCreditService;
    private final ProfileService profileService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorpaySecrete;

    public PaymentDto createOrder(PaymentDto paymentDto){
        try {
            ProfileDocument currentProfile=profileService.getCurrentProfile();
            String clerkId=currentProfile.getClerkId();
            RazorpayClient razorpayClient=new RazorpayClient(razorpayKeyId,razorpaySecrete);
            JSONObject orderRequest=new JSONObject();
            orderRequest.put("amount",paymentDto.getAmount());
            orderRequest.put("currency",paymentDto.getCurrency());
            orderRequest.put("receipt","order"+System.currentTimeMillis());
            Order order=razorpayClient.orders.create(orderRequest);
            String orderId=order.get("id");
            PaymentTransactionDocument paymentTransactionDocument=PaymentTransactionDocument.builder()
                    .clerkId(clerkId)
                    .orderId(orderId)
                    .amount(paymentDto.getAmount())
                    .currency(paymentDto.getCurrency())
                    //.creditsAdded(paymentDto.getCredits())//
                    .planId(paymentDto.getPlanId())
                    .status("PENDING")
                    .transactionDate(LocalDateTime.now())
                    .userEmail(currentProfile.getEmail())
                    .userName(currentProfile.getFirstName()+" "+currentProfile.getLastName())
                    .build();
            paymentTransactionRepository.save(paymentTransactionDocument);

            return PaymentDto.builder()
                    .orderId(orderId)
                    .success(true)
                    .message("Order Created Successfully")
                    .build();


        } catch (Exception e) {
            return PaymentDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();

        }
    }
    public PaymentDto verifyPayment(PaymentVerificationDto request){
        try {
            ProfileDocument currentProfile=profileService.getCurrentProfile();
            String clerkId=currentProfile.getClerkId();
            String data=request.getRazorpayOrderId()+"|"+request.getRazorpayPaymentId();
            String generatedSignature=generateHmacSha256Signature(data,razorpaySecrete);
            if(!generatedSignature.equals(request.getRazorpaySignature())){
               updateTransactionStatus(request.getRazorpayOrderId(),"FAILED",request.getRazorpayPaymentId(),null);
                return PaymentDto.builder()
                        .success(false)
                        .message("Payment Signature Verification Failed")
                        .build();
            }
            int creditsToAdd=0;
            String plan="BASIC";
            switch (request.getPlanId()){
                case "PREMIUM":
                    creditsToAdd=100;
                    plan="PREMIUM";
                    break;
                case "ULTIMATE":
                    creditsToAdd=200;
                    plan="ULTIMATE";
                    break;

            }
            if(creditsToAdd>0){
            userCreditService.addCredits(clerkId,creditsToAdd,plan);
            updateTransactionStatus(request.getRazorpayOrderId(),"SUCCESS",request.getRazorpayPaymentId(),creditsToAdd);
             return PaymentDto.builder()
                     .success(true)
                     .message("Payment Verified and credits added successfully")
                     .credits(userCreditService.getUserCredits(clerkId).getCredits())
                     .build();

            }else{
                updateTransactionStatus(request.getRazorpayOrderId(),"FAILED",request.getRazorpayPaymentId(),null);
                return PaymentDto.builder()
                        .success(false)
                        .message("Invalid Plan selected")
                        .build();
            }


        } catch (Exception e) {
              try {
                  updateTransactionStatus(request.getRazorpayOrderId(),"ERROR",request.getRazorpayPaymentId(),null);


              } catch (Exception ex) {
                  throw new RuntimeException(ex);
              }
            return PaymentDto.builder()
                    .success(false)
                    .message("Error verifying payment "+e.getMessage())
                    .build();

        }
    }

    private void updateTransactionStatus(String razorpayOrderId, String status, String razorpayPaymentId, Integer creditsToAdd) {
        paymentTransactionRepository.findAll().stream()
                .filter(t->t.getOrderId()!=null && t.getOrderId().equals(razorpayOrderId))
                .findFirst()
                .map(transaction->{
                    transaction.setStatus(status);
                    transaction.setPaymentId(razorpayPaymentId);
                    if (creditsToAdd!=null){
                        transaction.setCreditsAdded(creditsToAdd);

                    }
                    return paymentTransactionRepository.save(transaction);
                })
                .orElse(null);
    }

    private String generateHmacSha256Signature(String data, String secret) {
        try {
            String algorithm = "HmacSHA256";

            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
            mac.init(secretKey);

            byte[] hash = mac.doFinal(data.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA256 signature", e);
        }
    }
}
