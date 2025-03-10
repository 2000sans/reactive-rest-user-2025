package backend.repository;

import backend.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Flux<User> findByUserType(String userType);

    @Query("{ 'userName': { '$regex': ?0, '$options': 'i' } }")
    Mono<User> findByUserNameIgnoreCase(String userName);
}
