package com.github.rahulpat.antifraudsystem.services;

import com.github.rahulpat.antifraudsystem.entities.*;
import com.github.rahulpat.antifraudsystem.repository.IpRepository;
import com.github.rahulpat.antifraudsystem.repository.StolenCreditCardRepository;
import com.github.rahulpat.antifraudsystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionService {

    @Autowired
    IpRepository ipRepo;
    @Autowired
    StolenCreditCardRepository stolenCreditCardRepo;
    @Autowired
    TransactionRepository transactionRepo;

    // This method validates the Transaction data in the request JSON payload is valid
    public boolean isTransactionFormatValid(Transaction transaction) {

        // Validate the transaction amount
        if (transaction.getAmount() <= 0) {
            return false;
        }

        // Validate the ip
        if (Ip.isIpValid(transaction.getIp()) == false) {
            return false;
        }

        // Validate the credit card number
        if (StolenCreditCard.isCardNumberValid(transaction.getNumber()) == false) {
            return false;
        }

        // Validate the region
        List<RegionCode> regionCodes = List.of(RegionCode.values());
        if (regionCodes.contains(transaction.getRegion()) == false) {
            return false;
        }

        // Validate time
        String TIME_REGEX =
                "^(\\d{4})-(\\d\\d)-(\\d\\d)T(\\d\\d):(\\d\\d):(\\d\\d)Z$";

        Pattern TIME_PATTERN = Pattern.compile(TIME_REGEX);
        Matcher matcher = TIME_PATTERN.matcher(transaction.getDate().toString());
        if (matcher.matches() == false) {
            return false;
        }

        return true;
    }


    // This method validates the Transaction following a Rule-based system
    public ResponseEntity<String> transactionRulesEngine(Transaction transaction) {
        // Find all previous transactions within the last hour
        // Create two Sets with IPs and Regions
        List<Transaction> queryList = transactionRepo.findAllByNumber(transaction.getNumber());
        Set<RegionCode> regionsInQuery = new HashSet<>();
        Set<String> ipsInQuery = new HashSet<>();

        if (queryList.size() > 0 ) {
            Instant latestTime = transaction.getDate();
            Instant minusOneHour = latestTime.minus(1, ChronoUnit.HOURS);

            for (Transaction item : queryList) {
                boolean withinLastOneHourAgo = item.getDate().isAfter(minusOneHour) && item.getDate().isBefore(latestTime) ;
                if (withinLastOneHourAgo == true) {
                    regionsInQuery.add(item.getRegion());
                    ipsInQuery.add(item.getIp());
                    if (ipsInQuery.contains(transaction.getIp())) {
                        ipsInQuery.remove(transaction.getIp());
                    }
                    if (regionsInQuery.contains(transaction.getRegion())) {
                        regionsInQuery.remove(transaction.getRegion());
                    }
                }
            }
        }

        // PROHIBITED scenarios

        if (ipRepo.existsByIp(transaction.getIp()) == true) {
            transaction.setResult(TransactionResult.PROHIBITED);
            transaction.getInfo().add("ip");
            transactionRepo.save(transaction);
        }

        if (stolenCreditCardRepo.existsByNumber(transaction.getNumber()) == true) {
            transaction.setResult(TransactionResult.PROHIBITED);
            transaction.getInfo().add("card-number");
            transactionRepo.save(transaction);

        }

        if (transaction.getAmount() > 1500) {
            transaction.setResult(TransactionResult.PROHIBITED);
            transaction.getInfo().add("amount");
            transactionRepo.save(transaction);
        }

        if (regionsInQuery.size() > 2) {
            transaction.setResult(TransactionResult.PROHIBITED);
            transaction.getInfo().add("region-correlation");
            transactionRepo.save(transaction);
        }

        if (ipsInQuery.size() > 2) {
            transaction.setResult(TransactionResult.PROHIBITED);
            transaction.getInfo().add("ip-correlation");
            transactionRepo.save(transaction);
        }


        if (transaction.getResult() == TransactionResult.PROHIBITED) {
            regionsInQuery.clear();
            ipsInQuery.clear();
            return new ResponseEntity(Map.of("result", transaction.getResult(), "info", String.join(", ", transaction.getInfo())), HttpStatus.OK);
        }

        // MANUAL REVIEW scenarios

        if (transaction.getAmount() > 200 && transaction.getAmount() <= 1500) {
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            transaction.getInfo().add("amount");
            transactionRepo.save(transaction);
        }

        if (regionsInQuery.size() == 2) {
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            transaction.getInfo().add("region-correlation");
            transactionRepo.save(transaction);
        }

        if (ipsInQuery.size() == 2) {
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            transaction.getInfo().add("ip-correlation");
            transactionRepo.save(transaction);
        }


        if (transaction.getResult() == TransactionResult.MANUAL_PROCESSING) {
            regionsInQuery.clear();
            ipsInQuery.clear();
            return new ResponseEntity(Map.of("result", transaction.getResult(), "info", String.join(", ", transaction.getInfo())), HttpStatus.OK);
        }

        // ALLOWED SCENARIOS
        if (transaction.getAmount() > 0 && transaction.getAmount() <= 200) {
            transaction.setResult(TransactionResult.ALLOWED);
            transaction.getInfo().clear();
            transaction.getInfo().add("none");
            transactionRepo.save(transaction);
        }

        regionsInQuery.clear();
        ipsInQuery.clear();

        return new ResponseEntity(Map.of("result", transaction.getResult(), "info", String.join(", ", transaction.getInfo())), HttpStatus.OK);
    }
}
