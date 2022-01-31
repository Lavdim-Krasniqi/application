package com.securutyExamples.application.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("UserDetails")
@CompoundIndex(def = "{'token':1}", unique = true, name = "token_Index")
@CompoundIndex(def = "{'username':1}", unique = true, name = "username_Index")
@CompoundIndex(def = "{'email':1}", unique = true, name = "email_Index")
public class User {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String EMAIL = "email";

    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;
    private String token;
    private List<String> roles;
}
