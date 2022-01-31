package com.securutyExamples.application.configs;

import com.securutyExamples.application.service.MainService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

@Configuration
@AllArgsConstructor
public class WebFluxConfiguration extends WebFluxConfigurationSupport {

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new MainService();
    }

}
