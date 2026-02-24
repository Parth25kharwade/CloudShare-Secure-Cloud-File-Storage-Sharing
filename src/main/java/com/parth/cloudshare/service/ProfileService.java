package com.parth.cloudshare.service;

import com.mongodb.DuplicateKeyException;
import com.parth.cloudshare.Documents.ProfileDocument;
import com.parth.cloudshare.dto.ProfileDto;
import com.parth.cloudshare.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileDto createProfile(ProfileDto profileDto){
        if(profileRepository.existsByClerkId(profileDto.getClerkId())){
         return updateProfile(profileDto);
        }

        ProfileDocument profileDocument= ProfileDocument.builder()
                .email(profileDto.getEmail())
                .firstName(profileDto.getFirstName())
                .lastName(profileDto.getLastName())
                .clerkId(profileDto.getClerkId())
                .photoUrl(profileDto.getPhotoUrl())
                .credits(10)
                .crestedAt(Instant.now())
                .build();



            profileDocument=profileRepository.save(profileDocument);


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
    public ProfileDto updateProfile(ProfileDto profileDto){
        ProfileDocument profileDocument=profileRepository.findByClerkId(profileDto.getClerkId());
        if(profileDocument!=null){
           if(profileDto.getEmail()!=null && !profileDto.getEmail().isEmpty()){
               profileDocument.setEmail(profileDto.getEmail());
           }
           if(profileDto.getFirstName()!=null && !profileDto.getFirstName().isEmpty()){
               profileDocument.setFirstName(profileDto.getFirstName());

           }
           if(profileDto.getLastName()!=null && !profileDto.getLastName().isEmpty()){
               profileDocument.setLastName(profileDto.getLastName());

           }
           if(profileDto.getPhotoUrl()!=null && !profileDto.getPhotoUrl().isEmpty()){
               profileDocument.setPhotoUrl(profileDto.getPhotoUrl());

           }
           profileDocument=profileRepository.save(profileDocument);
           return ProfileDto.builder()
                   .id(profileDocument.getId())
                   .email(profileDocument.getEmail())
                   .firstName(profileDocument.getFirstName())
                   .lastName(profileDocument.getLastName())
                   .clerkId(profileDocument.getClerkId())
                   .crestedAt(profileDocument.getCrestedAt())
                   .credits(profileDocument.getCredits())
                   .photoUrl(profileDocument.getPhotoUrl())
                   .build();
        }
        return null;
    }
    public void delete(String clerkId){
        ProfileDocument profileDocument=profileRepository.findByClerkId(clerkId);
        if(profileDocument!=null){
            profileRepository.delete(profileDocument);
        }
    }

    public boolean existsByClerkId(String clerkId) {
        return profileRepository.existsByClerkId(clerkId);
    }
    public ProfileDocument getCurrentProfile(){
        if(SecurityContextHolder.getContext().getAuthentication()==null){
            throw new UsernameNotFoundException("User not Authenticated");
        }
        String clerkId=SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByClerkId(clerkId);
    }
}
