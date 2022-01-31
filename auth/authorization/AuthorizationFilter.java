package com.securutyExamples.application.auth.authorization;

import com.securutyExamples.application.auth.AuthFilter;
import com.securutyExamples.application.auth.annotations.Authorized;
import com.securutyExamples.application.auth.annotations.NotAuthenticate;
import com.securutyExamples.application.constants.Constants;
import com.securutyExamples.application.exceptions.UnauthorizedException;
import com.securutyExamples.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class AuthorizationFilter implements AuthFilter {
    @Autowired
    UserService service;

    @Override
    public Mono<HandlerMethod> filter(ServerWebExchange serverWebExchange, HandlerMethod handlerMethod) {

        if (handlerMethod.hasMethodAnnotation(Authorized.class)) {
            String[] roles = handlerMethod.getMethod()
                    .getAnnotation(Authorized.class).value();
            try {
                String username = Objects.requireNonNull(serverWebExchange.getRequest()
                        .getCookies().getFirst(Constants.Username.name())).getValue();
                return service.findUserByUsername(username)
                        .flatMap(user -> {
                            if (Arrays.stream(roles).anyMatch(s -> user.getRoles().contains(s))) {
                                return Mono.just(handlerMethod);
                            } else return Mono.error(
                                    new UnauthorizedException("This endpoint has not access for you"));
                        });
            } catch (NullPointerException e) {
                return Mono.error(new UnauthorizedException("This endpoint has not access for you"));
            }
        } else {
            if(handlerMethod.hasMethodAnnotation(NotAuthenticate.class)){
                return Mono.just(handlerMethod);
            }
            else {
                return Mono.error(new UnauthorizedException("This endpoint has not access for you"));
            }
        }



//            return Mono.just(handlerMethod);

    }
}
