package com.ic.myshop.db;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.model.Cart;
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
        String userId = user.getId();
        firebaseFirestore.collection(DatabaseConstant.USERS).document(userId).set(user);
        createCart(userId);
    }

    public void createCart(String userId) {
        String id = getCartId(userId);
        Cart cart = new Cart(id, userId);
        firebaseFirestore.collection(DatabaseConstant.CARTS).document(id).set(cart);
    }

    public StorageReference getStorageReference(String uri) {
        return firebaseStorage.getReference(DatabaseConstant.IMAGES).child(System.currentTimeMillis()
                + "." + uri);
    }

    public void addProduct(Product product) {
        firebaseFirestore.collection(DatabaseConstant.PRODUCTS).add(product);
    }

    public void addToCart(String productId, int quantity) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.CARTS).document(getCartId(getUserId()));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Cart cart = documentSnapshot.toObject(Cart.class);
                    cart.getQuantityProducts().put(productId, quantity);
                    documentReference.set(cart);
                }
            }
        });
    }

    public String getCartId(String userId) {
        return "cart_" + userId;
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }
}
