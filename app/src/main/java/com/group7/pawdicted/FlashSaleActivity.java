package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group7.pawdicted.mobile.adapters.FlashSaleAdapter;
import com.group7.pawdicted.mobile.models.FlashSale;
import com.group7.pawdicted.mobile.models.FlashSaleProduct;
import com.group7.pawdicted.mobile.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FlashSaleActivity extends AppCompatActivity {
    private RecyclerView rvFlashDeal;
    private FlashSaleAdapter adapter;
    private List<FlashSaleProduct> flashSaleProductList;
    private List<FlashSaleProduct> filteredProductList;
    private FirebaseFirestore db;

    private TextView txtHour, txtMinute, txtSecond;
    private TextView txtOngoing, txtOngoingTime, txtUpcoming, txtUpcomingTime;
    private Handler countdownHandler;
    private Runnable countdownRunnable;

    private String selectedCategory = "all";
    private FlashSale currentFlashSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashSale", "=== KHỞI TẠO FLASH SALE ACTIVITY ===");

        setContentView(R.layout.activity_flash_sale);

        Log.d("FlashSale", "Khởi tạo views...");
        initViews();

        Log.d("FlashSale", "Thiết lập RecyclerView...");
        setupRecyclerView();

        Log.d("FlashSale", "Thiết lập countdown...");
        setupCountdown();

        Log.d("FlashSale", "Thiết lập click listeners...");
        setupClickListeners();

        Log.d("FlashSale", "Bắt đầu tải flash sale...");
        loadCurrentFlashSale();
    }

    private void initViews() {
        txtHour = findViewById(R.id.txtHour);
        txtMinute = findViewById(R.id.txtMinute);
        txtSecond = findViewById(R.id.txtSecond);

        txtOngoing = findViewById(R.id.txtOngoing);
        txtOngoingTime = findViewById(R.id.txtOngoingTime);
        txtUpcoming = findViewById(R.id.txtUpcoming);
        txtUpcomingTime = findViewById(R.id.txtUpcomingTime);

        rvFlashDeal = findViewById(R.id.rvFlashDeal);

        db = FirebaseFirestore.getInstance();
        flashSaleProductList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        countdownHandler = new Handler();
    }

    private void setupRecyclerView() {
        adapter = new FlashSaleAdapter(filteredProductList, this);
        rvFlashDeal.setLayoutManager(new LinearLayoutManager(this));
        rvFlashDeal.setAdapter(adapter);
    }

    private void loadCurrentFlashSale() {
        Log.d("FlashSale", "=== BẮT ĐẦU TẢI FLASH SALE ===");

        long now = System.currentTimeMillis();
        Log.d("FlashSale", "Thời gian hiện tại: " + now);

        db.collection("flashsales")
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("FlashSale", "Hoàn thành truy vấn Firestore");

                    if (task.isSuccessful()) {
                        Log.d("FlashSale", "✅ Truy vấn thành công");
                        Log.d("FlashSale", "Số lượng documents: " + task.getResult().size());

                        if (task.getResult().isEmpty()) {
                            Log.w("FlashSale", "❌ Không có document nào trong collection flashsales");
                            Toast.makeText(this, "Collection flashsales trống", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FlashSale activeFlashSale = null;
                        int documentIndex = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            documentIndex++;
                            Log.d("FlashSale", "--- KIỂM TRA DOCUMENT " + documentIndex + " ---");
                            Log.d("FlashSale", "Document ID: " + document.getId());

                            try {
                                FlashSale flashSale = document.toObject(FlashSale.class);
                                flashSale.setFlashSale_id(document.getId());

                                Log.d("FlashSale", "Tên flash sale: " + flashSale.getFlashSale_name());
                                Log.d("FlashSale", "Thời gian bắt đầu: " + flashSale.getStartTime());
                                Log.d("FlashSale", "Thời gian kết thúc: " + flashSale.getEndTime());
                                Log.d("FlashSale", "Thời gian hiện tại: " + now);

                                boolean isStarted = flashSale.getStartTime() <= now;
                                boolean isNotEnded = flashSale.getEndTime() >= now;

                                Log.d("FlashSale", "Đã bắt đầu? " + isStarted);
                                Log.d("FlashSale", "Chưa kết thúc? " + isNotEnded);
                                Log.d("FlashSale", "Đang hoạt động? " + (isStarted && isNotEnded));

                                // Convert timestamp to readable date
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Log.d("FlashSale", "Ngày bắt đầu: " + sdf.format(new Date(flashSale.getStartTime())));
                                Log.d("FlashSale", "Ngày kết thúc: " + sdf.format(new Date(flashSale.getEndTime())));
                                Log.d("FlashSale", "Ngày hiện tại: " + sdf.format(new Date(now)));

                                if (isStarted && isNotEnded) {
                                    activeFlashSale = flashSale;
                                    Log.d("FlashSale", "🎉 TÌM THẤY FLASH SALE ĐANG HOẠT ĐỘNG: " + flashSale.getFlashSale_name());
                                    break;
                                } else {
                                    Log.d("FlashSale", "❌ Flash sale này không hoạt động");
                                }

                            } catch (Exception e) {
                                Log.e("FlashSale", "❌ Lỗi khi parse document " + document.getId() + ": " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if (activeFlashSale != null) {
                            Log.d("FlashSale", "✅ SỬ DỤNG FLASH SALE: " + activeFlashSale.getFlashSale_name());
                            currentFlashSale = activeFlashSale;
                            loadFlashSaleProducts();
                        } else {
                            Log.w("FlashSale", "❌ KHÔNG TÌM THẤY FLASH SALE NÀO ĐANG HOẠT ĐỘNG");
                            Toast.makeText(this, "Không có flash sale nào đang diễn ra", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.e("FlashSale", "❌ Truy vấn thất bại: " + task.getException().getMessage());
                        if (task.getException() != null) {
                            task.getException().printStackTrace();
                        }
                        Toast.makeText(this, "Lỗi khi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadFlashSaleProducts() {
        Log.d("FlashSale", "=== BẮT ĐẦU TẢI SAN PHẨM FLASH SALE ===");

        if (currentFlashSale == null) {
            Log.e("FlashSale", "❌ currentFlashSale là null");
            return;
        }

        if (currentFlashSale.getProducts() == null) {
            Log.e("FlashSale", "❌ Danh sách products trong flash sale là null");
            return;
        }

        Log.d("FlashSale", "Flash sale hiện tại: " + currentFlashSale.getFlashSale_name());
        Log.d("FlashSale", "Số lượng sản phẩm trong flash sale: " + currentFlashSale.getProducts().size());

        flashSaleProductList.clear();

        List<String> productIds = new ArrayList<>();
        for (FlashSale.FlashSaleProductInfo productInfo : currentFlashSale.getProducts()) {
            productIds.add(productInfo.getProduct_id());
            Log.d("FlashSale", "Product ID cần tải: " + productInfo.getProduct_id());
        }

        if (productIds.isEmpty()) {
            Log.w("FlashSale", "❌ Danh sách product IDs trống");
            return;
        }

        Log.d("FlashSale", "Bắt đầu truy vấn collection 'products' với " + productIds.size() + " IDs");

        db.collection("products")
                .whereIn("product_id", productIds)
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("FlashSale", "Hoàn thành truy vấn products");

                    if (task.isSuccessful()) {
                        Log.d("FlashSale", "✅ Truy vấn products thành công");
                        Log.d("FlashSale", "Số lượng products tìm thấy: " + task.getResult().size());

                        int productIndex = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            productIndex++;
                            Log.d("FlashSale", "--- XỬ LÝ PRODUCT " + productIndex + " ---");
                            Log.d("FlashSale", "Document ID: " + document.getId());

                            try {
                                Map<String, Object> data = document.getData();
                                Log.d("FlashSale", "Dữ liệu product: " + data.keySet());

                                Product product = createProductFromMap(data);
                                Log.d("FlashSale", "✅ Tạo Product thành công: " + product.getProduct_name());

                                FlashSale.FlashSaleProductInfo flashSaleInfo = findFlashSaleInfo(product.getProduct_id());

                                if (flashSaleInfo != null) {
                                    Log.d("FlashSale", "✅ Tìm thấy flash sale info cho product: " + product.getProduct_id());
                                    Log.d("FlashSale", "Discount rate: " + flashSaleInfo.getDiscountRate() + "%");
                                    Log.d("FlashSale", "Max quantity: " + flashSaleInfo.getMaxQuantity());
                                    Log.d("FlashSale", "Unit sold: " + flashSaleInfo.getUnitSold());

                                    FlashSaleProduct flashSaleProduct = new FlashSaleProduct(
                                            product,
                                            flashSaleInfo,
                                            currentFlashSale.getFlashSale_id(),
                                            currentFlashSale.getFlashSale_name(),
                                            currentFlashSale.getEndTime()
                                    );
                                    flashSaleProductList.add(flashSaleProduct);
                                    Log.d("FlashSale", "✅ Thêm FlashSaleProduct thành công");
                                } else {
                                    Log.w("FlashSale", "❌ Không tìm thấy flash sale info cho product: " + product.getProduct_id());
                                }

                            } catch (Exception e) {
                                Log.e("FlashSale", "❌ Lỗi khi xử lý product " + document.getId() + ": " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        Log.d("FlashSale", "Tổng số FlashSaleProduct đã tạo: " + flashSaleProductList.size());

                        if (!flashSaleProductList.isEmpty()) {
                            flashSaleProductList.sort((p1, p2) ->
                                    Integer.compare(p2.getFlashSaleUnitSold(), p1.getFlashSaleUnitSold()));

                            Log.d("FlashSale", "✅ Sắp xếp danh sách thành công");
                            filterByCategory(selectedCategory);
                        } else {
                            Log.w("FlashSale", "❌ Danh sách FlashSaleProduct trống sau khi xử lý");
                            Toast.makeText(this, "Không có sản phẩm nào trong flash sale", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.e("FlashSale", "❌ Truy vấn products thất bại: " + task.getException().getMessage());
                        if (task.getException() != null) {
                            task.getException().printStackTrace();
                        }
                    }
                });
    }

    private Product createProductFromMap(Map<String, Object> data) {
        Log.d("FlashSale", "--- TẠO PRODUCT TỪ MAP ---");

        Product product = new Product();

        try {
            product.setProduct_id((String) data.get("product_id"));
            Log.d("FlashSale", "Product ID: " + product.getProduct_id());

            product.setProduct_name((String) data.get("product_name"));
            Log.d("FlashSale", "Product name: " + product.getProduct_name());

            product.setPrice(getDoubleValue(data, "price"));
            Log.d("FlashSale", "Price: " + product.getPrice());

            product.setDescription((String) data.get("description"));
            product.setDetails((String) data.get("details"));
            product.setAverage_rating(getDoubleValue(data, "average_rating"));
            product.setRating_number(getIntValue(data, "rating_number"));
            product.setQuantity(getIntValue(data, "quantity"));
            product.setProduct_image((String) data.get("product_image"));
            product.setAnimal_class_id(getIntValue(data, "animal_class_id"));
            product.setCategory_id((String) data.get("category_id"));
            product.setChild_category_id((String) data.get("child_category_id"));
            product.setRank(getIntValue(data, "rank"));
            product.setDiscount(getIntValue(data, "discount"));
            product.setSold_quantity(getIntValue(data, "sold_quantity"));

            Log.d("FlashSale", "Discount: " + product.getDiscount());
            Log.d("FlashSale", "Sold quantity: " + product.getSold_quantity());
            Log.d("FlashSale", "Category: " + product.getCategory_id());

            // Handle Date
            if (data.get("date_listed") instanceof com.google.firebase.Timestamp) {
                com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) data.get("date_listed");
                product.setDate_listed(timestamp.toDate());
            }

            // Handle Lists
            product.setVariant_id((List<String>) data.get("variant_id"));
            product.setAlso_buy((List<String>) data.get("also_buy"));
            product.setAlso_view((List<String>) data.get("also_view"));
            product.setSimilar_item((List<String>) data.get("similar_item"));

            Log.d("FlashSale", "✅ Tạo Product hoàn tất");

        } catch (Exception e) {
            Log.e("FlashSale", "❌ Lỗi khi tạo Product: " + e.getMessage());
            e.printStackTrace();
        }

        return product;
    }

    private int getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        Log.d("FlashSale", "getIntValue - Key: " + key + ", Value: " + value + ", Type: " + (value != null ? value.getClass().getSimpleName() : "null"));

        if (value == null) return 0;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        return 0;
    }

    private double getDoubleValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        Log.d("FlashSale", "getDoubleValue - Key: " + key + ", Value: " + value + ", Type: " + (value != null ? value.getClass().getSimpleName() : "null"));

        if (value == null) return 0.0;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Long) return ((Long) value).doubleValue();
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        return 0.0;
    }

    private FlashSale.FlashSaleProductInfo findFlashSaleInfo(String productId) {
        Log.d("FlashSale", "Tìm flash sale info cho product: " + productId);

        if (currentFlashSale != null && currentFlashSale.getProducts() != null) {
            for (FlashSale.FlashSaleProductInfo info : currentFlashSale.getProducts()) {
                if (productId.equals(info.getProduct_id())) {
                    Log.d("FlashSale", "✅ Tìm thấy flash sale info");
                    return info;
                }
            }
        }

        Log.w("FlashSale", "❌ Không tìm thấy flash sale info");
        return null;
    }

    private void filterByCategory(String category) {
        Log.d("FlashSale", "=== LỌC THEO DANH MỤC: " + category + " ===");

        selectedCategory = category;
        filteredProductList.clear();

        if (category.equals("all")) {
            filteredProductList.addAll(flashSaleProductList);
            Log.d("FlashSale", "Hiển thị tất cả: " + filteredProductList.size() + " sản phẩm");
        } else {
            for (FlashSaleProduct product : flashSaleProductList) {
                if (product.getCategory_id() != null &&
                        product.getCategory_id().toLowerCase().contains(category.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
            Log.d("FlashSale", "Lọc theo '" + category + "': " + filteredProductList.size() + " sản phẩm");
        }

        Log.d("FlashSale", "Cập nhật adapter với " + filteredProductList.size() + " sản phẩm");
        adapter.notifyDataSetChanged();

        if (filteredProductList.isEmpty()) {
            Log.w("FlashSale", "❌ Không có sản phẩm nào sau khi lọc");
            Toast.makeText(this, "Không có sản phẩm nào trong danh mục " + category, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("FlashSale", "✅ Hiển thị thành công " + filteredProductList.size() + " sản phẩm");
        }
    }

    private void setupCountdown() {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                updateCountdown();
                countdownHandler.postDelayed(this, 1000);
            }
        };
        countdownHandler.post(countdownRunnable);
    }

    private void updateCountdown() {
        if (currentFlashSale != null) {
            long timeLeft = currentFlashSale.getTimeRemaining();

            if (timeLeft > 0) {
                long hours = timeLeft / (1000 * 60 * 60);
                long minutes = (timeLeft % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (timeLeft % (1000 * 60)) / 1000;

                txtHour.setText(String.format("%02d", hours));
                txtMinute.setText(String.format("%02d", minutes));
                txtSecond.setText(String.format("%02d", seconds));
            } else {
                txtHour.setText("00");
                txtMinute.setText("00");
                txtSecond.setText("00");
                loadCurrentFlashSale();
            }
        }
    }

    private void setupClickListeners() {
        setupCategoryButtons();
    }

    private void setupCategoryButtons() {
        Button btnAll = findViewById(R.id.btnAll);
        Button btnToys = findViewById(R.id.btnToys);
        Button btnFood = findViewById(R.id.btnFood);
        Button btnAccessories = findViewById(R.id.btnAccessories);

        if (btnAll != null) btnAll.setOnClickListener(v -> filterByCategory("all"));
        if (btnToys != null) btnToys.setOnClickListener(v -> filterByCategory("toys"));
        if (btnFood != null) btnFood.setOnClickListener(v -> filterByCategory("food"));
        if (btnAccessories != null) btnAccessories.setOnClickListener(v -> filterByCategory("accessories"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownHandler != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
    }
}
