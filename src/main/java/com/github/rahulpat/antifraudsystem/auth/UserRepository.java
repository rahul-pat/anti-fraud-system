package com.github.rahulpat.antifraudsystem.auth;

import com.github.rahulpat.antifraudsystem.entities.User;
import com.github.rahulpat.antifraudsystem.entities.UserWithoutPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// This class is allows our Spring Boot application to create, read, update and delete data from a database using Spring Data JPA
@Repository
public interface UserRepository extends JpaRepository<User, Long>  {

    // This is a standard method offered by Spring Data JPA to find a User by username in the H2 database
    public User findByUsername(String username);

    // This method leverages Spring Data JPA and the UserWithoutPassword interface to return
    // all Users from the database without their hashed password
    public List<UserWithoutPassword> findAllBy();


    // If we were to store the Users in a thread-safe Map instead of an H2 database,
    // we could simply uncomment the code below, delete the code above
    // and remove the JpaRepository parent class
            /*

            private Map<String, User> users = new ConcurrentHashMap<>();

            @Autowired
            PasswordEncoder encoder;

            public User findByUsername(String username) {
                return users.get(username);
            }

            public ResponseEntity<String> save(User user) {

                if (findByUsername(user.getUsername()) == null) {
                    user.setPassword(encoder.encode(user.getPassword()));
                    users.put(user.getUsername(), user);
                    return new ResponseEntity(user, HttpStatus.CREATED);
                } else if (findByUsername(user.getUsername()) != null) {
                    return new ResponseEntity(HttpStatus.CONFLICT);
                } else {
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                }

            }

            */

}
