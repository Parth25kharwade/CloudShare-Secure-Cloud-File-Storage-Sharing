package com.parth.cloudshare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {
    @Value("${clerk.issuer}")
    private String clerkIssuer;
    private final ClerkJwksProvider clerkJwksProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().contains("/webhooks")){
            filterChain.doFilter(request,response);
        }
        String authHeader=request.getHeader("Authorization");
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Authorization Header Invalid");
            return;
        }
        try {
            String token=authHeader.substring(7);
            String[] chunks=token.split("\\.");
            if(chunks.length<3){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid JWT Token format");
                return;
            }
        String headerJson=new String(Base64.getUrlDecoder().decode(chunks[0]));
            ObjectMapper objectMapper=new ObjectMapper();
            JsonNode headerNode=objectMapper.readTree(headerJson);
            if(!headerNode.has("kid")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token Header Missing KeyId");
                return;

            }
            String kid=headerNode.get("kid").asText();
            PublicKey publicKey=clerkJwksProvider.getPublicKey(kid);
            if(publicKey==null){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid Key Id");
                return;
            }
            Claims claims= Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(60)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String clerkID=claims.getSubject();
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(clerkID,null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid JWT Token: "+e.getMessage());
            return;
        }
    }
}
