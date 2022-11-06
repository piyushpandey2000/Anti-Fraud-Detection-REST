package antifraud.controller;

import antifraud.Transaction;
import antifraud.models.StolenCard;
import antifraud.models.SuspiciousIp;
import antifraud.service.AntiFraudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    Logger logger = LoggerFactory.getLogger(AntiFraudController.class);

    @Autowired
    AntiFraudService antiFraudService;

    @PostMapping("/transaction")
    public ResponseEntity<?> transaction(@RequestBody @Valid Transaction transaction) {
        logger.info("Processing transaction with body: {}", transaction);

        return antiFraudService.processTransaction(transaction);
    }

    // SUSPICIOUS IP

    @GetMapping("/suspicious-ip")
    public List<SuspiciousIp> getAllSuspiciousIps() {
        return antiFraudService.getAllSuspiciousIps();
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<?> saveSuspiciousIp(@RequestBody @Valid SuspiciousIp ipObj) {
        return antiFraudService.saveSuspiciousIp(ipObj);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteSuspiciousIp(@PathVariable String ip) {
        return antiFraudService.deleteSuspiciousIp(ip);
    }


    // STOLEN CARD

    @GetMapping("/stolencard")
    public List<StolenCard> getAllStolenCards() {
        return antiFraudService.getAllStolenCards();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<?> saveStolenCard(@RequestBody @Valid StolenCard card) {
        return antiFraudService.saveStolenCard(card);
    }

    @DeleteMapping("stolencard/{number}")
    public ResponseEntity<?> deleteStolenCard(@PathVariable String number) {
        return antiFraudService.deleteStolenCard(number);
    }
}