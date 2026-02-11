package com.parth.cloudshare.controller;

import com.parth.cloudshare.dto.ProfileDto;
import com.parth.cloudshare.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class ClerkWebhookController {
    @Value("${clerk.webhook.secret}")
    private String webhookSecret;
    private final ProfileService profileService;



    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(
            @RequestHeader("svix-id") String svixId,
            @RequestHeader("svix-timestamp") String svixTimestamp,
            @RequestHeader("svix-signature") String svixSignature,
            @RequestBody String payload) {

        try {


            boolean isValid = verifyWebhookSignature(
                    svixId,
                    svixTimestamp,
                    svixSignature,
                    payload
            );

            if (!isValid) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid webhook signature");
            }


            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            String eventType = root.get("type").asText();

            switch (eventType) {

                case "user.created":
                    handleUserCreated(root.path("data"));
                    System.out.println("🔥 User Created Event");
                    break;

                case "user.updated":
                    handleUserUpdated(root.path("data"));
                    System.out.println("🔥 User Updated Event");
                    break;

                case "user.deleted":
                    handleUserDeleted(root.path("data"));
                    System.out.println("🔥 User Deleted Event");
                    break;

                default:
                    System.out.println("Unhandled event: " + eventType);
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error processing webhook");
        }
    }
    private boolean verifyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {
        return true;
    }
    private void handleUserCreated(JsonNode data) {

        String clerkId = data.path("id").asText("");


        String email = "";
        JsonNode emailAddresses = data.path("email_addresses");

        if (emailAddresses.isArray() && emailAddresses.size() > 0) {
            email = emailAddresses.get(0)
                    .path("email_address")
                    .asText("");
        }

        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String imageUrl=data.path("image_url").asText();

        ProfileDto newProfile= ProfileDto.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(imageUrl)
                .build();
        profileService.createProfile(newProfile);
    }


    private void handleUserUpdated(JsonNode data) {

        String clerkId = data.path("id").asText("");


        String email = "";
        JsonNode emailAddresses = data.path("email_addresses");

        if (emailAddresses.isArray() && emailAddresses.size() > 0) {
            email = emailAddresses.get(0)
                    .path("email_address")
                    .asText("");
        }

        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String imageUrl=data.path("image_url").asText();

        ProfileDto updatedProfile= ProfileDto.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(imageUrl)
                .build();
        updatedProfile=profileService.updateProfile(updatedProfile);
        if(updatedProfile==null){
            handleUserCreated(data);
        }
    }


    private void handleUserDeleted(JsonNode data) {

        String clerkId = data.path("id").asText("");

        profileService.delete(clerkId);
    }
}

