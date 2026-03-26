package com.parth.cloudshare.controller;

import com.parth.cloudshare.Documents.PaymentTransactionDocument;
import com.parth.cloudshare.Documents.ProfileDocument;
import com.parth.cloudshare.repository.PaymentTransactionRepository;
import com.parth.cloudshare.service.PaymentService;
import com.parth.cloudshare.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
private final PaymentTransactionRepository paymentTransactionRepository;
private final ProfileService profileService;

@GetMapping
public ResponseEntity<?> getUsersTransaction(){
    ProfileDocument currentProfile=profileService.getCurrentProfile();
    String clerkId=currentProfile.getClerkId();
    List<PaymentTransactionDocument> transactionDocuments=paymentTransactionRepository.findByClerkIdAndStatusOrderByTransactionDateDesc(clerkId,"SUCCESS");
    return ResponseEntity.ok(transactionDocuments);
}

}
