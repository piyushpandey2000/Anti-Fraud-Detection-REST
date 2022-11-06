package antifraud.repository;

import antifraud.Region;
import antifraud.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    int countDistinctRegionByNumberEqualsAndDateBetween(String number, String date1, String date2);
    List<Transaction> findAllByNumberEqualsAndDateBetween(String number, String date1, String date2);
    int countDistinctIpByNumberEqualsAndDateBetween(String number, String date1, String date2);
}
