package com.ic.myshop.activity.usecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.user.MyOrderActivity;
import com.ic.myshop.adapter.address.AddressListAdapter;
import com.ic.myshop.adapter.product.BuyItemAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.constant.Payment;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ApiService;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;
import com.ic.myshop.output.BuyItem;
import com.ic.myshop.push.Notify;
import com.ic.myshop.zalo.CreateOrder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class BuyActivity extends AppCompatActivity {

    private long totalPrice = 0;
    private TextView txtTotalPrice, btnBuy, toolbarTitle, txtNameAddress, txtPhoneAddress,
            txtStreetAddress, btnBuyByZalo, txtChangeAddress;
    private ImageButton btnBack;
    // rcv
    private RecyclerView rcvBuyItem;
    private LinearLayoutManager linearLayoutManager;
    private BuyItemAdapter buyItemAdapter;
    // db
    private static final DbFactory dbFactory = DbFactory.getInstance();
    private User user;
    private Address address;
    private List<Address> addresses;
    private AddressListAdapter addressAdapter;
    private Map<String, Integer> cartItems;
    private boolean isBuyNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        init();

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        cartItems = (Map<String, Integer>) getIntent().getSerializableExtra(InputParam.CART_ITEMS);
        isBuyNow = getIntent().getBooleanExtra(Constant.BUY_NOW, false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dbFactory.getUser(dbFactory.getUserId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            user = documentSnapshot.toObject(User.class);
                            addresses = user.getAddresses();
                            addresses = user.getAddresses();
                            if (addresses == null || addresses.isEmpty()) {
                                // Tạo một Dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
                                // Inflate layout cho dialog từ tệp XML
                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_address, null);
                                builder.setView(dialogView);
                                // Khởi tạo các thành phần trong dialog
                                TextView txtName = dialogView.findViewById(R.id.txt_name);
                                TextView txtPhone = dialogView.findViewById(R.id.txt_phone);
                                TextView txtStreet = dialogView.findViewById(R.id.txt_street);
                                Button btnAddAddress = dialogView.findViewById(R.id.btn_add_address);
                                // Tạo và hiển thị dialog
                                AlertDialog dialog = builder.create();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                // Thiết lập sự kiện cho nút add, remove
                                btnAddAddress.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = txtName.getText().toString().trim();
                                        String phone = txtPhone.getText().toString().trim();
                                        String street = txtStreet.getText().toString();
                                        address = new Address(name, phone, street);
                                        changeAddress();
                                        user.addAddress(address);
                                        dbFactory.updateAddresses(user);
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                address = user.getAddresses().get(0);
                                changeAddress();
                            }
                        }
                    }
                });

        for (String id : cartItems.keySet()) {
            dbFactory.getProduct(id)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                Product product = documentSnapshot.toObject(Product.class);
                                int quantity = cartItems.get(id);
                                BuyItem buyItem = new BuyItem(product.getId(), product.getImageUrl(), product.getName(), product.getPrice(), product.getParentId());
                                buyItem.setQuantity(quantity);
                                buyItemAdapter.addProduct(buyItem);
                                totalPrice += product.getPrice() * quantity;
                                txtTotalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(totalPrice)));
                            }
                        }
                    });
        }

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment(Payment.COD.valueOf());
            }
        });

        btnBuyByZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZalo();
            }
        });

        // đổi địa chỉ nhận hàng
        txtChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = new RecyclerView(getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
                builder.setView(recyclerView);
                AlertDialog dialog = builder.create();
                dialog.setTitle(Constant.EDIT_ADDRESS);
                addressAdapter = new AddressListAdapter(addresses, new AddressListAdapter.OnAddressClickListener() {
                    @Override
                    public void onAddressClick(int position) {
                        address = addresses.get(position);
                        changeAddress();
                        dialog.dismiss();
                    }
                });
                recyclerView.setAdapter(addressAdapter);
                dialog.show();
            }
        });
    }

    // set lại text address
    private void changeAddress() {
        txtNameAddress.setText(String.format("Họ tên: %s", address.getName()));
        txtPhoneAddress.setText(String.format("Số điện thoại: %s", address.getPhone()));
        txtStreetAddress.setText(String.format("Địa chỉ: %s", address.getStreet()));
    }

    // cập nhập đơn hàng, chuyển đến giao diện đơn hàng
    private void payment(int payment) {
        List<BuyItem> buyItems = buyItemAdapter.getCartItems();
        List<String> orderIds = new ArrayList<>();
        for (BuyItem buyItem : buyItems) {
            Order order = dbFactory.createOrder(buyItem, address, payment);
            orderIds.add(order.getId());
            // khi tạo đơn hàng sẽ trừ số lượng trong kho
            dbFactory.updateQuantityProduct(buyItem);
            // send notify
            ApiService.apiService2.push(Notify.params(order.getSellerId(), order.getId(), order.getStatus()))
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            System.out.println(response.body());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
        }
        // nếu thực hiện thanh toán trực tiếp thì sẽ tạo thống kê lun
        // không cho hủy đơn hàng đã thanh toán
        // khi xác nhận giao hàng chỉ tạo hóa đơn cho đơn hàng COD
        if (Payment.get(payment) == Payment.IMMEDIATELY) {
            for (int i = 0; i < buyItems.size(); i++) {
                BuyItem buyItem = buyItems.get(i);
                dbFactory.addOrUpdateStatistics(orderIds.get(i), buyItem.getPrice() * buyItem.getQuantity(), buyItem.getParentId());
            }
        }

        Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
        startActivity(intent);
        finish();
    }

    // thanh toán = zalopay
    private void requestZalo() {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(String.valueOf(totalPrice));
            String code = data.getString("return_code");
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        payment(Payment.IMMEDIATELY.valueOf());
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Toast.makeText(BuyActivity.this, MessageConstant.FAIL, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Toast.makeText(BuyActivity.this, MessageConstant.FAIL, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isBuyNow) {
            Intent intent = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.BUY);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        btnBuy = findViewById(R.id.btn_buy);
        txtNameAddress = findViewById(R.id.txt_name_address);
        txtPhoneAddress = findViewById(R.id.txt_phone_address);
        txtStreetAddress = findViewById(R.id.txt_street_address);
        txtChangeAddress = findViewById(R.id.txt_change_address);
        // rcv
        rcvBuyItem = findViewById(R.id.rcv_buy_item);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvBuyItem.setLayoutManager(linearLayoutManager);
        buyItemAdapter = new BuyItemAdapter(this);
        rcvBuyItem.setAdapter(buyItemAdapter);
        // zalo
        btnBuyByZalo = findViewById(R.id.btn_buy_by_zalo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}