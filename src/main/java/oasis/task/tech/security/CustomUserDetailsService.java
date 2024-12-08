package oasis.task.tech.security;

import oasis.task.tech.repository.actors.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        oasis.task.tech.domains.actors.User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException("User not found with email:"+ email));
        user.setLastLoggedIn(new Date());
        userRepository.save(user);
        return user;
    }
}
