package com.parth.cloudshare.service;

import com.parth.cloudshare.Documents.UserCredit;
import com.parth.cloudshare.repository.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditService {
    private final UserCreditRepository userCreditRepository;

    public UserCredit initializeCredits(String clerkId) {

            UserCredit userCredit=UserCredit.builder()
                    .clerkId(clerkId)
                    .credits(10)
                    .plan("basic")
                    .build();
          return userCreditRepository.save(userCredit);


    }



}
