package com.parth.cloudshare.repository;

import com.parth.cloudshare.Documents.ProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<ProfileDocument, String> {
    Optional<ProfileDocument> findByEmail(String email);
}
