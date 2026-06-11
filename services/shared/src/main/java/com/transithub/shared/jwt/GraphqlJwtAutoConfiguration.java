package com.transithub.shared.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphqlJwtAutoConfiguration {

    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${jwt.secret:TransitHub_JWT_Min32Char_SecretKey_2024}") String secret) {
        return new JwtTokenProvider(secret);
    }

    @Bean
    public GraphqlJwtFilter graphqlJwtFilter(JwtTokenProvider tokenProvider) {
        return new GraphqlJwtFilter(tokenProvider);
    }

    @Bean
    public JwtGraphQLInstrumentation jwtGraphQLInstrumentation() {
        return new JwtGraphQLInstrumentation();
    }
}
