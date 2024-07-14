package recycling.back.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import recycling.back.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
