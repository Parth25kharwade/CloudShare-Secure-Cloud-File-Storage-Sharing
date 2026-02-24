package com.parth.cloudshare.repository;

import com.parth.cloudshare.Documents.FileMetadataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetadataDocumentRepository extends MongoRepository<FileMetadataDocument, String> {
    List<FileMetadataDocument> findByClerkId(String clerkId);
    Long countByClerkId(String clerkId);
}
