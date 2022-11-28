package com.github.rahulpat.antifraudsystem.controller;

import com.github.rahulpat.antifraudsystem.entities.*;
import com.github.rahulpat.antifraudsystem.services.IpService;
import com.github.rahulpat.antifraudsystem.services.StolenCreditCardService;
import com.github.rahulpat.antifraudsystem.services.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AntiFraudController {

    @Autowired
    TransactionService transactionService;
    @Autowired
    IpService ipService;
    @Autowired
    StolenCreditCardService stolenCreditCardService;

    // This endpoint will validate the submitted transaction following the Rule-based system
    // defined in the TransactionService. The rule-based system is designed based on the requirements
    // provided for the project on JetBrains Academy
    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<String> processTransactionEndpoint(@RequestBody Transaction transaction) {

        // Perform data validation
        if (transactionService.isTransactionFormatValid(transaction) == false) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return transactionService.transactionRulesEngine(transaction);
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<String> addSuspiciousIpEndpoint(@RequestBody Ip ip) {

        return ipService.addSuspiciousIp(ip);

    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<String> deleteSuspiciousIpEndpoint(@PathVariable String ip) {

        return ipService.deleteSuspiciousIp(ip);

    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<String> getAllSuspiciousIpEndpoint() {

        return ipService.getAllSuspiciousIp();

    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<String> addStolenCreditCardEndpoint(@RequestBody StolenCreditCard stolenCreditCard) {

        return stolenCreditCardService.addStolenCard(stolenCreditCard);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<String> deleteStolenCreditCardEndpoint(@PathVariable String number) {

      return stolenCreditCardService.deleteStolenCreditCard(number);

    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<String> getAllStolenCreditCardEndpoint() {

        return stolenCreditCardService.getAllStolenCreditCard();

    }

}
