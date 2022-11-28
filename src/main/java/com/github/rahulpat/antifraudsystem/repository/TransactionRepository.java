package com.github.rahulpat.antifraudsystem.repository;

import com.github.rahulpat.antifraudsystem.entities.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public List<Transaction> findAllByNumber(String number);
}
