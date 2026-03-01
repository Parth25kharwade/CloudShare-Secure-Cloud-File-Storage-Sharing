package com.parth.cloudshare.service;

import com.parth.cloudshare.Documents.UserCredit;
import com.parth.cloudshare.repository.ProfileRepository;
import com.parth.cloudshare.repository.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditService {
    private final UserCreditRepository userCreditRepository;
    private final ProfileService profileService;

    public UserCredit initializeCredits(String clerkId) {

            UserCredit userCredit=UserCredit.builder()
                    .clerkId(clerkId)
                    .credits(10)
                    .plan("basic")
                    .build();
          return userCreditRepository.save(userCredit);


    }
    public UserCredit getUserCredits(String clerkId){
        return userCreditRepository.findByClerkId(clerkId).orElseGet(() -> initializeCredits(clerkId));
    }
    public UserCredit getUserCredits(){
       String clerkId= profileService.getCurrentProfile().getClerkId();
       return getUserCredits(clerkId);

    }
    public Boolean hasEnoughCredits(int requiredCredits){
        UserCredit userCredit=getUserCredits();
        if(userCredit.getCredits()>=requiredCredits){
            return true;
        }else{
            return false;
        }

    }
    public UserCredit consumeCredits(){
        UserCredit userCredit=getUserCredits();
        if(userCredit.getCredits()<=0){
            return null;
        }
        userCredit.setCredits(userCredit.getCredits()-1);
        return userCreditRepository.save(userCredit);
    }



}
