package com.github.rahulpat.antifraudsystem.entities;

// This interface is used by Spring Data JPA to query back all registered users without the password column

public interface UserWithoutPassword {

    Long getId();

    String getName();

    String getUsername();

    Role getRole();

}
