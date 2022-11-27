package com.github.rahulpat.antifraudsystem.repository;

import com.github.rahulpat.antifraudsystem.entities.Ip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpRepository extends JpaRepository<Ip, Long> {

    public Ip findByIp(String ip);

    public boolean existsByIp(String ip);

}
