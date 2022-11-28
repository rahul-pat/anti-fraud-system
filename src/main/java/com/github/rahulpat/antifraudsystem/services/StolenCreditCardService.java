package com.github.rahulpat.antifraudsystem.services;

import com.github.rahulpat.antifraudsystem.entities.StolenCreditCard;
import com.github.rahulpat.antifraudsystem.repository.StolenCreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StolenCreditCardService {

    @Autowired
    StolenCreditCardRepository stolenCreditCardRepo;

    public ResponseEntity<String> addStolenCard(StolenCreditCard stolenCreditCard) {

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

    public ResponseEntity<String> deleteStolenCreditCard(String number) {

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

    public ResponseEntity<String> getAllStolenCreditCard() {
        List<StolenCreditCard> allStolenCreditCard = stolenCreditCardRepo.findAll();
        return new ResponseEntity(allStolenCreditCard, HttpStatus.OK);
    }
}
