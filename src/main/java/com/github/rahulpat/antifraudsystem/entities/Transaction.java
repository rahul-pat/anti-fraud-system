package com.github.rahulpat.antifraudsystem.entities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Transaction {
    private long amount;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    // This method is used by the http POST /api/antifraud/transaction endpoint
    // It will validate the submitted transaction based on the requirements
    // provided by the JetBrains project and return a ResponseEntity object back to the client
    public ResponseEntity<String> validateTransaction(Transaction transaction) {
        if (transaction.getAmount() > 0 && transaction.getAmount() <= 200) {
            return new ResponseEntity(Map.of("result", "ALLOWED"), HttpStatus.OK);
        } else if (transaction.getAmount() > 200 && transaction.getAmount() <= 1500) {
            return new ResponseEntity(Map.of("result", "MANUAL_PROCESSING"), HttpStatus.OK);
        } else if (transaction.getAmount() > 1500) {
            return new ResponseEntity(Map.of("result", "PROHIBITED"), HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
}
