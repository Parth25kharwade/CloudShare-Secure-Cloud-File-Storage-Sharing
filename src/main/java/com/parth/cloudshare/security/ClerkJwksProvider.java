package com.parth.cloudshare.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClerkJwksProvider {

    @Value("${clerk.jwks-url}")
    private String jwksUrl;

    private final Map<String, PublicKey> keyCache=new HashMap<>();
    private long lastFetchTime=0;
    private static final long CHACHE_TTL=3600000;//1hour

    public PublicKey getPublicKey(String keyId)throws Exception{
        if(keyCache.containsKey(keyId) && System.currentTimeMillis()-lastFetchTime<CHACHE_TTL){
            return keyCache.get(keyId);
        }
        refreshKey();
        return keyCache.get(keyId);
    }

    private void refreshKey()throws Exception{
        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode jwks=objectMapper.readTree(new URL(jwksUrl).openStream());
        JsonNode keys=jwks.get("keys");
        for(JsonNode keyNode:keys){
            String kid=keyNode.get("kid").asText();
            String kty=keyNode.get("kty").asText();

            String alg=keyNode.get("alg").asText();

           if("RSA".equals(kty) && "RS256".equals(alg)){
               String n=keyNode.get("n").asText();
               String e=keyNode.get("e").asText();
               
               PublicKey publicKey=createPublicKey(n,e);
               keyCache.put(kid,publicKey);
           }

           lastFetchTime=System.currentTimeMillis();
        }
    }

    private PublicKey createPublicKey(String modulas, String exponent) throws Exception {
       byte[] modulasBytes= Base64.getUrlDecoder().decode(modulas);
       byte[] exponentsBytes=Base64.getUrlDecoder().decode(exponent);

        BigInteger modulasBigint=new BigInteger(1,modulasBytes);
        BigInteger exponentBigint=new BigInteger(1,exponentsBytes);

        RSAPublicKeySpec spec=new RSAPublicKeySpec(modulasBigint,exponentBigint);

        KeyFactory factory=KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);

    }
}
