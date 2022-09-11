package antifraud.repository;

import antifraud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsernameIgnoreCase(String username);
    void deleteByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
