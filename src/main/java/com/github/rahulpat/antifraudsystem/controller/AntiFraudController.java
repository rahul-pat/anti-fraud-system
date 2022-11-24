package com.github.rahulpat.antifraudsystem.controller;

import com.github.rahulpat.antifraudsystem.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AntiFraudController {

    @Autowired
    Transaction transaction;

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<String> processTransaction(@RequestBody Transaction transaction) {
        return transaction.validateTransaction(transaction);
    }

}
