package com.group7.pawdicted;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.graphics.Color;

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

import java.text.NumberFormat;
import java.util.*;

public class RefundReturnRequestActivity extends AppCompatActivity {

    ImageView btn_back;
    FirebaseFirestore db;
    LinearLayout ll_items, layout_add_photo, layout_add_video;
    CheckBox checkAll, checkSituationAll;
    EditText et_reason;
    Button btn_send_request;

    List<CheckBox> allProductCheckboxes = new ArrayList<>();
    List<CheckBox> situationCheckboxes = new ArrayList<>();
    Map<String, Integer> productPrices = new HashMap<>();
    Map<String, String> productIdNameMap = new HashMap<>();
    Map<CheckBox, String> checkboxToProductIdMap = new HashMap<>();

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

        checkAll = findViewById(R.id.checkbox_all);
        checkSituationAll = findViewById(R.id.checkbox_situation_all);
        situationCheckboxes.add(findViewById(R.id.cb_problem_received));
        situationCheckboxes.add(findViewById(R.id.cb_missing));

        layout_add_photo.setOnClickListener(v -> openMediaChooser("image/*", 1001));
        layout_add_video.setOnClickListener(v -> openMediaChooser("video/*", 1002));

        checkAll.setOnCheckedChangeListener((btn, checked) -> allProductCheckboxes.forEach(cb -> cb.setChecked(checked)));
        checkSituationAll.setOnCheckedChangeListener((btn, checked) -> situationCheckboxes.forEach(cb -> cb.setChecked(checked)));
        situationCheckboxes.forEach(cb -> cb.setOnCheckedChangeListener((b, i) -> updateSituationCheckboxState()));

        orderId = getIntent().getStringExtra("order_id");
        orderItemId = getIntent().getStringExtra("order_item_id");

        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng để gửi yêu cầu", Toast.LENGTH_LONG).show();
            return;
        }

        if (orderItemId != null) loadOrderItems(orderItemId);

        btn_send_request.setOnClickListener(v -> submitRefundRequest());
    }

    private void openMediaChooser(String type, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(Intent.createChooser(intent, "Chọn file"), requestCode);
    }

    private void submitRefundRequest() {
        List<Map<String, String>> selectedProducts = new ArrayList<>();
        for (CheckBox cb : allProductCheckboxes) {
            if (cb.isChecked()) {
                String productName = ((TextView)((LinearLayout) cb.getParent()).findViewById(R.id.product_name)).getText().toString();
                String productId = checkboxToProductIdMap.get(cb);
                Map<String, String> entry = new HashMap<>();
                entry.put("product_id", productId);
                entry.put("product_name", productName);
                selectedProducts.add(entry);
            }
        }

        List<String> selectedSituations = new ArrayList<>();
        for (CheckBox cb : situationCheckboxes) {
            if (cb.isChecked()) selectedSituations.add(cb.getText().toString());
        }

        String reason = et_reason.getText().toString().trim();

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Please select at least 1 product", Toast.LENGTH_SHORT).show(); return;
        }
        if (selectedSituations.isEmpty()) {
            Toast.makeText(this, "Please select at least 1 situation encountered", Toast.LENGTH_SHORT).show(); return;
        }
        if (reason.isEmpty()) {
            Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show(); return;
        }

        if (mediaUri != null) {
            uploadMediaToFirebase(mediaUri, mediaUrl -> sendRefundRequest(selectedProducts, selectedSituations, reason, mediaUrl));
        } else {
            sendRefundRequest(selectedProducts, selectedSituations, reason, null);
        }
    }

    private void sendRefundRequest(List<Map<String, String>> selectedProducts, List<String> selectedSituations, String reason, @Nullable String mediaUrl) {
        int totalReturnAmount = 0;
        for (Map<String, String> product : selectedProducts) {
            String productId = product.get("product_id");
            if (productId != null && productPrices.containsKey(productId)) {
                totalReturnAmount += productPrices.get(productId);
            }
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("product_return", selectedProducts);
        updateMap.put("return_reason", reason);
        updateMap.put("return_situation", selectedSituations);
        updateMap.put("return_requested_at", Timestamp.now());
        updateMap.put("order_status", "Return/Refund");
        updateMap.put("return_amount", totalReturnAmount);
        if (mediaUrl != null) updateMap.put("return_source", mediaUrl);

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

    private void updateSituationCheckboxState() {
        boolean allChecked = true;
        for (CheckBox cb : situationCheckboxes) {
            if (!cb.isChecked()) {
                allChecked = false; break;
            }
        }
        checkSituationAll.setOnCheckedChangeListener(null);
        checkSituationAll.setChecked(allChecked);
        checkSituationAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox cb : situationCheckboxes) cb.setChecked(isChecked);
        });
    }

    private void loadOrderItems(String orderItemId) {
        db.collection("order_items").document(orderItemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            ll_items.removeAllViews();
                            for (String key : doc.getData().keySet()) {
                                if (key.startsWith("product")) {
                                    String productId = doc.getString(key + ".product_id");
                                    Long cost = doc.getLong(key + ".total_cost_of_goods");
                                    int totalCost = (cost != null) ? cost.intValue() : 0;
                                    if (productId != null) fetchProductDetails(productId, totalCost);
                                }
                            }
                        }
                    }
                });
    }

    private void fetchProductDetails(String productId, int totalCost) {
        db.collection("products").document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String productName = doc.getString("product_name");
                            String imageUrl = doc.getString("product_image");

                            View view = LayoutInflater.from(this).inflate(R.layout.product_refund_item_view, ll_items, false);
                            TextView nameTV = view.findViewById(R.id.product_name);
                            ImageView img = view.findViewById(R.id.product_image);
                            CheckBox cb = view.findViewById(R.id.product_checkbox);

                            nameTV.setText(productName);
                            Glide.with(this).load(imageUrl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .into(img);

                            TextView priceTV = new TextView(this);
                            priceTV.setTextSize(14f);
                            priceTV.setTextColor(Color.DKGRAY);
                            priceTV.setPadding(8, 4, 8, 8);
                            priceTV.setText("Return Amount: " + NumberFormat.getInstance(new Locale("vi", "VN")).format(totalCost) + "₫");

                            ((LinearLayout) nameTV.getParent()).addView(priceTV);

                            ll_items.addView(view);
                            allProductCheckboxes.add(cb);
                            productPrices.put(productId, totalCost);
                            productIdNameMap.put(productName, productId);
                            checkboxToProductIdMap.put(cb, productId);

                            cb.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllCheckboxState());
                        }
                    }
                });
    }

    private void updateAllCheckboxState() {
        boolean allChecked = allProductCheckboxes.stream().allMatch(CheckBox::isChecked);
        checkAll.setOnCheckedChangeListener(null);
        checkAll.setChecked(allChecked);
        checkAll.setOnCheckedChangeListener((buttonView, isChecked) -> allProductCheckboxes.forEach(cb -> cb.setChecked(isChecked)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            mediaUri = data.getData();
            Toast.makeText(this, requestCode == 1001 ? "Photo selected" : "Video selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadMediaToFirebase(Uri uri, OnMediaUploadedListener listener) {
        if (uri == null) {
            Toast.makeText(this, "No file found to upload", Toast.LENGTH_SHORT).show(); return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = "refund_media/" + UUID.randomUUID() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    listener.onUploaded(downloadUri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FirebaseUpload", "Upload failed", e);
                });
    }

    interface OnMediaUploadedListener {
        void onUploaded(String mediaUrl);
    }
}
