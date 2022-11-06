package antifraud.repository;

import antifraud.models.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    Boolean existsByNumber(String number);
    Optional<StolenCard> findByNumber(String number);
}
