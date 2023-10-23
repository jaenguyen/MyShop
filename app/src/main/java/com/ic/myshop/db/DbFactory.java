package com.ic.myshop.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ic.myshop.model.User;

public class DbFactory {

    private static final DbFactory dbFactory = new DbFactory();
    private FirebaseFirestore firebaseFirestore;

    private DbFactory() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static DbFactory getInstance() {
        return dbFactory;
    }

    public void addUser(User user) {
        firebaseFirestore.collection("users").document(user.getId()).set(user);
    }
}
