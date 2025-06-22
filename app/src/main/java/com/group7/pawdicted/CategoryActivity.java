package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group7.pawdicted.mobile.adapters.DividerItemDecoration;
import com.group7.pawdicted.mobile.adapters.ProductAdapter;
import com.group7.pawdicted.mobile.models.ChildCategory;
import com.group7.pawdicted.mobile.models.ListCategory;
import com.group7.pawdicted.mobile.models.ListChildCategory;
import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    FooterManager footerManager;
    private RecyclerView productList; // Ensure this is RecyclerView, not LinearLayout
    private ProductAdapter adapter;
    private int selectedAnimalClass = -2; // -2: none, -1: all, 0: cat, 1: dog
    private String selectedSideCategory = "ALL"; // ALL, FT, FU, PC, TO, AC, CK
    private String selectedChildCategoryId = null;
    private List<Object> displayItems;

    private ListChildCategory listChildCategory;
    private ListProduct listProduct;
    private ListCategory listCategory;

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

    private LinearLayout layoutAllProducts, layoutFood, layoutPetCare, layoutFurniture, layoutToys, layoutAccessories, layoutCarriers;

    private Map<Integer, String> layoutToCategoryMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        footerManager = new FooterManager(this);

        // Initialize data sources first
        listCategory = new ListCategory();
        listProduct = new ListProduct();
        listChildCategory = new ListChildCategory();

        addViews();
        initializeCategoryMap();
        addEvents();
    }

    private void initializeCategoryMap() {
        layoutToCategoryMap = new HashMap<>();
        layoutToCategoryMap.put(R.id.layout_all_products, "ALL");
        layoutToCategoryMap.put(R.id.layout_cate_food, "FT");
        layoutToCategoryMap.put(R.id.layout_cate_pet_care, "PC");
        layoutToCategoryMap.put(R.id.layout_cate_furniture, "FU");
        layoutToCategoryMap.put(R.id.layout_cate_toys, "TO");
        layoutToCategoryMap.put(R.id.layout_cate_accessories, "AC");
        layoutToCategoryMap.put(R.id.layout_cate_carriers, "CK");
    }

    private void addViews() {
        // Initialize views
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

        layoutAllProducts = findViewById(R.id.layout_all_products);
        layoutFood = findViewById(R.id.layout_cate_food);
        layoutPetCare = findViewById(R.id.layout_cate_pet_care);
        layoutFurniture = findViewById(R.id.layout_cate_furniture);
        layoutToys = findViewById(R.id.layout_cate_toys);
        layoutAccessories = findViewById(R.id.layout_cate_accessories);
        layoutCarriers = findViewById(R.id.layout_cate_carriers);

        // Correctly initialize productList as RecyclerView
        productList = findViewById(R.id.product_list);

        // Generate sample datasets
        try {
            listCategory.generate_sample_dataset();
            listProduct.generate_sample_dataset();
            listChildCategory.generate_sample_dataset();
        } catch (Exception e) {
            Log.e("CategoryActivity", "Error generating sample dataset", e);
        }
    }

    private void addEvents() {
        displayItems = new ArrayList<>();
        adapter = new ProductAdapter(this);
        productList.setLayoutManager(new GridLayoutManager(this, 2));
        productList.setAdapter(adapter);
//        productList.addItemDecoration(new DividerItemDecoration(this));

        // Ensure datasets are initialized
        if (listProduct.getProducts() == null || listChildCategory.getChildCategories() == null) {
            try {
                listProduct.generate_sample_dataset();
                listChildCategory.generate_sample_dataset();
            } catch (Exception e) {
                Log.e("CategoryActivity", "Error generating sample dataset in addEvents", e);
            }
        }

        layoutAllProducts.setSelected(true);
        selectedSideCategory = "ALL";
        setSideActive(txtAllProducts, redBarAllProducts, imgAllProducts, true, R.mipmap.ic_all_product_red, R.mipmap.ic_all_product_black);
        filterProducts();

        txtForDogs.setOnClickListener(v -> {
            if (txtForDogs.isSelected()) {
                txtForDogs.setSelected(false);
                txtForDogs.setTextColor(getColor(R.color.black));
                redBarDogs.setVisibility(View.GONE);
                selectedAnimalClass = -2;
            } else {
                txtForDogs.setSelected(true);
                txtForCats.setSelected(false);
                txtForDogs.setTextColor(getColor(R.color.main_color));
                txtForCats.setTextColor(getColor(R.color.black));
                redBarDogs.setVisibility(View.VISIBLE);
                redBarCats.setVisibility(View.GONE);
                selectedAnimalClass = 1;
            }
            filterProducts();
        });

        txtForCats.setOnClickListener(v -> {
            if (txtForCats.isSelected()) {
                txtForCats.setSelected(false);
                txtForCats.setTextColor(getColor(R.color.black));
                redBarCats.setVisibility(View.GONE);
                selectedAnimalClass = -2;
            } else {
                txtForCats.setSelected(true);
                txtForDogs.setSelected(false);
                txtForCats.setTextColor(getColor(R.color.main_color));
                txtForDogs.setTextColor(getColor(R.color.black));
                redBarCats.setVisibility(View.VISIBLE);
                redBarDogs.setVisibility(View.GONE);
                selectedAnimalClass = 0;
            }
            filterProducts();
        });

        layoutAllProducts.setOnClickListener(v -> {
            if (!layoutAllProducts.isSelected()) {
                resetSideCategories();
                layoutAllProducts.setSelected(true);
                setSideActive(txtAllProducts, redBarAllProducts, imgAllProducts, true, R.mipmap.ic_all_product_red, R.mipmap.ic_all_product_black);
                selectedSideCategory = "ALL";
                filterProducts();
            } else {
                layoutAllProducts.setSelected(false);
                setSideActive(txtAllProducts, redBarAllProducts, imgAllProducts, false, R.mipmap.ic_all_product_red, R.mipmap.ic_all_product_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

        layoutFood.setOnClickListener(v -> {
            if (!layoutFood.isSelected()) {
                resetSideCategories();
                layoutFood.setSelected(true);
                setSideActive(txtFood, redBarFood, imgFood, true, R.mipmap.ic_food_cate_red, R.mipmap.ic_food_cate_black);
                selectedSideCategory = "FT";
                filterProducts();
            } else {
                layoutFood.setSelected(false);
                setSideActive(txtFood, redBarFood, imgFood, false, R.mipmap.ic_food_cate_red, R.mipmap.ic_food_cate_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

        layoutPetCare.setOnClickListener(v -> {
            if (!layoutPetCare.isSelected()) {
                resetSideCategories();
                layoutPetCare.setSelected(true);
                setSideActive(txtPetCare, redBarPetCare, imgPetCare, true, R.mipmap.ic_petcare_cate_red, R.mipmap.ic_petcare_cate_black);
                selectedSideCategory = "PC";
                filterProducts();
            } else {
                layoutPetCare.setSelected(false);
                setSideActive(txtPetCare, redBarPetCare, imgPetCare, false, R.mipmap.ic_petcare_cate_red, R.mipmap.ic_petcare_cate_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

        layoutFurniture.setOnClickListener(v -> {
            if (!layoutFurniture.isSelected()) {
                resetSideCategories();
                layoutFurniture.setSelected(true);
                setSideActive(txtFurniture, redBarFurniture, imgFurniture, true, R.mipmap.ic_furniture_cate_red, R.mipmap.ic_furniture_cate_black);
                selectedSideCategory = "FU";
                filterProducts();
            } else {
                layoutFurniture.setSelected(false);
                setSideActive(txtFurniture, redBarFurniture, imgFurniture, false, R.mipmap.ic_furniture_cate_red, R.mipmap.ic_furniture_cate_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

        layoutToys.setOnClickListener(v -> {
            if (!layoutToys.isSelected()) {
                resetSideCategories();
                layoutToys.setSelected(true);
                setSideActive(txtToys, redBarToys, imgToys, true, R.mipmap.ic_toy_cate_red, R.mipmap.ic_toy_cate_black);
                selectedSideCategory = "TO";
                filterProducts();
            } else {
                layoutToys.setSelected(false);
                setSideActive(txtToys, redBarToys, imgToys, false, R.mipmap.ic_toy_cate_red, R.mipmap.ic_toy_cate_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

        layoutAccessories.setOnClickListener(v -> {
            if (!layoutAccessories.isSelected()) {
                resetSideCategories();
                layoutAccessories.setSelected(true);
                setSideActive(txtAccesories, redBarAccessories, imgAccesories, true, R.mipmap.ic_accessories_cate_red, R.mipmap.ic_accessories_cate_black);
                selectedSideCategory = "AC";
                filterProducts();
            } else {
                layoutAccessories.setSelected(false);
                setSideActive(txtAccesories, redBarAccessories, imgAccesories, false, R.mipmap.ic_accessories_cate_red, R.mipmap.ic_accessories_cate_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

        layoutCarriers.setOnClickListener(v -> {
            if (!layoutCarriers.isSelected()) {
                resetSideCategories();
                layoutCarriers.setSelected(true);
                setSideActive(txtCarriers, redBarCarriers, imgCarriers, true, R.mipmap.ic_kennels_cate_red, R.mipmap.ic_kennels_cate_black);
                selectedSideCategory = "CK";
                filterProducts();
            } else {
                layoutCarriers.setSelected(false);
                setSideActive(txtCarriers, redBarCarriers, imgCarriers, false, R.mipmap.ic_kennels_cate_red, R.mipmap.ic_kennels_cate_black);
                selectedSideCategory = null;
                filterProducts();
            }
        });

//        View.OnLongClickListener longClickListener = v -> {
//            String categoryId = layoutToCategoryMap.get(v.getId());
//            if (categoryId != null) {
//                Intent intent = new Intent(CategoryActivity.this, CategoryDetailsActivity.class);
//                intent.putExtra("category_id", categoryId);
//                startActivity(intent);
//            }
//            return true;
//        };

        adapter.setOnChildCategoryClickListener(childCategory -> {
            Intent intent = new Intent(CategoryActivity.this, CategoryDetailsActivity.class);
            intent.putExtra("child_category_id", childCategory.getChildCategory_id());
            intent.putExtra("category_id", childCategory.getCategory_id());
            intent.putExtra("animal_class", selectedAnimalClass);
            startActivity(intent);
        });

//        layoutAllProducts.setOnLongClickListener(longClickListener);
//        layoutFood.setOnLongClickListener(longClickListener);
//        layoutPetCare.setOnLongClickListener(longClickListener);
//        layoutFurniture.setOnLongClickListener(longClickListener);
//        layoutToys.setOnLongClickListener(longClickListener);
//        layoutAccessories.setOnLongClickListener(longClickListener);
//        layoutCarriers.setOnLongClickListener(longClickListener);
    }

    private void resetSideCategories() {
        layoutAllProducts.setSelected(false);
        layoutFood.setSelected(false);
        layoutPetCare.setSelected(false);
        layoutFurniture.setSelected(false);
        layoutToys.setSelected(false);
        layoutAccessories.setSelected(false);
        layoutCarriers.setSelected(false);

        setSideActive(txtAllProducts, redBarAllProducts, imgAllProducts, false, R.mipmap.ic_all_product_red, R.mipmap.ic_all_product_black);
        setSideActive(txtFood, redBarFood, imgFood, false, R.mipmap.ic_food_cate_red, R.mipmap.ic_food_cate_black);
        setSideActive(txtPetCare, redBarPetCare, imgPetCare, false, R.mipmap.ic_petcare_cate_red, R.mipmap.ic_petcare_cate_black);
        setSideActive(txtFurniture, redBarFurniture, imgFurniture, false, R.mipmap.ic_furniture_cate_red, R.mipmap.ic_furniture_cate_black);
        setSideActive(txtToys, redBarToys, imgToys, false, R.mipmap.ic_toy_cate_red, R.mipmap.ic_toy_cate_black);
        setSideActive(txtAccesories, redBarAccessories, imgAccesories, false, R.mipmap.ic_accessories_cate_red, R.mipmap.ic_accessories_cate_black);
        setSideActive(txtCarriers, redBarCarriers, imgCarriers, false, R.mipmap.ic_kennels_cate_red, R.mipmap.ic_kennels_cate_black);
    }

    private void filterProducts() {
        displayItems.clear();

        List<ChildCategory> childCategories = listChildCategory.getChildCategories();

        if (childCategories == null) {
            Log.e("CategoryActivity", "ChildCategories is null");
            return;
        }

        if (selectedSideCategory != null && !"ALL".equals(selectedSideCategory)) {
            for (ChildCategory childCategory : childCategories) {
                if (selectedSideCategory.equals(childCategory.getCategory_id())) {
                    displayItems.add(childCategory);
                    Log.d("CategoryActivity", "Added ChildCategory: " + childCategory.getChildCategory_name());
                }
            }
        } else {
            // Nếu chọn "ALL" hoặc không chọn SideCategory, hiển thị tất cả ChildCategory
            displayItems.addAll(childCategories);
        }

        Log.d("CategoryActivity", "DisplayItems size: " + displayItems.size());
        adapter.updateItems(displayItems);
    }

    private boolean matchesAnimalClass(Product product) {
        if (selectedAnimalClass == -2 || selectedAnimalClass == -1) {
            return true;
        }
        return product.getAnimal_class_id() == selectedAnimalClass ||
                product.getAnimal_class_id() == 2; // 2 might be for both cats and dogs
    }

    private void setSideActive(TextView textView, View redBar, ImageView iconView,
                               boolean isActive, int iconSelectedResId, int iconUnselectedResId) {
        textView.setTextColor(isActive ? getColor(R.color.main_color) : getColor(R.color.black));
        redBar.setVisibility(isActive ? View.VISIBLE : View.GONE);
        iconView.setImageResource(isActive ? iconSelectedResId : iconUnselectedResId);
    }
}