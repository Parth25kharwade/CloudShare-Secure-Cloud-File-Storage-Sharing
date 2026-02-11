package com.parth.cloudshare.repository;

import com.parth.cloudshare.Documents.UserCredit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserCreditRepository extends MongoRepository<UserCredit, String> {
    Boolean existsByClerkId(String clerkId);
}
