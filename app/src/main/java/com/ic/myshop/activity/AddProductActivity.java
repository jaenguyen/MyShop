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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView toolbarTitle;
    private ImageButton btnBack;
    private EditText txtName, txtDescription, txtPrice, txtSellNumber;
    private Spinner typeSpinner;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button btnChooseImage, btnUpload;
    private Uri imageUri;
    // firebase
    private StorageTask uploadTask;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), MessageConstant.UPLOADING, Toast.LENGTH_SHORT).show();
                } else {
                    uploadProduct();
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ADD_PRODUCT);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtName = findViewById(R.id.txt_name);
        txtDescription = findViewById(R.id.txt_description);
        txtPrice = findViewById(R.id.txt_price);
        txtSellNumber = findViewById(R.id.txt_sell_number);
        typeSpinner = findViewById(R.id.typeSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_type_product,
                getResources().getStringArray(R.array.type)));
        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress_bar);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnUpload = findViewById(R.id.btn_upload);
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
            Glide.with(this).load(imageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadProduct() {
        if (imageUri != null) { // TODO: BỔ SUNG VALIDATE
            StorageReference fileReference = dbFactory.getStorageReference(getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String name = txtName.getText().toString();
                                    String description = txtDescription.getText().toString();
                                    long price = Long.parseLong(txtPrice.getText().toString());
                                    int sellNumber = Integer.parseInt(txtSellNumber.getText().toString());
                                    String type = typeSpinner.getSelectedItem().toString();
                                    Product product = new Product(name, description, price, sellNumber,
                                            type, uri.toString(), dbFactory.getUserId());
                                    dbFactory.addProduct(product);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), MessageConstant.UPLOAD_FAILURE, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MyProductActivity.class);
        startActivity(intent);
        finish();
    }
}