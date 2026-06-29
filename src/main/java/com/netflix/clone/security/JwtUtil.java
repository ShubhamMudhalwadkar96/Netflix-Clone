package com.netflix.clone.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component // added so this bean is managed by IoC container
public class JwtUtil {

    // 30 Days, 24 hrs, 60 min, 60 sec and 1000 millisecond
    private static final long JWT_TOKEN_VALIDITY = 30L * 24 * 60 * 60 * 1000;

    @Value("${jwt.secret:defaultSecretKeyForNetflixClonedefaultSecretKeyForNetflixClone}")
    private String secret;

    /**
     * Generates the signing key used for JWT signing and verification.
     *
     * @return SecretKey derived from the configured secret
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token JWT token
     * @return username stored in the token subject
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts the role claim from the JWT token.
     *
     * @param token JWT token
     * @return role stored in the token
     */
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    /**
     * Retrieves the expiration date from the JWT token.
     *
     * @param token JWT token
     * @return token expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided resolver.
     *
     * @param token          JWT token
     * @param claimsResolver function used to extract a specific claim
     * @param <T>            type of the claim being returned
     * @return extracted claim value
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token and returns all claims contained within it.
     *
     * @param token JWT token
     * @return Claims extracted from the token
     */
    private Claims getAllClaimFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks whether the JWT token has expired.
     *
     * @param token JWT token
     * @return true if the token is expired, otherwise false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates a JWT token containing the username and role.
     *
     * @param username username to be stored as the token subject
     * @param role     user role to be stored as a claim
     * @return generated JWT token
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return doGenerateToken(claims, username);
    }

    /**
     * Generates a JWT token with the provided claims and subject.
     * The token includes issue time, expiration time, and is signed
     * using the configured signing key.
     *
     * @param claims  additional claims to be included in the token
     * @param subject subject (typically username) to be stored in the token
     * @return generated JWT token as a compact string
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the provided JWT token by verifying its signature,
     * structure, and expiration status.
     *
     * @param token JWT token to validate
     * @return true if the token is valid and not expired; false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            getAllClaimFromToken(token); // verifies signature
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}