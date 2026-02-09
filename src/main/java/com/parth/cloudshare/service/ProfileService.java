package com.parth.cloudshare.service;

import com.mongodb.DuplicateKeyException;
import com.parth.cloudshare.Documents.ProfileDocument;
import com.parth.cloudshare.dto.ProfileDto;
import com.parth.cloudshare.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileDto createProfile(ProfileDto profileDto){
        ProfileDocument profileDocument= ProfileDocument.builder()
                .email(profileDto.getEmail())
                .firstName(profileDto.getFirstName())
                .lastName(profileDto.getLastName())
                .clerkId(profileDto.getClerkId())
                .photoUrl(profileDto.getPhotoUrl())
                .credits(10)
                .crestedAt(Instant.now())
                .build();


        try{
            profileDocument=profileRepository.save(profileDocument);
        }catch (DuplicateKeyException e){
            throw new RuntimeException("Email already exits...");
        }

        return ProfileDto.builder()
                .id(profileDocument.getId())
                .email(profileDocument.getEmail())
                .firstName(profileDocument.getFirstName())
                .lastName(profileDocument.getLastName())
                .clerkId(profileDocument.getClerkId())
                .photoUrl(profileDocument.getPhotoUrl())
                .credits(10)
                .crestedAt(Instant.now())
                .build();


    }
}
