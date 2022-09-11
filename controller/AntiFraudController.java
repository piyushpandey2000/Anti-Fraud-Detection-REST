package antifraud.controller;

import antifraud.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    Logger logger = LoggerFactory.getLogger(AntiFraudController.class);

    @PostMapping("/transaction")
    public Transaction transaction(@RequestBody @Valid Transaction transaction) {
        logger.info("Processing transaction with body: {}", transaction);
        transaction.calculateResult();
        return transaction;
    }
}