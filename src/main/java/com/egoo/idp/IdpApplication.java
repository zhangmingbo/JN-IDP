package com.egoo.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class IdpApplication {

    public static void main(String[] args) {

        SpringApplication.run(IdpApplication.class, args);

    }

}
