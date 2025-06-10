package com.group7.pawdicted.mobile.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public class ListProduct {
    ArrayList<Product> products;
    public ListProduct()
    {
        products=new ArrayList<>();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
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
        Product p1 = new Product(
                "FT0009",
                "CIAO Tuna & Scallop",
                0, null,
                20000,
                "CIAO Tuna & Scallop Flavoured Packets...",
                "Weight: 60gr\nOrigin: Japan",
                4.8,
                55,
                61,
                Set.of(Product.AnimalType.CAT),
                "Food",
                "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                2,
                getDate("2024-11-01"),
                5,
                18000,
                76
        );

        Product p2 = new Product("PC0009", "BIOLINE Ear Care Ear Drops for Dogs and Cats", 0, null,
                150000, "BIOLINE Ear Care ear drops...", "Volumetric: 50ml",
                4.7, 198, 22, Set.of(Product.AnimalType.DOG, Product.AnimalType.CAT),"Health", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 2,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 2, getDate("2024-11-03"), 10, 13500, 120);

        Product p3 = new Product("FU0010", "Sunny Daze Raised Air Dog Bed with UV Canopy Blue", 0, null,
                835000, "Sunny Daze Raised Dog Air Bed With UV Canopy...", "Suitable for: Dogs up to 31kg\nOne Size: H90 x W105 x D76cm",
                4.1, 12, 42, Set.of(Product.AnimalType.DOG),"Bed", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 3,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-05"), 8, 12000, 150);

        Product p4 = new Product("TO0011", "PAW Ultrasonic Dog Whistles", 0, null,
                65000, "PAW Ultrasonic Dog Whistles (automatic sound adjustment)...", "Brand: PAW\nColor: Silver\nLength: 67 mm",
                3.6, 50, 60, Set.of(Product.AnimalType.DOG, Product.AnimalType.CAT),"Training", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 4,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-07"), 50, 40000, 1004);

        Product p5 = new Product("AC0006", "Pets at Home Polka Dot Bow Pink Cat Collar", 0, null,
                70000, "Pets at Home Polka Dot Cat Collar in Pink...", "Brand: Pets at Home\nLength: 20-30 cm",
                5.0, 29, 150, Set.of(Product.AnimalType.CAT),"Collars & Leashes", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 5,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 2, getDate("2024-11-09"),0,70000, 8);

        Product p6 = new Product("CK0005", "3 Peaks Multi-functional Travel Dog Bottle Black", 0, null,
                560000, "3 Peaks Water Bottle in durable stainless steel...", "Capacity: 350ml bottle / 160ml bowl",
                5.0, 87, 95, Set.of(Product.AnimalType.DOG), "Carriers","https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 6,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-11"), 45, 30000, 450);

        products.add(p1);
        products.add(p2);
        products.add(p3);
        products.add(p4);
        products.add(p5);
        products.add(p6);

    }
   
}
