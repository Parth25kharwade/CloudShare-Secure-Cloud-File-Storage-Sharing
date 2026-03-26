package com.parth.cloudshare.controller;

import com.parth.cloudshare.dto.PaymentDto;
import com.parth.cloudshare.dto.PaymentVerificationDto;
import com.parth.cloudshare.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentDto paymentDto){
     PaymentDto responce=paymentService.createOrder(paymentDto);
     if (responce.getSuccess()){
         return ResponseEntity.ok(responce);
     }else{
         return ResponseEntity.badRequest().body(responce);

     }

    }
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationDto request){
        PaymentDto responce=paymentService.verifyPayment(request);
        if (responce.getSuccess()){
            return ResponseEntity.ok(responce);
        }else{
            return ResponseEntity.badRequest().body(responce);

        }
    }
}
