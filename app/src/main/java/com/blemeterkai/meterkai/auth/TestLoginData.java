package com.blemeterkai.meterkai.auth; // Or your preferred package

import java.util.ArrayList;
import java.util.List;

public class TestLoginData {

    // You could make LoginCredentials a public class in its own file too
    public static class LoginCredentials {
        public String username;
        public String password;
        public String authenticateLevel;

        public LoginCredentials(String username, String password, String authenticateLevel) {
            this.username = username;
            this.password = password;
            this.authenticateLevel = authenticateLevel;
        }
    }

    public static List<LoginCredentials> getUsers() {
        List<LoginCredentials> users = new ArrayList<>();
        users.add(new LoginCredentials("Super", "BleMeter", "0"));
        users.add(new LoginCredentials("Admin", "Admin", "1"));
        users.add(new LoginCredentials("Reader", "Reader", "3"));
        return users;
    }
}