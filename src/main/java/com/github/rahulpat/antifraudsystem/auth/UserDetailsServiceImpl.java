package com.github.rahulpat.antifraudsystem.auth;

import com.github.rahulpat.antifraudsystem.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// This class is used to retrieve data from the user store.
// A username is pass into loadUserByUsername (this happens when a user attempts to authenticate)
// A user from this username is retrieved from the H2 database using Spring Data JPA findByUsername()
// The User is then passed into UserDetailsImpl which turns this into a type Spring Security can recognize
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }

        return new UserDetailsImpl(user);
    }
}
