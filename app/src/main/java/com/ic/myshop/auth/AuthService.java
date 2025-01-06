package com.ic.myshop.auth;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthService {

    private static final AuthService firebaseAuthService = new AuthService();
    private FirebaseAuth firebaseAuth;

    private AuthService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static AuthService getInstance() {
        return firebaseAuthService;
    }

    public boolean isLogin() {
        return firebaseAuth.getCurrentUser() == null ? false : true;
    }

    public Task<AuthResult> createUser(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<Void> resetPassword(String email) {
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public String getUserId() {
        return firebaseAuth.getUid();
    }
}
