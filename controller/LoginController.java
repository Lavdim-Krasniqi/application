package com.securutyExamples.application.controller;

import com.securutyExamples.application.auth.annotations.Authorized;
import com.securutyExamples.application.auth.annotations.NotAuthenticate;
import com.securutyExamples.application.constants.Constants;
import com.securutyExamples.application.entity.UserDto;
import com.securutyExamples.application.entity.UserLoginDto;
import com.securutyExamples.application.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {
    private final UserService service;

    @PostMapping("/test1")
    @NotAuthenticate
    public Mono<String> getString() {
        return Mono.just("Metoda qe e permban annotation NotAuthenticate");
    }

    @PostMapping("/test2")
    @Authorized({"User", "Admin"})
    public Mono<String> getString2() {
        return Mono.just("Metoda qe nuk e permban annotation NotAuthenticate");
    }

    @PostMapping("/test3")
    @NotAuthenticate
    public Mono<Void> logIn(@RequestBody UserLoginDto userLoginDto, ServerWebExchange exchange) {
        return service.findByPasswordAndUsername(userLoginDto)
                .flatMap(user -> service.generateToken(exchange, userLoginDto));
    }

    @PostMapping("/test4")
    @NotAuthenticate
    public Mono<Void> registerUser(@RequestBody UserDto user, ServerWebExchange exchange) {
        return service.registerUser(user, exchange);
    }

    @PostMapping("/test5")
    @Authorized("admin")
    public Mono<String> getString3() {
        return Mono.just("metoda qe ka rolin admin");
    }

    @PostMapping("/test6")
    @Authorized()
    public Mono<String> getString4() {
        return Mono.just("metoda qe nuk ka role");
    }

    @PostMapping("/test7")
    public Mono<String> getString5() {
        return Mono.just("metoda qe nuk ka Authorized annotation");
    }

    @RequestMapping("/logOut/{username}")
    @Authorized({"User", "Admin"})
    public Mono<Void> logOut(@PathVariable String username, ServerWebExchange exchange) {
        return service.addOrUpdateToken(username, "")
                .switchIfEmpty(Mono.fromRunnable(() -> {
                    ResponseCookie authentication =
                            ResponseCookie.from(Constants.Authentication.name(), "")
                                    .maxAge(0).build();
                    exchange.getResponse().addCookie(authentication);
                    ResponseCookie username1 =
                            ResponseCookie.from(Constants.Username.name(), "")
                                    .maxAge(0).build();
                    exchange.getResponse().addCookie(username1);
                }));
    }
}
