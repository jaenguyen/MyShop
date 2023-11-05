package com.ic.myshop.db;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.Cart;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;
import com.ic.myshop.output.BuyItem;

import java.util.List;

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

    public void updatePhoneUser(String id, String phone) {
        firebaseFirestore.collection(DatabaseConstant.USERS).document(id).update("phone", phone);
    }

    public void updateAvatarUser(String id, String avatar) {
        firebaseFirestore.collection(DatabaseConstant.USERS).document(id).update("avatar", avatar);
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
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document();
        product.setId(documentReference.getId());
        documentReference.set(product);
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

    public void updateCart(String productId, int quantity) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.CARTS).document(getCartId(getUserId()));
        documentReference.update(String.format("quantityProducts.%s", productId), quantity);
    }

    public void deleteCart(String productId) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.CARTS).document(getCartId(getUserId()));
        documentReference.update(String.format("quantityProducts.%s", productId), FieldValue.delete());
    }

    public String getCartId(String userId) {
        return "cart_" + userId;
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public void updateAddresses(User user) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.USERS).document(getUserId());
        documentReference.update("addresses", user.getAddresses());
    }

    public void createOrder(BuyItem buyItem) {
        Order order = new Order(buyItem.getId(), buyItem.getQuantity(), buyItem.getPrice() * buyItem.getQuantity(), getUserId(), buyItem.getParentId());
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.ORDERS).document();
        order.setId(documentReference.getId());
        documentReference.set(order);
    }

    public void updateSellNumber(BuyItem buyItem) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(buyItem.getId());
        documentReference.update("sellNumber", FieldValue.increment(buyItem.getQuantity() * -1));
        // TODO: đánh giá cập nhập luôn số lượng bán
    }

    public void buyProduct(BuyItem buyItem) {
        deleteCart(buyItem.getId());
        createOrder(buyItem);
        updateSellNumber(buyItem);
    }
}
