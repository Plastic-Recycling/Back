package recycling.back.user.register.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import recycling.back.user.register.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
