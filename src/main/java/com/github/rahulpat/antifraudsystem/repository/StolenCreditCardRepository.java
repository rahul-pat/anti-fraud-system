package com.github.rahulpat.antifraudsystem.repository;

import com.github.rahulpat.antifraudsystem.entities.StolenCreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StolenCreditCardRepository extends JpaRepository<StolenCreditCard, Long> {

    public StolenCreditCard findByNumber(String number);

    public boolean existsByNumber(String number);
}
