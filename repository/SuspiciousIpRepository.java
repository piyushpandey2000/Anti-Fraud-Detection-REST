package antifraud.repository;

import antifraud.models.SuspiciousIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspiciousIpRepository extends JpaRepository<SuspiciousIp, Long> {
    Boolean existsByIp(String ip);
    Optional<SuspiciousIp> findByIp(String ip);
}
