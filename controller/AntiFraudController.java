package antifraud.controller;

import antifraud.Transaction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AntiFraudController {

    @PostMapping("/api/antifraud/transaction")
    public Transaction transaction(@RequestBody @Valid Transaction t) {
        t.calculateResult();
        return t;
    }
}