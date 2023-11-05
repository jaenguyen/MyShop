package com.ic.myshop.db;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.model.Cart;
import com.ic.myshop.model.Like;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;
import com.ic.myshop.output.BuyItem;

public class DbFactory {

    private static final DbFactory dbFactory = new DbFactory();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    WriteBatch batch;

    private DbFactory() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        batch = firebaseFirestore.batch();
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

    public void updateLikeProduct(String userId, String productId, boolean isLiked) {
        CollectionReference collectionReference = firebaseFirestore.collection(DatabaseConstant.LIKES);
        if (isLiked) {
            collectionReference
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("productId", productId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    batch.delete(document.getReference());
                                }
                                // Thực hiện xóa các tài liệu trong batched write
                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Xóa thành công
                                        } else {
                                            // Xóa thất bại
                                        }
                                    }
                                });
                            } else {
                                // Lỗi khi lấy dữ liệu
                            }
                        }
                    });
            firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(productId)
                    .update(DatabaseConstant.LIKES, FieldValue.increment(-1));
        } else {
            collectionReference.document().set(new Like(userId, productId, System.currentTimeMillis()));
            firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(productId)
                    .update(DatabaseConstant.LIKES, FieldValue.increment(1));
        }
    }
}
