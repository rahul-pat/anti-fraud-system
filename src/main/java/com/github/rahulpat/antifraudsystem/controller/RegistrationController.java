package com.github.rahulpat.antifraudsystem.controller;

import com.github.rahulpat.antifraudsystem.auth.UserRepository;
import com.github.rahulpat.antifraudsystem.entities.Operation;
import com.github.rahulpat.antifraudsystem.entities.Role;
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

            // Give the first User an ADMINISTRATOR role and all following Users a MERCHANT role
            // The ADMINISTRATOR account is not locked while all MERCHANT are locked initially
            // TODO: replace with .count() method
            if(userRepo.findAll().size() == 0) {
                user.setRole(Role.ADMINISTRATOR);
                user.setUserNotLocked(true);
            } else {
                user.setRole(Role.MERCHANT);
                user.setUserNotLocked(false);
            }

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
                                            "username", newUser.getUsername(),
                                            "role", newUser.getRole()),
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
    // This endpoint is only available to ADMINISTRATOR
    // It is used to update the role of a User
    @PutMapping("/api/auth/role")
    public ResponseEntity<String> changeUserRole(@RequestBody User user) {

        // If the username is not found in the database, return HTTP NOT FOUND status
        // If the Role is not SUPPORT or MERCHANT, return HTTP BAD REQUEST status
        if (userRepo.findByUsername(user.getUsername()) == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (user.getRole().toString() != Role.SUPPORT.toString() && user.getRole().toString() != Role.MERCHANT.toString()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Find the username in the database
        User userToUpdate = userRepo.findByUsername(user.getUsername());

        // If the Role to assign is the same as the existing role, return HTTP CONFLICT status
        if (userToUpdate.getRole() == user.getRole()) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        // Update the role and save to the database
        userToUpdate.setRole(user.getRole());
        userRepo.save(userToUpdate);

        return new ResponseEntity(Map.of("id", userToUpdate.getId(),
                "name", userToUpdate.getName(),
                "username", userToUpdate.getUsername(),
                "role", userToUpdate.getRole()),
                HttpStatus.OK);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<String> changeUserAccess(@RequestBody User user) {

        // If the username is not found in the database, return HTTP NOT FOUND status
        // For safety reasons, ADMINISTRATOR cannot be blocked
        if (userRepo.findByUsername(user.getUsername()) == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (user.getRole() == Role.ADMINISTRATOR) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Find the username in the database
        User userToUpdate = userRepo.findByUsername(user.getUsername());

        // Unlock or lock the User access
        if (user.getOperation() == Operation.LOCK) {
            userToUpdate.setOperation(Operation.LOCK);
            userToUpdate.setUserNotLocked(false);
            userRepo.save(userToUpdate);
        } else if (user.getOperation() == Operation.UNLOCK) {
            userToUpdate.setOperation(Operation.UNLOCK);
            userToUpdate.setUserNotLocked(true);
            userRepo.save(userToUpdate);
        } else {
            // Bad operation in the request
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(Map.of("status", "User " + user.getUsername() + " " + user.getOperation().toString().toLowerCase() + "ed!"),
                HttpStatus.OK);
    }

}
