package com.group7.pawdicted;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RefundReturnRequestActivity extends AppCompatActivity {
    ImageView btn_back;
    FirebaseFirestore db;
    LinearLayout ll_items, layout_add_photo, layout_add_video;
    CheckBox checkAll, checkSituationAll;
    EditText et_reason;
    Button btn_send_request;
    List<CheckBox> allProductCheckboxes = new ArrayList<>();
    List<CheckBox> situationCheckboxes = new ArrayList<>();
    Uri mediaUri = null;
    String orderId;
    String orderItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_refund_return_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();

        db = FirebaseFirestore.getInstance();
        ll_items = findViewById(R.id.ll_items);

        layout_add_photo = findViewById(R.id.layout_add_photo);
        layout_add_video = findViewById(R.id.layout_add_video);
        et_reason = findViewById(R.id.et_reason);
        btn_send_request = findViewById(R.id.btn_send_request);

        layout_add_photo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 1001);
        });

        layout_add_video.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn video"), 1002);
        });

        checkAll = findViewById(R.id.checkbox_all);
        checkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox cb : allProductCheckboxes) {
                cb.setChecked(isChecked);
            }
        });

        checkSituationAll = findViewById(R.id.checkbox_situation_all);
        situationCheckboxes.add(findViewById(R.id.cb_problem_received));
        situationCheckboxes.add(findViewById(R.id.cb_missing));

        checkSituationAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox cb : situationCheckboxes) {
                cb.setChecked(isChecked);
            }
        });

        for (CheckBox cb : situationCheckboxes) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSituationCheckboxState();
            });
        }

        orderItemId = getIntent().getStringExtra("order_item_id");
        orderId = getIntent().getStringExtra("order_id");
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng để gửi yêu cầu", Toast.LENGTH_LONG).show();
            return;
        }
        if (orderItemId != null && !orderItemId.isEmpty()) {
            loadOrderItems(orderItemId);
        }

        btn_send_request.setOnClickListener(v -> {
            List<String> selectedProducts = new ArrayList<>();
            for (CheckBox cb : allProductCheckboxes) {
                if (cb.isChecked()) {
                    String productName = ((TextView)((LinearLayout) cb.getParent()).findViewById(R.id.product_name)).getText().toString();
                    selectedProducts.add(productName);
                }
            }

            List<String> selectedSituations = new ArrayList<>();
            for (CheckBox cb : situationCheckboxes) {
                if (cb.isChecked()) {
                    selectedSituations.add(cb.getText().toString());
                }
            }

            String reason = et_reason.getText().toString().trim();

            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Please select at least 1 product", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSituations.isEmpty()) {
                Toast.makeText(this, "Please select at least 1 situation encountered", Toast.LENGTH_SHORT).show();
                return;
            }
            if (reason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mediaUri != null) {
                uploadMediaToFirebase(mediaUri, mediaUrl -> {
                    sendRefundRequest(selectedProducts, selectedSituations, reason, mediaUrl);
                });
            } else {
                sendRefundRequest(selectedProducts, selectedSituations, reason, null);
            }
        });
    }

    private void sendRefundRequest(List<String> selectedProducts, List<String> selectedSituations, String reason, @Nullable String mediaUrl) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("product_return", selectedProducts);
        updateMap.put("return_reason", reason);
        updateMap.put("return_situation", selectedSituations);
        updateMap.put("return_requested_at", Timestamp.now());
        updateMap.put("status", "Return/Refund");
        if (mediaUrl != null) {
            updateMap.put("return_source", mediaUrl);
        }

        db.collection("orders").document(orderId)
                .update(updateMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Refund request sent successfully", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error sending: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void addViews() {
        btn_back = findViewById(R.id.btn_back);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> onBackPressed());
    }

    private void updateAllCheckboxState() {
        boolean allChecked = true;
        for (CheckBox cb : allProductCheckboxes) {
            if (!cb.isChecked()) {
                allChecked = false;
                break;
            }
        }
        checkAll.setOnCheckedChangeListener(null);
        checkAll.setChecked(allChecked);
        checkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox cb : allProductCheckboxes) {
                cb.setChecked(isChecked);
            }
        });
    }

    private void updateSituationCheckboxState() {
        boolean allChecked = true;
        for (CheckBox cb : situationCheckboxes) {
            if (!cb.isChecked()) {
                allChecked = false;
                break;
            }
        }
        checkSituationAll.setOnCheckedChangeListener(null);
        checkSituationAll.setChecked(allChecked);
        checkSituationAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox cb : situationCheckboxes) {
                cb.setChecked(isChecked);
            }
        });
    }

    private void loadOrderItems(String orderItemId) {
        db.collection("order_items").document(orderItemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ll_items.removeAllViews();
                            for (String key : document.getData().keySet()) {
                                if (key.startsWith("product")) {
                                    String productId = document.getString(key + ".product_id");
                                    fetchProductDetails(productId);
                                }
                            }
                        }
                    }
                });
    }

    private void fetchProductDetails(String productId) {
        db.collection("products").document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot productDoc = task.getResult();
                        if (productDoc.exists()) {
                            String productName = productDoc.getString("product_name");
                            String imageUrl = productDoc.getString("product_image");

                            View productView = LayoutInflater.from(this).inflate(R.layout.product_refund_item_view, ll_items, false);

                            TextView productNameTextView = productView.findViewById(R.id.product_name);
                            ImageView productImageView = productView.findViewById(R.id.product_image);
                            CheckBox productCheckBox = productView.findViewById(R.id.product_checkbox);

                            productNameTextView.setText(productName);
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .into(productImageView);

                            ll_items.addView(productView);
                            allProductCheckboxes.add(productCheckBox);

                            productCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                updateAllCheckboxState();
                            });
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            mediaUri = data.getData();
            if (requestCode == 1001) {
                Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 1002) {
                Toast.makeText(this, "Video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadMediaToFirebase(Uri uri, OnMediaUploadedListener listener) {
        if (uri == null) {
            Toast.makeText(this, "No file found to upload", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("uploadMediaToFirebase", "Uploading: " + uri.toString());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = "refund_media/" + UUID.randomUUID() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        listener.onUploaded(downloadUri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FirebaseUpload", "Upload failed", e);
                });
    }

    interface OnMediaUploadedListener {
        void onUploaded(String mediaUrl);
    }
}
