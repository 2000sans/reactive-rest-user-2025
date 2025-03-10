package backend.controller;

import backend.model.User;
import backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping
    public Flux<User> getUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public Mono<User> getUser(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

    @GetMapping("/user_type/{userType}")
    public Flux<User> findUserUsingUserType(@PathVariable String userType) {
        return userService.getUserByUserType(userType);
    }

    @GetMapping("/user_name/{userName}")
    public Mono<User> findUserUsingUserName(@PathVariable String userName) {
        return userService.getUserByUserName(userName);
    }

    @PutMapping
    public Mono<String> modifyUser(@RequestBody User user) {
        return userService.updateUserDetails(user);
    }

    @DeleteMapping("/{userId}")
    public Mono<String> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }
}
