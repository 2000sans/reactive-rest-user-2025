package backend.service;


import backend.externalfunctionalities.RandomIdGenerator;
import backend.model.User;
import backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // CRUD CREATE READ UPDATE DELETE

    public Mono<String> addUser(User user) {
        RandomIdGenerator obj = new RandomIdGenerator();
        String generatedUserId = obj.getGeneratedId(user.getUserType()).toString();

        // Check if the username already exists (case-insensitive)
        return userRepository.findByUserNameIgnoreCase(user.getUserName())
                .flatMap(existingUser -> {
                    // If a user with the same username exists, return a message
                    return Mono.just("{\"message\": \"Username already exists. Please choose a different username.\"}");
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // Proceed to add the user if the username does not exist
                    user.setUserId(generatedUserId);
                    user.setUserLoginState("signed-in");
                    user.setUserName(user.getUserName().toLowerCase());

                    if (user.getUserPassword() == null) {
                        user.setUserPassword(UUID.randomUUID().toString());
                    }

                    user.setUserName(user.getUserName().toLowerCase());

                    user.setUserHref("http://localhost:8080/users");
                    user.setUserSelf("http://localhost:8080/users/" + generatedUserId);

                    // Save the user and convert to JSON
                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    // Convert saved User object to JSON
                                    String jsonResponse = objectMapper.writeValueAsString(savedUser);
                                    return Mono.just(jsonResponse);
                                } catch (JsonProcessingException e) {
                                    return Mono.just("{\"error\": {\"message\": \"Error converting user to JSON\", \"details\": \"" + e.getLocalizedMessage() + "\"}}");
                                }
                            });
                }));
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserByUserId(String userId) {
        return userRepository.findById(userId);
    }

    public Flux<User> getUserByUserType(String userType) {
        return userRepository.findByUserType(userType);
    }

    public Mono<User> getUserByUserName(String userName) {
        return userRepository.findByUserNameIgnoreCase(userName);
    }

    public Mono<String> updateUserDetails(User userRequest) {
        return userRepository.findById(userRequest.getUserId())
                .flatMap(existingUser -> {
                    // Check if the new username already exists (case-insensitive)
                    return userRepository.findByUserNameIgnoreCase(userRequest.getUserName())
                            .filter(user -> !user.getUserId().equals(existingUser.getUserId())) // Ensure it's not the same user
                            .flatMap(conflictingUser -> {
                                // If a user with the same username exists, return a message
                                return Mono.just("{\"message\": \"Username already exists. Please choose a different username.\"}");
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // Update user details if no conflict
                                if (userRequest.getUserName() != null) {
                                    existingUser.setUserName(userRequest.getUserName());
                                }

                                if (userRequest.getUserPassword() != null) {
                                    existingUser.setUserPassword(userRequest.getUserPassword());
                                }

                                // Save the updated user and convert to JSON
                                return userRepository.save(existingUser)
                                        .flatMap(savedUser -> {
                                            ObjectMapper objectMapper = new ObjectMapper();
                                            try {
                                                // Convert saved User object to JSON
                                                String jsonResponse = objectMapper.writeValueAsString(savedUser);
                                                return Mono.just(jsonResponse);
                                            } catch (JsonProcessingException e) {
                                                return Mono.just("{\"error\": {\"message\": \"Error converting updated user to JSON\", \"details\": \"" + e.getLocalizedMessage() + "\"}}");
                                            }
                                        });
                            }));
                })
                .switchIfEmpty(Mono.just("{\"message\": \"The user with userId '" + userRequest.getUserId() + "' was not found in the database\"}"));
    }

    public Mono<String> deleteUser(String userId) {
        return userRepository.deleteById(userId)
                .then(Mono.just("{\"message\": \"The user which has userId '" + userId + "' was deleted from the database\"}"));
    }
}
