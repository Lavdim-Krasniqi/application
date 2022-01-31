package com.securutyExamples.application.repositories;

import com.securutyExamples.application.constants.UserRoles;
import com.securutyExamples.application.entity.User;
import com.securutyExamples.application.entity.UserLoginDto;
import com.securutyExamples.application.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepository {
    private final ReactiveMongoTemplate repo;

    //part of generating hashed token is not implemented
    public Mono<User> registerUser(User user) {
        List<String> roles = new ArrayList<>();
        roles.add(UserRoles.User.name());
        user.setRoles(roles);
        user.setToken("");
        val c1 = Criteria.where(User.USERNAME).is(user.getUsername());
        val c2 = Criteria.where(User.EMAIL).is(user.getEmail());
        val c3 = Criteria.where(User.PASSWORD).is(user.getPassword());
        val c4 = new Criteria().orOperator(c1, c2, c3);
        return repo.exists(Query.query(c4), User.class)
                .flatMap(aBoolean ->
                {
                    if (!aBoolean) return repo.insert(user);
                    else return Mono.error(new BadRequestException("User details like username " +
                            "email or password may be taken by others"));
                });

    }

    public Mono<User> findByPasswordAndUsername(UserLoginDto userLoginDto) {
        val c1 = Criteria.where(User.USERNAME).is(userLoginDto.getUsername())
                .and(User.PASSWORD).is(userLoginDto.getPassword());

        return repo.findOne(Query.query(c1), User.class)
                .switchIfEmpty(Mono.error(new BadRequestException("Your username and password are " +
                        "incorrect")));
    }

    public Mono<Void> addOrUpdateToken(String username, String token) {
        val c1 = Criteria.where(User.USERNAME).is(username);
        Update update = new Update();
        update.set("token", token);
        return repo.updateFirst(Query.query(c1), update, User.class)
                .flatMap(updateResult -> Mono.empty());
    }

    public Mono<String> findTokenByUsername(String username) {
        val c1 = Criteria.where(User.USERNAME).is(username);
        return repo.findOne(Query.query(c1), User.class)
                .flatMap(user -> Mono.just(user.getToken()));
    }
    public Mono<User> findUserByUsername(String username){
        val c1 = Criteria.where(User.USERNAME).is(username);
        return repo.findOne(Query.query(c1),User.class);
    }
}
