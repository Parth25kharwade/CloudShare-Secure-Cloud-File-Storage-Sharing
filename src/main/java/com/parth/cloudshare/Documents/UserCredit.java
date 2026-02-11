package com.parth.cloudshare.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_credits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredit {
    @Id
    private String id;
    private String clerkId;
    private Integer credits;
    private String plan;//basic premium ultimate
}
