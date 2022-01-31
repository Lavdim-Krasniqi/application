package com.securutyExamples.application.service;

import com.securutyExamples.application.constants.Constants;
import com.securutyExamples.application.entity.User;
import com.securutyExamples.application.entity.UserDto;
import com.securutyExamples.application.entity.UserLoginDto;
import com.securutyExamples.application.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repo;

    public Mono<Void> registerUser(UserDto user, ServerWebExchange exchange) {
        User user1 = new User();
        user1.setName(user.getName());
        user1.setUsername(user.getUsername());
        user1.setSurname(user.getSurname());
        user1.setEmail(user.getEmail());
        user1.setPassword(user.getPassword());
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(user.getUsername());
        userLoginDto.setPassword(user.getPassword());
        return repo.registerUser(user1)
                .flatMap(user2 -> generateToken(exchange, userLoginDto));
    }

    public Mono<User> findByPasswordAndUsername(UserLoginDto userLoginDto) {
        return repo.findByPasswordAndUsername(userLoginDto);
    }

    //the part of generation a hashed token is not implemented
    public Mono<Void> generateToken(ServerWebExchange exchange, UserLoginDto userLoginDto) {
        ResponseCookie authentication =
                ResponseCookie.from(Constants.Authentication.name(), userLoginDto.getUsername())
                        .maxAge(60*60*24).build();
        exchange.getResponse().addCookie(authentication);

        ResponseCookie authentication1 =
                ResponseCookie.from(Constants.Username.name(), userLoginDto.getUsername())
                        .maxAge(60*60*24).build();
        exchange.getResponse().addCookie(authentication1);
        return repo.addOrUpdateToken(userLoginDto.getUsername(), userLoginDto.getUsername());
    }

    public Mono<String> findTokenByUsername(String username) {
        return repo.findTokenByUsername(username);
    }

    public Mono<User> findUserByUsername(String username) {
        return repo.findUserByUsername(username);
    }

    public Mono<Void> addOrUpdateToken(String username, String token) {
        return repo.addOrUpdateToken(username, token);
    }
}
