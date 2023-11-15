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
import com.ic.myshop.constant.SortField;
import com.ic.myshop.constant.TypeProduct;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.Cart;
import com.ic.myshop.model.Like;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.Statistics;
import com.ic.myshop.model.User;
import com.ic.myshop.output.BuyItem;

import java.util.HashMap;
import java.util.Map;

public class DbFactory {

    private static final DbFactory dbFactory = new DbFactory();
    private static final SearchService searchService = SearchService.getInstance();
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

    public void updateFieldUser(String id, String field, String value) {
        firebaseFirestore.collection(DatabaseConstant.USERS).document(id).update(field, value);
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
        // add to search
        searchService.addProduct(product);
    }

    public Task<DocumentSnapshot> getProduct(String productId) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(productId).get();
    }

    public Task<QuerySnapshot> getAllProducts() {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS).get();
    }

    public void createCart(String userId) {
        String id = getCartId(userId);
        Cart cart = new Cart(id, userId);
        firebaseFirestore.collection(DatabaseConstant.CARTS).document(id).set(cart);
    }

    public void updateSellNumber(String productId, int quantity) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(productId);
        documentReference.update("sellNumber", FieldValue.increment(quantity * -1));
        // TODO: đánh giá cập nhập luôn số lượng bán => Không, bh đơn hàng chuyển status về 2 thì trừ
    }

    public void updateSoldNumber(String productId, int quantity) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.PRODUCTS).document(productId);
        documentReference.update("soldNumber", FieldValue.increment(quantity));
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

    public Task<QuerySnapshot> getProductsSelfDefault(String parentId, long from, int limit) {
        return firebaseFirestore.collection(DatabaseConstant.PRODUCTS)
                .whereEqualTo(InputParam.PARENT_ID, parentId)
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .startAfter(from).limit(limit).get();
    }

    // TODO: 15/11
    public Task<QuerySnapshot> getProducts(TypeProduct typeProduct, SortField sortField, long from, long to, int limit) {
        if (sortField.equals(SortField.PRICE_LOW)) {
            from = to;
        }
        Query query = firebaseFirestore.collection(DatabaseConstant.PRODUCTS)
                .orderBy(SortField.getField(sortField), SortField.getSortType(sortField) == 0 ? Query.Direction.ASCENDING : Query.Direction.DESCENDING)
                .startAt(from);
        if (sortField.equals(SortField.BEST_SELLERS)) {
            query = query.endBefore(0);
        }
        if (!typeProduct.equals(TypeProduct.ALL)) {
            query = query.whereEqualTo(InputParam.TYPE, TypeProduct.getName(typeProduct));
        }
        if (limit > 0) {
            query = query.limit(limit);
        }
        return query.get();
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
    public String createOrder(BuyItem buyItem, Address address, int payment) {
        Order order = new Order(buyItem.getId(), buyItem.getQuantity(), buyItem.getPrice(),
                buyItem.getPrice() * buyItem.getQuantity(), getUserId(), buyItem.getParentId(), address, payment);
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseConstant.ORDERS).document();
        order.setId(documentReference.getId());
        documentReference.set(order);
        return order.getId();
    }

    public void updateQuantityProduct(BuyItem buyItem) {
        // xóa sp trong giỏ
        deleteCart(buyItem.getId());
        // trừ số lượng tồn
        updateSellNumber(buyItem.getId(), buyItem.getQuantity());
        // thêm số lượng bán
    }

    public void updateStatusOrder(String id, int status) {
        Map<String, Object> data = new HashMap<>();
        data.put(InputParam.STATUS, status);
        data.put(InputParam.UPDATED_TIME, System.currentTimeMillis());
        firebaseFirestore.collection(DatabaseConstant.ORDERS).document(id).update(data);
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

    /*
        statistics
     */
    public void addOrUpdateStatistics(String orderId, long price, String sellerId) {
        CollectionReference collectionReference = firebaseFirestore.collection(DatabaseConstant.STATISTICS);
        collectionReference.add(new Statistics(orderId, getUserId(), price, 0, System.currentTimeMillis()));
        collectionReference.add(new Statistics(orderId, sellerId, price, 1, System.currentTimeMillis()));
    }

    // scope: 0->3, 1->6, 2->9, 3->12
    // type: -1 -> all, 0-> -, 1 -> +
    public Task<QuerySnapshot> getStatistics(String parentId, int scope, int type) {
        long end = System.currentTimeMillis() - getTimestamp(scope);
        if (type == 0) {
            return getStatisticsDefault(parentId, end);
        }
        return firebaseFirestore.collection(DatabaseConstant.STATISTICS)
                .whereEqualTo(InputParam.PARENT_ID, parentId)
                .whereEqualTo(InputParam.TYPE, type == 1 ? 1 : 0)
                .orderBy(InputParam.TIMESTAMP, Query.Direction.DESCENDING)
                .startAfter(Long.MAX_VALUE)
                .endAt(end)
                .get();
    }

    public Task<QuerySnapshot> getStatisticsDefault(String parentId, long end) {
        return firebaseFirestore.collection(DatabaseConstant.STATISTICS)
                .whereEqualTo(InputParam.PARENT_ID, parentId)
                .orderBy(InputParam.TIMESTAMP, Query.Direction.DESCENDING)
                .startAfter(Long.MAX_VALUE)
                .endAt(end)
                .get();
    }

    public long getTimestamp(int scope) {
        switch (scope) {
            case 0:
                return ConversionHelper.monthToMillisecond(3);
            case 1:
                return ConversionHelper.monthToMillisecond(6);
            case 2:
                return ConversionHelper.monthToMillisecond(9);
            case 3:
                return ConversionHelper.monthToMillisecond(12);
            default:
                return ConversionHelper.monthToMillisecond(3);
        }
    }
}
