package com.securutyExamples.application.auth;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface AuthFilter {

     Mono<HandlerMethod> filter(ServerWebExchange serverWebExchange, HandlerMethod handlerMethod);
}
