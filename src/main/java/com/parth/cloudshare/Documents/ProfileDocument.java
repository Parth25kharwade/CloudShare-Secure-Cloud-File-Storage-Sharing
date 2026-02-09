package com.parth.cloudshare.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "profiles")
public class ProfileDocument {
    @Id
    private String id;
    private String clerkId;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private String photoUrl;
    private Integer credits;
    @CreatedDate
    private Instant crestedAt;

}
