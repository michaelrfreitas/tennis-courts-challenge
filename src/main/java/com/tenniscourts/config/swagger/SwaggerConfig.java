package com.tenniscourts.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.tenniscourts"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(this.gInfo());
    }

    private ApiInfo gInfo(){
        return new ApiInfoBuilder()
        .title("Dell Challenge")
        .version("1.0.0")
        .contact(new Contact("Michael Freitas", "https://github.com/michaelrfreitas", "michael_rfreitas@hotmail.com"))
        .build();
    }
}
