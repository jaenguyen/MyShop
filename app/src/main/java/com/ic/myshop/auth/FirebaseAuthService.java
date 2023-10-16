package com.ic.myshop.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthService {

    private FirebaseAuth fbAuth;

    public FirebaseAuthService() {
        this.fbAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getUser() {
        return fbAuth.getCurrentUser();
    }

    public boolean isLogin() {
        return fbAuth.getCurrentUser() == null ? false : true;
    }
}
