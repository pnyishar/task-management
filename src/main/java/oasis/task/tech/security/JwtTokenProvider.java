/**
 * Created By: Paul Nyishar
 * Date: 9/15/2024
 * Time: 6:24 AM
 * Project: hall-mgt-backend
 */
package oasis.task.tech.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import oasis.task.tech.dto.auth.AuthenticationResponse;
import oasis.task.tech.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationInMs;

    @Value("${jwt.remember-me.expiration}")
    private long tokenValidityInSecondsForRememberMe;

    // generate token
    public AuthenticationResponse generateToken(String principal, Collection<? extends GrantedAuthority> grantedAuthorities, boolean rememberMe){
        String authorities = grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long expiresIn = rememberMe ? tokenValidityInSecondsForRememberMe : jwtExpirationInMs;
        Date validity = new Date((new Date()).getTime() + (expiresIn * 1000));
        String token = Jwts.builder()
                .setSubject(principal)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .setExpiration(validity)
                .compact();
        return new AuthenticationResponse(token, expiresIn);
    }

    Collection<GrantedAuthority> getAuthorities(String token) {

        Claims claims = getClaimsFromToken(token);
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public AuthenticationResponse generateToken(UserDetails userDetails, boolean rememberMe) {
        return this.generateToken(userDetails.getUsername(), userDetails.getAuthorities(), rememberMe);
    }

    // get username from token
    public String getUsernameFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token){
        try {
            log.debug("Validating token {}", token);
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            log.error("Invalid JWT signature: {}", ex.getMessage());
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
        }catch (MalformedJwtException ex){
            log.error("Invalid Token: {}", ex.getMessage());
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        }catch (ExpiredJwtException ex){
            log.error("Expired JWT signature: {}", ex.getMessage());
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        }catch (UnsupportedJwtException ex){
            log.error("Unsupported JWT token: {}", ex.getMessage());
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
        }

    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public AuthenticationResponse refreshToken(String token, boolean rememberMe) {
        return this.generateToken(getUsernameFromJWT(token), getAuthorities(token), rememberMe);
    }
}
