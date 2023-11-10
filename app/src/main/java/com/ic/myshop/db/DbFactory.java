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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.Cart;
import com.ic.myshop.model.Like;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;
import com.ic.myshop.output.BuyItem;

import java.util.HashMap;
import java.util.Map;

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

    /*
        User
     */
    public void addUser(User user) {
        String userId = user.getId();
        firebaseFirestore.collection(DatabaseConstant.USERS).document(userId).set(user);
        createCart(userId);
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return firebaseFirestore.collection(DatabaseConstant.USERS).document(userId).get();
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public void updateAddresses(User user) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.USERS).document(getUserId());
        documentReference.update("addresses", user.getAddresses());
    }

    public void updatePhoneUser(String id, String phone) {
        firebaseFirestore.collection(DatabaseConstant.USERS).document(id).update("phone", phone);
    }

    public void updateAvatarUser(String id, String avatar) {
        firebaseFirestore.collection(DatabaseConstant.USERS).document(id).update("avatar", avatar);
    }

    /*
        Images
     */
    public StorageReference getStorageReference(String uri) {
        return firebaseStorage.getReference(DatabaseConstant.IMAGES).child(System.currentTimeMillis()
                + "." + uri);
    }

    /*
        Product
     */
    public void addProduct(Product product) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document();
        product.setId(documentReference.getId());
        documentReference.set(product);
    }

    public Task<DocumentSnapshot> getProduct(String productId) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(productId).get();
    }

    public void createCart(String userId) {
        String id = getCartId(userId);
        Cart cart = new Cart(id, userId);
        firebaseFirestore.collection(DatabaseConstant.CARTS).document(id).set(cart);
    }

    public void updateSellNumber(BuyItem buyItem) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(buyItem.getId());
        documentReference.update("sellNumber", FieldValue.increment(buyItem.getQuantity() * -1));
        // TODO: đánh giá cập nhập luôn số lượng bán
    }

    public Task<QuerySnapshot> getListProduct(String type, String field, long from, int limit) {
        if (type != null)
            return getListProductByType(type, from, limit);
        if (field != null && !field.equals(InputParam.CREATED_TIME)) {
            return getListProductByField(field, from, limit);
        }
        return getProductsDefault(from, limit);
    }

    public Task<QuerySnapshot> getListProductByType(String type, long from, int limit) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS)
                .whereEqualTo(InputParam.TYPE, type)
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .startAfter(from).limit(limit).get();
    }

    public Task<QuerySnapshot> getListProductByField(String field, long from, int limit) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS)
                .orderBy(field, Query.Direction.DESCENDING)
                .startAt(from).limit(limit).get();
    }

    public Task<QuerySnapshot> getProductsDefault(long from, int limit) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS)
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .startAfter(from).limit(limit).get();
    }

    public Task<QuerySnapshot> getProductsSelfDefault(long from, int limit) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS)
                .whereEqualTo(InputParam.PARENT_ID, dbFactory.getUserId())
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .startAfter(from).limit(limit).get();
    }

    /*
        Cart
     */
    public void addToCart(String productId, int quantity) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.CARTS).document(getCartId(getUserId()));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Cart cart = documentSnapshot.toObject(Cart.class);
                    int currentQuantity = 0;
                    if (cart.getQuantityProducts().containsKey(productId)) {
                        currentQuantity = cart.getQuantityProducts().get(productId);
                    }
                    cart.getQuantityProducts().put(productId, quantity + currentQuantity);
                    documentReference.set(cart);
                }
            }
        });
    }

    public Task<DocumentSnapshot> getCart(String userId) {
        return firebaseFirestore.collection(DatabaseConstant.CARTS).document(getCartId(userId)).get();
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

    /*
        Order
     */
    public void buyProduct(BuyItem buyItem, Address address) {
        deleteCart(buyItem.getId());
        createOrder(buyItem, address);
        updateSellNumber(buyItem);
    }

    public void createOrder(BuyItem buyItem, Address address) {
        Order order = new Order(buyItem.getId(), buyItem.getQuantity(), buyItem.getPrice(),
                buyItem.getPrice() * buyItem.getQuantity(), getUserId(), buyItem.getParentId(), address);
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.ORDERS).document();
        order.setId(documentReference.getId());
        documentReference.set(order);
    }

    /*
        Like
     */
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
                                WriteBatch batch = firebaseFirestore.batch();
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

    public void updateStatusOrder(String id, int status) {
        Map<String, Object> data = new HashMap<>();
        data.put(InputParam.STATUS, status);
        data.put(InputParam.UPDATED_TIME, System.currentTimeMillis());
        firebaseFirestore.collection(DatabaseConstant.ORDERS).document(id).update(data);
    }
}
