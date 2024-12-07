package oasis.task.tech.service;

import oasis.task.tech.domains.actors.User;
import oasis.task.tech.dto.auth.AuthenticationRequest;
import oasis.task.tech.dto.auth.AuthenticationResponse;
import oasis.task.tech.exception.NotFoundException;
import oasis.task.tech.mappers.UserRoleMapper;
import oasis.task.tech.repository.actors.UserRepository;
import oasis.task.tech.security.CustomUserDetailsService;
import oasis.task.tech.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:36PM
 * Project:task-management
 */

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository, CustomUserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        // Authenticate the user
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                );

        // This line might be causing the loop, make sure CustomUserDetailsService is properly configured
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Fetch user from the database
        User user = userRepository.findByEmail(authenticationRequest.getUsername()).orElseThrow(
                () -> new NotFoundException("The requested user was not found!!!")
        );

        // Generate JWT token
        boolean rememberMe = (authenticationRequest.isRememberMe() != null && authenticationRequest.isRememberMe());
        final AuthenticationResponse authenticationResponse = jwtTokenProvider.generateToken(
                authentication.getName(),
                authentication.getAuthorities(),
                rememberMe
        );

        // Prepare response
        authenticationResponse.setEmail(user.getEmail());
        authenticationResponse.setFullName(user.getFullName());
        authenticationResponse.setRoles(UserRoleMapper.mapToDtoList(user.getRoles()));

        return authenticationResponse;
    }
}
