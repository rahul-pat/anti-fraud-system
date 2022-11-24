package com.github.rahulpat.antifraudsystem.controller;

import com.github.rahulpat.antifraudsystem.auth.UserRepository;
import com.github.rahulpat.antifraudsystem.entities.User;
import com.github.rahulpat.antifraudsystem.entities.UserWithoutPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RegistrationController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    // This endpoint is available to unauthenticated users. It is used to register new Users.
    // New Users are added to an in-memory database (H2) using Spring Data JPA and Hibernate
    @PostMapping("/api/auth/user")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        // Simple data validation
        // TODO: look to improve the data validation using existing Spring capabilities
        if (user.getName() == null || user.getPassword() == null || user.getUsername() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Set username to lowercase since they should be stored in the DB without case sensitivity
        user.setUsername(user.getUsername().toLowerCase());

        // Validate if the username already exists. If not, add the user to the database
        if (userRepo.findByUsername(user.getUsername()) == null) {

            // Encode the password using BCryptPasswordEncoder
            // A bean is available of type PasswordEncoder from the WebSecurityImpl class
            user.setPassword(encoder.encode(user.getPassword()));

            // Add the User to the H2 database using the built-in save() method from Spring Data JPA
            // We Autowired a bean of UserRepository type which extends the JpaRepository class and provides us
            // the built-in Spring Data JPA methods such as save()
            userRepo.save(user);

            // Retrieve the newly added User from the H2 database and store the object into variable newUser
            User newUser = userRepo.findByUsername(user.getUsername());

            // newUser is used to return a ResponseEntity to the client with the User id (auto-generated),
            // name and username
            return new ResponseEntity(Map.of("id", newUser.getId(),
                                            "name", newUser.getName(),
                                            "username", newUser.getUsername()),
                    HttpStatus.CREATED);
        } else {

            // If the username already exists, return an HTTP CONFLICT status
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

    }

    // This endpoint is only available to authenticated users
    // It will return the list of all registered users with their id, name and username, but NOT
    // return the hashed password
    @GetMapping("/api/auth/list")
    public List<UserWithoutPassword> listUsers() {
        return userRepo.findAllBy();
    }

    // This endpoint is only available to authenticated users
    // It is used to delete a User from the H2 database
    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {

        // If the username is not found in the database, return HTTP NOT FOUND status
        if (userRepo.findByUsername(username) == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            // If the username is found, delete it from the database
            User userToDelete = userRepo.findByUsername(username);
            userRepo.deleteById(userToDelete.getId());
            return new ResponseEntity(Map.of("username", username,
                    "status", "Deleted successfully!"),
                    HttpStatus.OK);
        }
    }

}
