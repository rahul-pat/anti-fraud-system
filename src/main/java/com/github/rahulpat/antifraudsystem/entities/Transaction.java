package com.github.rahulpat.antifraudsystem.entities;

import com.github.rahulpat.antifraudsystem.controller.AntiFraudController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    @Column
    private long amount;
    @Column
    private String ip;
    @Column
    private String number;
    @Column
    private RegionCode region;
    @Column
    private Instant date;
    @Column
    private TransactionResult result;
    @Transient
    private Set<String> info = new HashSet<>();


    // No arg constructor
    public Transaction() {};


    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public RegionCode getRegion() {
        return region;
    }

    public void setRegion(RegionCode region) {
        this.region = region;
    }

    public Instant getDate() {
        return date;
    }

    // Updated to accept the format in request JSON payload ("yyyy-MM-ddTHH:mm:ss")
    public void setDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            this.date = sdf.parse(date).toInstant();
        } catch (ParseException e) {
            this.date = null;
        }
    }

    public TransactionResult getResult() {
        return result;
    }

    public void setResult(TransactionResult result) {
        this.result = result;
    }

    public Set<String> getInfo() {
        return info;
    }

    public void setInfo(Set<String> info) {
        this.info = info;
    }


}
