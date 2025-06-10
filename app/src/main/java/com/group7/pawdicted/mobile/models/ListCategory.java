package com.group7.pawdicted.mobile.models;

import com.group7.pawdicted.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class ListCategory {

    private ArrayList<Category> categories;
    public ListCategory()
    {
        categories=new ArrayList<>();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
    public void addCategory (Category c)
    {
        categories.add(c);
    }

    private static Date getDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // fallback nếu lỗi
        }
    }
    public void generate_sample_dataset()
    {
       Category c1= new Category(1, "Food & Treats", "Nutrition and snacks for pets", R.mipmap.ic_food_cate_red, 1, Arrays.asList("food", "snacks", "treats"));
       Product p1= new Product("FT0009", "CIAO Tuna & Scallop Tuna & Scallop", 0, null,
                20000, "CIAO Tuna & Scallop Flavoured Packets...", "Weight: 60gr\nOrigin: Japan",
                4.8, 55, 61,  Set.of(Product.AnimalType.CAT),"Food","https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),2, getDate("2024-11-01"),5, 18000, 76);
       c1.addProduct(p1);
       categories.add(c1);

       Category c2= new Category(2, "Pet Care", "Health and hygiene care products", R.mipmap.ic_petcare_cate_red, 2, Arrays.asList("health", "hygiene", "cleaning"));
       Product p2 = new Product("PC0009", "BIOLINE Ear Care Ear Drops for Dogs and Cats", 0, null,
                150000, "BIOLINE Ear Care ear drops...", "Volumetric: 50ml",
                4.7, 198, 22, Set.of(Product.AnimalType.DOG, Product.AnimalType.CAT),"Health", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 2,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 2, getDate("2024-11-03"), 10, 13500, 120);

       c2.addProduct(p2);
       categories.add(c2);

       Category c3= new Category(3, "Furniture", "Pet furniture for comfort", R.mipmap.ic_furniture_cate_red, 3, Arrays.asList("bed", "sofa", "rest"));
       Product p3 = new Product("FU0010", "Sunny Daze Raised Air Dog Bed with UV Canopy Blue", 0, null,
                835000, "Sunny Daze Raised Dog Air Bed With UV Canopy...", "Suitable for: Dogs up to 31kg\nOne Size: H90 x W105 x D76cm",
                4.1, 12, 42, Set.of(Product.AnimalType.DOG),"Bed", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 3,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-05"), 8, 12000, 150);
       c3.addProduct(p3);
       categories.add(c3);

       Category c4= new Category(4, "Toys", "Entertainment and toys for pets", R.mipmap.ic_toy_cate_red, 4, Arrays.asList("toy", "play", "fun"));
       Product p4 = new Product("TO0011", "PAW Ultrasonic Dog Whistles", 0, null,
                65000, "PAW Ultrasonic Dog Whistles (automatic sound adjustment)...", "Brand: PAW\nColor: Silver\nLength: 67 mm",
                3.6, 50, 60, Set.of(Product.AnimalType.DOG, Product.AnimalType.CAT),"Training", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 4,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-07"), 50, 40000, 1004);

       c4.addProduct(p4);
       categories.add(c4);


       Category c5= new Category(5, "Accessories", "Collars, leashes and other accessories", R.mipmap.ic_accessories_cate_red, 5, Arrays.asList("collar", "leash", "wear"));
       Product p5 = new Product("AC0006", "Pets at Home Polka Dot Bow Pink Cat Collar", 0, null,
                70000, "Pets at Home Polka Dot Cat Collar in Pink...", "Brand: Pets at Home\nLength: 20-30 cm",
                5.0, 29, 150, Set.of(Product.AnimalType.CAT),"Collars & Leashes", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 5,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 2, getDate("2024-11-09"),0,70000, 8);

       c5.addProduct(p5);
       categories.add(c5);

       Category c6= new Category(6, "Carriers & Kennels", "Travel and portable homes", R.mipmap.ic_kennels_cate_red, 6, Arrays.asList("travel", "carrier", "kennel"));
       Product p6 = new Product("CK0005", "3 Peaks Multi-functional Travel Dog Bottle Black", 0, null,
                560000, "3 Peaks Water Bottle in durable stainless steel...", "Capacity: 350ml bottle / 160ml bowl",
                5.0, 87, 95, Set.of(Product.AnimalType.DOG), "Carriers","https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 6,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-11"), 45, 30000, 450);

       c6.addProduct(p6);
       categories.add(c6);
    }
}
