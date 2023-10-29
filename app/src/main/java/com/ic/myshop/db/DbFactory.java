package com.ic.myshop.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;

public class DbFactory {

    private static final DbFactory dbFactory = new DbFactory();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    private DbFactory() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public static DbFactory getInstance() {
        return dbFactory;
    }

    public void addUser(User user) {
        firebaseFirestore.collection("users").document(user.getId()).set(user);
    }

    public StorageReference getStorageReference(String uri) {
        return firebaseStorage.getReference("images").child(System.currentTimeMillis()
                + "." + uri);
    }

    public void addProduct(Product product) {
        firebaseFirestore.collection("products").add(product);
    }
}
