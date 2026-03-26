package com.parth.cloudshare.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserCreditsDto {
    private Integer credits;
    private String plan;
}
