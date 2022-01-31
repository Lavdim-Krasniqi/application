package com.securutyExamples.application.auth.authentication;

import com.securutyExamples.application.auth.AuthFilter;
import com.securutyExamples.application.auth.annotations.NotAuthenticate;
import com.securutyExamples.application.exceptions.UnauthorizedException;
import com.securutyExamples.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthenticationFilter implements AuthFilter {
    @Autowired
    UserService userService;

    @Override
    public Mono<HandlerMethod> filter(ServerWebExchange serverWebExchange, HandlerMethod handlerMethod) {

        if (handlerMethod.hasMethodAnnotation(NotAuthenticate.class)) {
            return Mono.just(handlerMethod);
        } else {
            try {
                String authentication = Objects.requireNonNull(serverWebExchange.getRequest()
                        .getCookies().getFirst("Authentication")).getValue();
                String username = Objects.requireNonNull(serverWebExchange.getRequest()
                        .getCookies().getFirst("Username")).getValue();
                return userService.findTokenByUsername(username)
                        .flatMap(s -> {
                            if (s.equals(authentication) && !s.equals("")) return Mono.just(handlerMethod);
                            else return Mono.error(new UnauthorizedException("Missing Authentication " +
                                    "and Username cookie"));
                        });
            } catch (NullPointerException e) {
                return Mono.error(new UnauthorizedException("Missing Authentication " +
                        "and Username cookie"));
            }

        }
    }
}

