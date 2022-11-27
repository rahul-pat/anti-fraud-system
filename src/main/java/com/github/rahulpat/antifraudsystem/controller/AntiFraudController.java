package com.github.rahulpat.antifraudsystem.controller;

import com.github.rahulpat.antifraudsystem.entities.Transaction;
import com.github.rahulpat.antifraudsystem.entities.StolenCreditCard;
import com.github.rahulpat.antifraudsystem.repository.IpRepository;
import com.github.rahulpat.antifraudsystem.entities.Ip;
import com.github.rahulpat.antifraudsystem.repository.StolenCreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


@RestController
public class AntiFraudController {

    @Autowired
    IpRepository ipRepo;
    @Autowired
    StolenCreditCardRepository stolenCreditCardRepo;

    // This endpoint will validate the submitted transaction based on the requirements
    // provided by the JetBrains project and return a ResponseEntity object back to the client
    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<String> processTransaction(@RequestBody Transaction transaction) {
        String result = "";
        Set<String> info = new TreeSet<>();

        if (ipRepo.existsByIp(transaction.getIp())) {
            result = "PROHIBITED";
            info.add("ip");
        }

        if (stolenCreditCardRepo.existsByNumber(transaction.getNumber())) {
            result = "PROHIBITED";
            info.add("card-number");
        }

        if (transaction.getAmount() > 0 && transaction.getAmount() <= 200 && result != "PROHIBITED") {
            result = "ALLOWED";
            info.clear();
            info.add("none");
        } else if (transaction.getAmount() > 200 && transaction.getAmount() <= 1500 && result != "PROHIBITED") {
            result = "MANUAL_PROCESSING";
            info.add("amount");
        } else if (transaction.getAmount() > 1500) {
            result = "PROHIBITED";
            info.add("amount");
        }

        if (result == "") {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        } else {
            String infoOutput = String.join(", ", info);
            return new ResponseEntity(Map.of("result", result, "info", infoOutput), HttpStatus.OK);
        }

    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<String> addSuspiciousIp(@RequestBody Ip ip) {

        // If the suspicious IP is not a valid format, return HTTP BAD REQUEST status
        // Validate if the suspicious IP already exists in the database. If yes, return HTTP CONFLICT status
        // Else, save the IP to the database

        if (Ip.isIpValid(ip.getIp()) == false) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (ipRepo.existsByIp(ip.getIp())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            ipRepo.save(ip);
            Ip suspiciousIp = ipRepo.findByIp(ip.getIp());

            return new ResponseEntity(Map.of("id", suspiciousIp.getId(),
                    "ip", suspiciousIp.getIp()),
                    HttpStatus.OK);
        }
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<String> deleteSuspiciousIp(@PathVariable String ip) {

        // If the suspicious IP is not a valid format, return HTTP BAD REQUEST status
        // If the suspicious IP exists in the database, delete it and return an HTTP OK status
        // If not found in the database, return an HTTP NOT FOUND status
        if (Ip.isIpValid(ip) == false) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (ipRepo.existsByIp(ip)) {

            Ip suspiciousIp = ipRepo.findByIp(ip);
            ipRepo.delete(suspiciousIp);

            return new ResponseEntity(Map.of("status", "IP " + ip + " successfully removed!"), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<String> getAllSuspiciousIp() {
        List<Ip> allSuspiciousIp = ipRepo.findAll();
        return new ResponseEntity(allSuspiciousIp, HttpStatus.OK);
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<String> addStolenCard(@RequestBody StolenCreditCard stolenCreditCard) {

        // If the Stolen Credit Card is not a valid format, return HTTP BAD REQUEST status
        // Validate if the Stolen Credit Card already exists in the database. If yes, return HTTP CONFLICT status
        // Else, save the Stolen Credit Card to the database

        if (StolenCreditCard.isCardNumberValid(stolenCreditCard.getNumber()) == false) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (stolenCreditCardRepo.existsByNumber(stolenCreditCard.getNumber())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            stolenCreditCardRepo.save(stolenCreditCard);
            StolenCreditCard addedStolenCreditCard = stolenCreditCardRepo.findByNumber(stolenCreditCard.getNumber());

            return new ResponseEntity(Map.of("id", addedStolenCreditCard.getId(),
                    "number", addedStolenCreditCard.getNumber()),
                    HttpStatus.OK);
        }
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<String> deleteStolenCreditCard(@PathVariable String number) {

        // If the Stolen Credit Card is not a valid format, return HTTP BAD REQUEST status
        // If the Stolen Credit Card exists in the database, delete it and return an HTTP OK status
        // If not found in the database, return an HTTP NOT FOUND status
        if (StolenCreditCard.isCardNumberValid(number) == false) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (stolenCreditCardRepo.existsByNumber(number)) {

            StolenCreditCard stolenCreditCardToDelete = stolenCreditCardRepo.findByNumber(number);
            stolenCreditCardRepo.delete(stolenCreditCardToDelete);

            return new ResponseEntity(Map.of("status", "Card " + number + " successfully removed!"), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<String> getAllStolenCreditCard() {
        List<StolenCreditCard> allStolenCreditCard = stolenCreditCardRepo.findAll();
        return new ResponseEntity(allStolenCreditCard, HttpStatus.OK);
    }

}
