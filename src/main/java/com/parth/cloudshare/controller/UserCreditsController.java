package com.parth.cloudshare.controller;

import com.parth.cloudshare.Documents.UserCredit;
import com.parth.cloudshare.dto.UserCreditsDto;
import com.parth.cloudshare.service.UserCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserCreditsController {
    private final UserCreditService userCreditService;

    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits(){
        UserCredit userCredit=userCreditService.getUserCredits();
        UserCreditsDto userCreditsDto=UserCreditsDto.builder()
                .credits(userCredit.getCredits())
                .plan(userCredit.getPlan())
                .build();
        return ResponseEntity.ok(userCreditsDto);
    }
}
