package com.parth.cloudshare.repository;

import com.parth.cloudshare.Documents.PaymentTransactionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransactionDocument, String> {
     List<PaymentTransactionDocument> findByClerkId(String clerkId);
     List<PaymentTransactionDocument> findByClerkIdOrderByTransactionDateDesc(String clerkId);
     List<PaymentTransactionDocument> findByClerkIdAndStatusOrderByTransactionDateDesc(String clerkId, String status);
}
