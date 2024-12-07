package oasis.task.tech.repository.actors;

import oasis.task.tech.domains.actors.User;
import oasis.task.tech.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
