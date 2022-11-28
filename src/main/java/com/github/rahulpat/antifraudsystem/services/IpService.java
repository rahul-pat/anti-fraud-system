package com.github.rahulpat.antifraudsystem.services;

import com.github.rahulpat.antifraudsystem.entities.Ip;
import com.github.rahulpat.antifraudsystem.repository.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IpService {

    @Autowired
    IpRepository ipRepo;

    public ResponseEntity<String> addSuspiciousIp(Ip ip) {

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

    public ResponseEntity<String> deleteSuspiciousIp(String ip) {

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

    public ResponseEntity<String> getAllSuspiciousIp() {
        List<Ip> allSuspiciousIp = ipRepo.findAll();
        return new ResponseEntity(allSuspiciousIp, HttpStatus.OK);
    }


}
