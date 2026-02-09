package com.parth.cloudshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {
    private String id;
    private String clerkId;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;
    private Integer credits;
    private Instant crestedAt;
}
