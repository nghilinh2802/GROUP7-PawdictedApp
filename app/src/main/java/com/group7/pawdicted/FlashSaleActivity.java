package com.group7.pawdicted;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private LinearLayout tabContainer;
    private ProgressBar progressBar;
    private Handler countdownHandler;
    private Runnable countdownRunnable;

    private String selectedCategory = "all";
    private List<FlashSale> activeFlashSales;
    private FlashSale currentFlashSale;
    private int selectedTabIndex = 0;
    private boolean isFlashsale = false;
    private int flashsaleDiscountRate = 0;
    private double flashsalePrice = 0;
    private String flashsaleId = "";

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
        loadActiveFlashSales();
    }

    private void initViews() {
        txtHour = findViewById(R.id.txtHour);
        txtMinute = findViewById(R.id.txtMinute);
        txtSecond = findViewById(R.id.txtSecond);
        tabContainer = findViewById(R.id.tabContainer);
        rvFlashDeal = findViewById(R.id.rvFlashDeal);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        flashSaleProductList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        activeFlashSales = new ArrayList<>();
        countdownHandler = new Handler();
    }

    private void setupRecyclerView() {
        adapter = new FlashSaleAdapter(filteredProductList, this);
        rvFlashDeal.setLayoutManager(new LinearLayoutManager(this));
        rvFlashDeal.setAdapter(adapter);
    }

    private void loadActiveFlashSales() {
        Log.d("FlashSale", "=== TẢI TẤT CẢ FLASH SALE ĐANG HOẠT ĐỘNG ===");

        showLoading(true);

        // Timeout handler
        Handler timeoutHandler = new Handler();
        Runnable timeoutRunnable = () -> {
            showLoading(false);
            Toast.makeText(this, "Timeout: Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
        };
        timeoutHandler.postDelayed(timeoutRunnable, 10000); // 10 giây timeout

        long now = System.currentTimeMillis();

        db.collection("flashsales")
                .get()
                .addOnCompleteListener(task -> {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    showLoading(false);

                    if (task.isSuccessful()) {
                        activeFlashSales.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                FlashSale flashSale = document.toObject(FlashSale.class);
                                flashSale.setFlashSale_id(document.getId());

                                boolean isStarted = flashSale.getStartTime() <= now;
                                boolean isNotEnded = flashSale.getEndTime() >= now;

                                if (isStarted && isNotEnded) {
                                    activeFlashSales.add(flashSale);
                                    Log.d("FlashSale", "✅ Flash sale hoạt động: " + flashSale.getFlashSale_name() + " - Discount: " + flashSale.getDiscountRate() + "%");
                                }
                            } catch (Exception e) {
                                Log.e("FlashSale", "Lỗi parse flash sale: " + e.getMessage());
                            }
                        }

                        if (!activeFlashSales.isEmpty()) {
                            // Sắp xếp theo discount rate giảm dần
                            activeFlashSales.sort((f1, f2) -> Integer.compare(f2.getDiscountRate(), f1.getDiscountRate()));

                            createFlashSaleTabs();
                            selectFlashSaleTab(0); // Chọn tab đầu tiên (discount cao nhất)
                        } else {
                            Toast.makeText(this, "Không có flash sale nào đang diễn ra", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi tải flash sale: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createFlashSaleTabs() {
        Log.d("FlashSale", "=== TẠO TABS CHO " + activeFlashSales.size() + " FLASH SALE ===");

        tabContainer.removeAllViews();

        for (int i = 0; i < activeFlashSales.size(); i++) {
            FlashSale flashSale = activeFlashSales.get(i);
            View tabView = createTabView(flashSale, i);
            tabContainer.addView(tabView);

            // Thêm divider nếu không phải tab cuối
            if (i < activeFlashSales.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
                dividerParams.setMargins(8, 8, 8, 8);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
                tabContainer.addView(divider);
            }
        }
    }

    private View createTabView(FlashSale flashSale, int index) {
        LinearLayout tabLayout = new LinearLayout(this);
        tabLayout.setOrientation(LinearLayout.VERTICAL);
        tabLayout.setGravity(Gravity.CENTER);
        tabLayout.setPadding(16, 12, 16, 12);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        tabLayout.setLayoutParams(params);

        // Tên flash sale
        TextView tvName = new TextView(this);
        tvName.setText(flashSale.getFlashSale_name());
        tvName.setTextSize(20);
        tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvName.setGravity(Gravity.CENTER);
        tvName.setMaxLines(1);
        tvName.setEllipsize(TextUtils.TruncateAt.END);

        // Discount rate
        TextView tvDiscount = new TextView(this);
        tvDiscount.setText("-" + flashSale.getDiscountRate() + "%");
        tvDiscount.setTextSize(12);
        tvDiscount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvDiscount.setGravity(Gravity.CENTER);

        tabLayout.addView(tvName);
        tabLayout.addView(tvDiscount);

        // Set style cho tab đầu tiên (selected)
        if (index == 0) {
            setTabSelected(tabLayout, tvName, tvDiscount);
        } else {
            setTabNormal(tabLayout, tvName, tvDiscount);
        }

        // Click listener
        final int tabIndex = index;
        tabLayout.setOnClickListener(v -> selectFlashSaleTab(tabIndex));

        return tabLayout;
    }

    private void selectFlashSaleTab(int index) {
        Log.d("FlashSale", "=== CHỌN TAB " + index + " ===");

        selectedTabIndex = index;
        currentFlashSale = activeFlashSales.get(index);

        Log.d("FlashSale", "Flash sale được chọn: " + currentFlashSale.getFlashSale_name());

        // Cập nhật style cho tất cả tabs
        updateTabStyles();

        // Load sản phẩm của flash sale được chọn
        loadFlashSaleProducts();
    }

    private void updateTabStyles() {
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            View child = tabContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout tabLayout = (LinearLayout) child;
                if (tabLayout.getChildCount() >= 2) {
                    TextView tvName = (TextView) tabLayout.getChildAt(0);
                    TextView tvDiscount = (TextView) tabLayout.getChildAt(1);

                    int tabIndex = i / 2; // Chia 2 vì có divider

                    if (tabIndex == selectedTabIndex) {
                        setTabSelected(tabLayout, tvName, tvDiscount);
                    } else {
                        setTabNormal(tabLayout, tvName, tvDiscount);
                    }
                }
            }
        }
    }

    private void setTabSelected(LinearLayout tabLayout, TextView tvName, TextView tvDiscount) {
        tabLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvName.setTextColor(Color.parseColor("#A01B1B"));
        tvName.setTypeface(null, Typeface.BOLD);
        tvDiscount.setTextColor(Color.parseColor("#A01B1B"));
        tvDiscount.setTypeface(null, Typeface.BOLD);
    }

    private void setTabNormal(LinearLayout tabLayout, TextView tvName, TextView tvDiscount) {
        tabLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        tvName.setTextColor(Color.parseColor("#B0B0B0"));
        tvName.setTypeface(null, Typeface.NORMAL);
        tvDiscount.setTextColor(Color.parseColor("#B0B0B0"));
        tvDiscount.setTypeface(null, Typeface.NORMAL);
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

        showLoading(true);

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
            showLoading(false);
            return;
        }

        // Timeout handler cho products
        Handler timeoutHandler = new Handler();
        Runnable timeoutRunnable = () -> {
            showLoading(false);
            Toast.makeText(this, "Timeout: Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000); // 15 giây timeout

        Log.d("FlashSale", "Bắt đầu truy vấn collection 'products' với " + productIds.size() + " IDs");

        db.collection("products")
                .whereIn("product_id", productIds)
                .get()
                .addOnCompleteListener(task -> {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    showLoading(false);

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
                        Toast.makeText(this, "Lỗi tải sản phẩm: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        product.getCategory_id().startsWith(category)) {
                    filteredProductList.add(product);
                }
            }
            Log.d("FlashSale", "Lọc theo '" + category + "': " + filteredProductList.size() + " sản phẩm");
        }

        adapter.notifyDataSetChanged();
        updateCategoryButtonStyles(category);

        if (filteredProductList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào trong danh mục này", Toast.LENGTH_SHORT).show();
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
                // Flash sale hiện tại đã kết thúc, reload tất cả
                loadActiveFlashSales();
            }
        }
    }

    private void setupClickListeners() {
        setupCategoryButtons();
    }

    private void setupCategoryButtons() {
        Button btnAll = findViewById(R.id.btnAll);
        Button btnFoodTreat = findViewById(R.id.btnFoodTreat);
        Button btnPetCare = findViewById(R.id.btnPetCare);
        Button btnFurniture = findViewById(R.id.btnFurniture);
        Button btnToys = findViewById(R.id.btnToys);
        Button btnAccessories = findViewById(R.id.btnAccessories);
        Button btnCarriers = findViewById(R.id.btnCarriers);

        if (btnAll != null) btnAll.setOnClickListener(v -> filterByCategory("all"));
        if (btnFoodTreat != null) btnFoodTreat.setOnClickListener(v -> filterByCategory("FT"));
        if (btnPetCare != null) btnPetCare.setOnClickListener(v -> filterByCategory("PC"));
        if (btnFurniture != null) btnFurniture.setOnClickListener(v -> filterByCategory("FU"));
        if (btnToys != null) btnToys.setOnClickListener(v -> filterByCategory("TO"));
        if (btnAccessories != null) btnAccessories.setOnClickListener(v -> filterByCategory("AC"));
        if (btnCarriers != null) btnCarriers.setOnClickListener(v -> filterByCategory("CK"));
    }

    private void updateCategoryButtonStyles(String selectedCategory) {
        // Reset tất cả buttons về style normal
        resetButtonStyle(findViewById(R.id.btnAll));
        resetButtonStyle(findViewById(R.id.btnFoodTreat));
        resetButtonStyle(findViewById(R.id.btnPetCare));
        resetButtonStyle(findViewById(R.id.btnFurniture));
        resetButtonStyle(findViewById(R.id.btnToys));
        resetButtonStyle(findViewById(R.id.btnAccessories));
        resetButtonStyle(findViewById(R.id.btnCarriers));

        // Set style cho button được chọn
        Button selectedButton = null;
        switch (selectedCategory) {
            case "all":
                selectedButton = findViewById(R.id.btnAll);
                break;
            case "FT":
                selectedButton = findViewById(R.id.btnFoodTreat);
                break;
            case "PC":
                selectedButton = findViewById(R.id.btnPetCare);
                break;
            case "FU":
                selectedButton = findViewById(R.id.btnFurniture);
                break;
            case "TO":
                selectedButton = findViewById(R.id.btnToys);
                break;
            case "AC":
                selectedButton = findViewById(R.id.btnAccessories);
                break;
            case "CK":
                selectedButton = findViewById(R.id.btnCarriers);
                break;
        }

        if (selectedButton != null) {
            setButtonSelectedStyle(selectedButton);
        }
    }

    private void resetButtonStyle(Button button) {
        if (button != null) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            button.setTextColor(Color.parseColor("#A01B1B"));
        }
    }

    private void setButtonSelectedStyle(Button button) {
        if (button != null) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A01B1B")));
            button.setTextColor(Color.WHITE);
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        if (rvFlashDeal != null) {
            rvFlashDeal.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }

        if (tabContainer != null) {
            tabContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }
    // Thêm method này vào cuối class FlashSaleActivity
    public void navigateToProductDetails(String productId, FlashSale.FlashSaleProductInfo flashSaleInfo) {
        Log.d("FlashSale", "=== CHUYỂN SANG PRODUCT DETAILS ===");
        Log.d("FlashSale", "Product ID: " + productId);
        Log.d("FlashSale", "Flashsale ID: " + currentFlashSale.getFlashSale_id());
        Log.d("FlashSale", "Discount Rate: " + flashSaleInfo.getDiscountRate() + "%");

        Intent intent = new Intent(FlashSaleActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product_id", productId);
        intent.putExtra("IS_FLASHSALE", true);
        intent.putExtra("FLASHSALE_DISCOUNT_RATE", flashSaleInfo.getDiscountRate());
        intent.putExtra("FLASHSALE_ID", currentFlashSale.getFlashSale_id());
        intent.putExtra("FLASHSALE_NAME", currentFlashSale.getFlashSale_name());
        intent.putExtra("FLASHSALE_END_TIME", currentFlashSale.getEndTime());

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownHandler != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
    }
}
