package com.userauthenticationmicroservice.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    private final String SECRET_KEY;

    public JwtService(@Value("${JWT_SECRET}") String secretKey){
        this.SECRET_KEY = secretKey;
    }

    public String extractEmail(String jwtToken){
        return extractClaim(jwtToken, Claims::getSubject); //The subject is the unique ideentifier for the user, email in this application.
    }

    //Generate a JWT token for the given user details and extra claims. The token will include the user's email as the subject and any additional claims provided in the extraClaims map. The token will be signed using the secret key defined in the application properties.
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                    .claims(extraClaims)
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24*3)) //Token valid for 3 days
                    .signWith(getSigningKey(), Jwts.SIG.HS256)
                    .compact();
    }
    
    //Overloaded method to generate a token without extra claims.
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails){
        final String email = extractEmail(jwtToken);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken){
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken){
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken){
        return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
    }

    private SecretKey getSigningKey(){
        final int MIN_BYTES = 32; //256 bits for HS256
        byte[] keyBytes;
        try{
            keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        }catch(IllegalArgumentException e){
            throw new IllegalStateException("Invalid JWT secret key. Ensure it is a valid Base64-encoded string.", e);
        }
        if (keyBytes.length < MIN_BYTES){
            throw new IllegalStateException(
                "Decoded JWT_SECRET is too short (" + keyBytes.length + " bytes). Require >= " + MIN_BYTES + "  bytes.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
