package com.ic.myshop.model;

import java.util.ArrayList;
import java.util.List;

public class Token {

    private List<String> tokens;

    public Token() {
    }

    public Token(String userId, List<String> tokens) {
        this.tokens = new ArrayList<>();
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void addToken(String token) {
        this.tokens.add(token);
    }
}
