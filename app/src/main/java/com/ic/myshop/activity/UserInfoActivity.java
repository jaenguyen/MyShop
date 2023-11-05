package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ic.myshop.R;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;

public class UserInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean getSuccess = false;
    private TextView toolbarTitle;
    private ImageButton btnBack;
    private ImageView avatar;
    private TextView txtEmail, txtPhone, txtChangeAvatar, txtChangePassword;
    private Button btnUpload;
    private Uri imageUri;
    // db
    private FirebaseFirestore db;
    private User user;
    private static final AuthService authService = AuthService.getInstance();
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // get info user
        db.collection(DatabaseConstant.USERS).document(dbFactory.getUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            getSuccess = true;
                            user = value.toObject(User.class);
                            txtEmail.setText(user.getEmail());
                            txtPhone.setText(user.getPhone());
                            Glide.with(getApplicationContext())
                                    .load(user.getAvatar())
                                    .fitCenter()
                                    .into(avatar);
                        }
                    }
                });

        // update avatar
        txtChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authService.resetPassword(txtEmail.getText().toString());
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUser();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(avatar);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadUser() {
        if (getSuccess) {
            if (imageUri != null) {
                StorageReference fileReference = dbFactory.getStorageReference(getFileExtension(imageUri));
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        dbFactory.updateAvatarUser(user.getId(), uri.toString());
                                    }
                                });
                                imageUri = null;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), MessageConstant.UPLOAD_FAILURE, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if (!user.getPhone().equals(txtPhone.getText().toString())) {
                dbFactory.updatePhoneUser(user.getId(), txtPhone.getText().toString());
            }
        }
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT_USER_FILE);
        btnBack = findViewById(R.id.toolbar_back_button);
        avatar = findViewById(R.id.avatar);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        txtChangeAvatar = findViewById(R.id.txt_change_avatar);
        txtChangePassword = findViewById(R.id.txt_change_password);
        btnUpload = findViewById(R.id.btn_upload);
        db = FirebaseFirestore.getInstance();
    }
}