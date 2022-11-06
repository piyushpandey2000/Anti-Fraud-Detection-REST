package antifraud.service;

import antifraud.Region;
import antifraud.Transaction;
import antifraud.TransactionResult;
import antifraud.models.StolenCard;
import antifraud.models.SuspiciousIp;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class AntiFraudService {
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    @Autowired
    StolenCardRepository stolenCardRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public ResponseEntity<?> processTransaction(Transaction transaction) {
        String date1;
        String date2 = transaction.getDate();
        try {
            date1 = subtractOneHour(date2);
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!isValidCard(transaction.getNumber())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        transaction.calculateResult();
        int ip_corr = transactionRepository.countDistinctIpByNumberEqualsAndDateBetween(transaction.getNumber(), date1, date2);
        int region_corr = transactionRepository.countDistinctRegionByNumberEqualsAndDateBetween(transaction.getNumber(), date1, date2);

        List<Transaction> list = transactionRepository.findAllByNumberEqualsAndDateBetween(transaction.getNumber(), date1, date2);
        Set<String> ipSet = new HashSet<>();
        Set<Region> regionSet = new HashSet<>();

        for (Transaction t : list) {
            System.out.println(t);
            ipSet.add(t.getIp());
            regionSet.add(t.getRegion());
        }

        ip_corr = ipSet.contains(transaction.getIp()) ? ipSet.size()-1 : ipSet.size();
        region_corr = regionSet.contains(transaction.getRegion()) ? regionSet.size()-1 : regionSet.size();


        if(transaction.getResult()==TransactionResult.PROHIBITED) {
            transaction.setInfo("amount");
        }
        if(stolenCardRepository.findByNumber(transaction.getNumber()).isPresent()) {
            transaction.setInfo(transaction.getInfo().equals("none") ? "card-number" : transaction.getInfo() + ", card-number");
        }
        if(suspiciousIpRepository.findByIp(transaction.getIp()).isPresent()) {
            transaction.setInfo(transaction.getInfo().equals("none") ? "ip" : transaction.getInfo() + ", ip");
        }
        if(ip_corr>2) {
            transaction.setInfo(transaction.getInfo().equals("none") ? "ip-correlation" : transaction.getInfo() + ", ip-correlation");
        }
        if(region_corr>2) {
            transaction.setInfo(transaction.getInfo().equals("none") ? "region-correlation" : transaction.getInfo() + ", region-correlation");
        }

        if(!transaction.getInfo().equals("none")) {
            transaction.setResult(TransactionResult.PROHIBITED);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }


        if (transaction.getResult()==TransactionResult.MANUAL_PROCESSING) {
            transaction.setInfo("amount");
        }
        if(ip_corr==2) {
            transaction.setInfo(transaction.getInfo().equals("none") ? "ip-correlation" : transaction.getInfo() + ", ip-correlation");
        }
        if(region_corr==2) {
            transaction.setInfo(transaction.getInfo().equals("none") ? "region-correlation" : transaction.getInfo() + ", region-correlation");
        }
        if(!transaction.getInfo().equals("none")) {
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }

        transactionRepository.save(transaction);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    public List<SuspiciousIp> getAllSuspiciousIps() {
        return suspiciousIpRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public ResponseEntity<?> saveSuspiciousIp(SuspiciousIp ipObj) {
        Boolean exists = suspiciousIpRepository.existsByIp(ipObj.getIp());

        if(exists) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        SuspiciousIp savedIpObj = suspiciousIpRepository.save(ipObj);
        return new ResponseEntity<>(savedIpObj, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSuspiciousIp(String ip) {
        String validIpRegex = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$";

        if (!ip.matches(validIpRegex)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<SuspiciousIp> ipObj = suspiciousIpRepository.findByIp(ip);

        if(ipObj.isPresent()) {
            suspiciousIpRepository.deleteById(ipObj.get().getId());
            return new ResponseEntity<>(Map.of("status", String.format("IP %s successfully removed!", ip)), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public List<StolenCard> getAllStolenCards() {
        return stolenCardRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public ResponseEntity<?> saveStolenCard(StolenCard card) {
        if(!isValidCard(card.getNumber())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Boolean exists = stolenCardRepository.existsByNumber(card.getNumber());

        if(exists) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        StolenCard savedCard = stolenCardRepository.save(card);
        return new ResponseEntity<>(savedCard, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteStolenCard(String number) {
        if(!isValidCard(number)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<StolenCard> card = stolenCardRepository.findByNumber(number);
        if (card.isPresent()) {
            stolenCardRepository.deleteById(card.get().getId());
            return new ResponseEntity<>(Map.of("status", String.format("Card %s successfully removed!", number)), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    boolean isValidCard(String number) {
        int n = number.length();

        if(n!=16) {
            return false;
        }

        int sum = 0;
        for(int i=0; i<n; i++) {
            int x = number.charAt(n-1-i) - '0';

            if(i%2!=0) {
                x = x*2;
                if(x>9) {
                    x = x%10 + x/10;
                }
            }

            sum += x;
        }
        return sum%10==0;
    }

    static String subtractOneHour(String date) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d1 = f.parse(date);

        Calendar c = Calendar.getInstance();
        c.setTime(d1);
        c.add(Calendar.HOUR_OF_DAY, -1);

        return f.format(c.getTime());
    }
}
