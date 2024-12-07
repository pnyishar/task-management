package oasis.task.tech.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import oasis.task.tech.dto.JsonResponse;
import oasis.task.tech.dto.auth.AuthenticationRequest;
import oasis.task.tech.dto.auth.AuthenticationResponse;
import oasis.task.tech.security.JwtTokenProvider;
import oasis.task.tech.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:44PM
 * Project:task-management
 */
@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AuthenticationService authenticationService;

    private JwtTokenProvider tokenProvider;

    public AuthenticationController(AuthenticationService authenticationService, JwtTokenProvider tokenProvider) {
        this.authenticationService = authenticationService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("signIn")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid AuthenticationRequest authenticationRequest){
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtTokenProvider.AUTHORIZATION_HEADER, "Bearer " + authenticationResponse);
        return new ResponseEntity<>(authenticationResponse, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("refresh")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request,
                                                              @RequestParam(value = "remember-me", required = false,
                                                                      defaultValue = "false") Boolean rememberMe) {
        logger.info("Request for token refresh");
        AuthenticationResponse authenticationResponse = tokenProvider.refreshToken(tokenProvider.resolveToken(request), rememberMe);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtTokenProvider.AUTHORIZATION_HEADER, "Bearer " + authenticationResponse);
        return new ResponseEntity<>(authenticationResponse, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String token = tokenProvider.resolveToken(request);
        String msg = null;
        if (token != null){
            // Invalidate the token by setting its expiration date to the current time
            Claims claims = tokenProvider.getClaimsFromToken(token);
            claims.setExpiration(new Date());

            msg = "Successfully logged out!!!";
        }

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true, msg, "Successfully Registered User!!!"), HttpStatus.OK);

    }
}
