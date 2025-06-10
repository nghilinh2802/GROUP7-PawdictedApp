package com.group7.pawdicted;

import static com.group7.pawdicted.CategoryActivity.SideCategory.ACCESSORIES;
import static com.group7.pawdicted.CategoryActivity.SideCategory.ALL;
import static com.group7.pawdicted.CategoryActivity.SideCategory.CARRIERS;
import static com.group7.pawdicted.CategoryActivity.SideCategory.FOOD;
import static com.group7.pawdicted.CategoryActivity.SideCategory.PET_CARE;
import static com.group7.pawdicted.CategoryActivity.SideCategory.TOYS;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group7.pawdicted.mobile.models.Category;
import com.group7.pawdicted.mobile.models.ListCategory;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    FooterManager footerManager;
    public enum TopCategory {
        DOGS, CATS, NONE
    }

    public enum SideCategory {
        ALL, FOOD, PET_CARE, FURNITURE, TOYS, ACCESSORIES, CARRIERS, NONE
    }

    private TopCategory selectedTop = TopCategory.NONE;
    private SideCategory selectedSide = ALL;

    private TextView txtForDogs, txtForCats;
    private View redBarDogs, redBarCats;

    private ImageView imgAllProducts;
    private TextView txtAllProducts;
    private View redBarAllProducts;

    private ImageView imgFood;
    private TextView txtFood;
    private View redBarFood;

    private ImageView imgPetCare;
    private TextView txtPetCare;
    private View redBarPetCare;

    private ImageView imgFurniture;
    private TextView txtFurniture;
    private View redBarFurniture;

    private ImageView imgToys;
    private TextView txtToys;
    private View redBarToys;

    private ImageView imgAccesories;
    private TextView txtAccesories;
    private View redBarAccessories;

    private ImageView imgCarriers;
    private TextView txtCarriers;
    private View redBarCarriers;
    private LinearLayout layoutAllProducts;
    private LinearLayout layoutFood;
    private LinearLayout layoutPetCare;
    private LinearLayout layoutFurniture;
    private LinearLayout layoutToys;
    private LinearLayout layoutAccessories;
    private LinearLayout layoutCarriers;
    private LinearLayout productListLayout;
    private ListCategory listCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        footerManager = new FooterManager(this);


        addViews();
        addEvents();
    }

    private void addViews() {
        txtForDogs = findViewById(R.id.txtForDogs);
        txtForCats = findViewById(R.id.txtForCats);
        redBarDogs = findViewById(R.id.redBarDogs);
        redBarCats = findViewById(R.id.redBarCats);

        imgAllProducts = findViewById(R.id.img_cate_all_products);
        txtAllProducts = findViewById(R.id.txt_cate_all_products);
        redBarAllProducts = findViewById(R.id.red_bar_all_products);

        imgFood = findViewById(R.id.img_cate_food);
        txtFood = findViewById(R.id.txt_cate_food);
        redBarFood = findViewById(R.id.red_bar_food);

        imgPetCare = findViewById(R.id.img_pet_care);
        txtPetCare = findViewById(R.id.txt_pet_care);
        redBarPetCare = findViewById(R.id.red_bar_pet_care);

        imgFurniture = findViewById(R.id.img_furniture);
        txtFurniture = findViewById(R.id.txt_furniture);
        redBarFurniture = findViewById(R.id.red_bar_furniture);

        imgToys = findViewById(R.id.img_cate_toys);
        txtToys = findViewById(R.id.txt_cate_toys);
        redBarToys = findViewById(R.id.red_bar_toys);

        imgAccesories = findViewById(R.id.img_accesories);
        txtAccesories = findViewById(R.id.txt_accesories);
        redBarAccessories = findViewById(R.id.red_bar_accesories);

        imgCarriers = findViewById(R.id.img_carriers);
        txtCarriers = findViewById(R.id.txt_carriers);
        redBarCarriers = findViewById(R.id.red_bar_carriers);

        layoutAllProducts=findViewById(R.id.layout_all_products);
        layoutFood=findViewById(R.id.layout_cate_food);
        layoutPetCare=findViewById(R.id.layout_cate_pet_care);
        layoutFurniture=findViewById(R.id.layout_cate_furniture);
        layoutToys=findViewById(R.id.layout_cate_toys);
        layoutAccessories=findViewById(R.id.layout_cate_accessories);
        layoutCarriers=findViewById(R.id.layout_cate_carriers);

        productListLayout = findViewById(R.id.ProductList);

        listCategory = new ListCategory();
        listCategory.generate_sample_dataset();
    }

    private void addEvents() {
        txtForDogs.setOnClickListener(v -> toggleTopCategory(TopCategory.DOGS));
        txtForCats.setOnClickListener(v -> toggleTopCategory(TopCategory.CATS));

        layoutAllProducts.setOnClickListener(v -> toggleSideCategory(ALL));
        layoutFood.setOnClickListener(v -> toggleSideCategory(FOOD));
        layoutPetCare.setOnClickListener(v -> toggleSideCategory(PET_CARE));
        layoutFurniture.setOnClickListener(v -> toggleSideCategory(SideCategory.FURNITURE));
        layoutToys.setOnClickListener(v -> toggleSideCategory(TOYS));
        layoutAccessories.setOnClickListener(v -> toggleSideCategory(ACCESSORIES));
        layoutCarriers.setOnClickListener(v -> toggleSideCategory(CARRIERS));
    }

    private void toggleTopCategory(TopCategory topCategory) {
        selectedTop = (selectedTop == topCategory) ? TopCategory.NONE : topCategory;
        updateTopUI();
        updateProductList();
    }

    private void toggleSideCategory(SideCategory sideCategory) {
        selectedSide = (selectedSide == sideCategory) ? SideCategory.NONE : sideCategory;
        updateSideUI();
        updateProductList();
    }

    private final Map<SideCategory, Integer> sideCategoryMap = new HashMap<SideCategory, Integer>() {{
        put(ALL, -1); // -1 để thể hiện "tất cả"
        put(FOOD, 1);
        put(PET_CARE, 2);
        put(SideCategory.FURNITURE, 3);
        put(TOYS, 4);
        put(ACCESSORIES, 5);
        put(CARRIERS, 6);
    }};

    private void updateTopUI() {
        if (selectedTop == TopCategory.DOGS) {
            txtForDogs.setTextColor(getColor(R.color.main_color));
            redBarDogs.setVisibility(View.VISIBLE);
        } else {
            txtForDogs.setTextColor(Color.BLACK);
            redBarDogs.setVisibility(View.INVISIBLE);
        }

        if (selectedTop == TopCategory.CATS) {
            txtForCats.setTextColor(getColor(R.color.main_color));
            redBarCats.setVisibility(View.VISIBLE);
        } else {
            txtForCats.setTextColor(Color.BLACK);
            redBarCats.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSideUI() {
        setSideActive(
                txtAllProducts, redBarAllProducts, imgAllProducts,
                selectedSide == ALL,
                R.mipmap.ic_all_product_red,
                R.mipmap.ic_all_product_black
        );
        setSideActive(
                txtFood, redBarFood, imgFood,
                selectedSide == FOOD,
                R.mipmap.ic_food_cate_red,
                R.mipmap.ic_food_cate_black
        );
        setSideActive(
                txtPetCare, redBarPetCare, imgPetCare,
                selectedSide == PET_CARE,
                R.mipmap.ic_petcare_cate_red,
                R.mipmap.ic_petcare_cate_black
        );
        setSideActive(
                txtFurniture, redBarFurniture, imgFurniture,
                selectedSide == SideCategory.FURNITURE,
                R.mipmap.ic_furniture_cate_red,
                R.mipmap.ic_furniture_cate_black
        );
        setSideActive(
                txtToys, redBarToys, imgToys,
                selectedSide == TOYS,
                R.mipmap.ic_toy_cate_red,
                R.mipmap.ic_toy_cate_black
        );
        setSideActive(
                txtAccesories, redBarAccessories, imgAccesories,
                selectedSide == ACCESSORIES,
                R.mipmap.ic_accessories_cate_red,
                R.mipmap.ic_accessories_cate_black
        );
        setSideActive(
                txtCarriers, redBarCarriers, imgCarriers,
                selectedSide == CARRIERS,
                R.mipmap.ic_kennels_cate_red,
                R.mipmap.ic_kennels_cate_black
        );
    }


    private void setSideActive(TextView textView, View redBar, ImageView iconView,
                               boolean isActive, int iconSelectedResId, int iconUnselectedResId) {
        textView.setTextColor(isActive ? getColor(R.color.main_color) : Color.GRAY);
        redBar.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
        iconView.setImageResource(isActive ? iconSelectedResId : iconUnselectedResId);
    }


//    private void updateProductList() {
//        // Xóa hết sản phẩm cũ trước khi thêm mới
//        productListLayout.removeAllViews();
//
//        for (Category category : listCategory.getCategories()) {
//
//            // 1. Nhóm sản phẩm theo child_category
//            Map<String, List<Product>> groupedProducts = new HashMap<>();
//            for (Product product : category.getProducts()) {
//                String childCategory = product.getChild_category();
//                if (!groupedProducts.containsKey(childCategory)) {
//                    groupedProducts.put(childCategory, new ArrayList<>());
//                }
//                groupedProducts.get(childCategory).add(product);
//            }
//
//            // 2. Duyệt từng nhóm child_category
//            for (Map.Entry<String, List<Product>> entry : groupedProducts.entrySet()) {
//                String childCategoryName = entry.getKey();
//                List<Product> products = entry.getValue();
//
//                // 2.1. Thêm tiêu đề cho nhóm sản phẩm (child_category)
//                TextView title = new TextView(this);
//                title.setText(childCategoryName);
//                title.setTextSize(20f);
//                title.setTextColor(Color.DKGRAY);
//                title.setTypeface(Typeface.defaultFromStyle(R.font.inter_bold), Typeface.BOLD);
//                title.setPadding(24, 48, 0, 16);
//                productListLayout.addView(title);
//
//                // 2.2. Tạo TableLayout cho sản phẩm
//                TableLayout tableLayout = new TableLayout(this);
//                tableLayout.setStretchAllColumns(true);
//
//                int count = 0;
//                TableRow row = new TableRow(this);
//
//                for (Product product : products) {
//                    View productView = createProductView(product);
//
//                    TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
//                    productView.setLayoutParams(params);
//
//                    row.addView(productView);
//                    count++;
//
//                    if (count % 2 == 0) {
//                        tableLayout.addView(row);
//                        row = new TableRow(this);
//                    }
//
//                    if (count == 6) {
//                        break;
//                    }
//                }
//
//                if (count % 2 != 0) {
//                    View emptyView = new View(this);
//                    TableRow.LayoutParams emptyParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
//                    emptyView.setLayoutParams(emptyParams);
//                    row.addView(emptyView);
//                    tableLayout.addView(row);
//                }
//
//
//                if (products.size() > 6) {
//                    TableRow seeAllRow = new TableRow(this);
//                    TextView seeAll = new TextView(this);
//                    seeAll.setText("See all");
//                    seeAll.setTextColor(Color.RED);
//                    seeAll.setTextSize(16f);
//                    seeAll.setPadding(16, 16, 16, 16);
//                    seeAll.setGravity(Gravity.CENTER);
//                    seeAllRow.addView(seeAll);
//                    tableLayout.addView(seeAllRow);
//                }
//
//                productListLayout.addView(tableLayout);
//
//                View divider = new View(this);
//                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, 2);
//                dividerParams.setMargins(0, 32, 0, 32); // Trên + Dưới 32px
//                divider.setLayoutParams(dividerParams);
//
//                productListLayout.addView(divider);
//            }
//        }
//    }
    private void updateProductList() {
        productListLayout.removeAllViews();

        if (selectedTop == TopCategory.NONE || selectedSide == SideCategory.NONE) return;

        String selectedAnimal = selectedTop == TopCategory.DOGS ? "DOG" : "CAT";
        Integer selectedCategoryId = sideCategoryMap.get(selectedSide); // null nếu ALL

        Map<String, List<Product>> groupedProducts = new HashMap<>();
        for (Category category : listCategory.getCategories()) {
            for (Product product : category.getProducts()) {
                if (!product.getAnimal_class().contains(selectedAnimal)) continue;
                if (selectedCategoryId != null && product.getCategory_id() != selectedCategoryId) continue;

                String childCategory = product.getChild_category();
                groupedProducts.putIfAbsent(childCategory, new ArrayList<>());
                groupedProducts.get(childCategory).add(product);
            }
        }

        if (groupedProducts.isEmpty()) return;

        for (Map.Entry<String, List<Product>> entry : groupedProducts.entrySet()) {
            String childCategoryName = entry.getKey();
            List<Product> products = entry.getValue();

            // Tiêu đề nhóm
            TextView title = new TextView(this);
            title.setText(childCategoryName);
            title.setTextSize(20f);
            title.setTextColor(Color.DKGRAY);
            title.setTypeface(Typeface.defaultFromStyle(R.font.inter_bold), Typeface.BOLD);
            title.setPadding(24, 48, 0, 16);
            productListLayout.addView(title);

            // TableLayout cho sản phẩm
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setStretchAllColumns(true);

            int count = 0;
            TableRow row = new TableRow(this);

            for (Product product : products) {
                View productView = createProductView(product);
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                productView.setLayoutParams(params);
                row.addView(productView);
                count++;

                if (count % 2 == 0) {
                    tableLayout.addView(row);
                    row = new TableRow(this);
                }

                if (count == 6) break;
            }

            if (count % 2 != 0) {
                View emptyView = new View(this);
                TableRow.LayoutParams emptyParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                emptyView.setLayoutParams(emptyParams);
                row.addView(emptyView);
                tableLayout.addView(row);
            }

            if (products.size() > 6) {
                TableRow seeAllRow = new TableRow(this);

                // Inflate giao diện từ see_all_item.xml
                View seeAllView = LayoutInflater.from(this).inflate(R.layout.see_all_item, seeAllRow, false);

                // Gán sự kiện click tại đây
                seeAllView.setOnClickListener(v -> {
                    // TODO: Viết xử lý khi click See All ở đây
                    Toast.makeText(this, "Clicked See All for " + childCategoryName, Toast.LENGTH_SHORT).show();
                    // Hoặc mở Activity mới / Dialog, tuỳ bạn
                });

                seeAllRow.addView(seeAllView);
                tableLayout.addView(seeAllRow);
            }


            productListLayout.addView(tableLayout);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.LTGRAY);
            productListLayout.addView(divider);
        }
    }





    private View createProductView(Product product) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_category, null);

        ImageView img = view.findViewById(R.id.img_cate_product);
        TextView txt = view.findViewById(R.id.txt_cate_product);

        txt.setText(product.getProduct_name()); // hoặc getTitle(), tuỳ cấu trúc của bạn

        // Nếu bạn có URL ảnh, dùng Glide (nếu chưa có thì dùng ảnh mẫu)
        // Glide.with(this).load(product.getImageUrl()).into(img);
        img.setImageResource(R.drawable.ic_launcher_foreground); // ảnh mẫu nếu chưa có URL

        return view;
    }
}
