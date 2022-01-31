package com.securutyExamples.application.service;

import com.securutyExamples.application.auth.authentication.AuthenticationFilter;
import com.securutyExamples.application.auth.authorization.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class MainService extends RequestMappingHandlerMapping {

    @Autowired
    AuthenticationFilter authenticationFilter;
    @Autowired
    AuthorizationFilter authorizationFilter;

    @Override
    public Mono<HandlerMethod> getHandlerInternal(ServerWebExchange exchange) {
        return super.getHandlerInternal(exchange)
                .flatMap(handlerMethod -> authenticationFilter.filter(exchange, handlerMethod))
                .flatMap(handlerMethod -> authorizationFilter.filter(exchange, handlerMethod));
    }


}