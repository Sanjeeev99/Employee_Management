package com.example.employee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import org.postgresql.ds.PGSimpleDataSource;

@Configuration
public class RagDatabaseConfig {

    @Value("${rag.datasource.url}")
    private String url;

    @Value("${rag.datasource.username}")
    private String username;

    @Value("${rag.datasource.password}")
    private String password;

    @Bean
    public JdbcTemplate ragJdbcTemplate() {

        PGSimpleDataSource dataSource =
                new PGSimpleDataSource();

        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        return new JdbcTemplate(dataSource);
    }
}