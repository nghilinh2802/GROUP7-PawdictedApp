package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Date;

public class ListProduct {
    private ArrayList<Product> products;

    public ListProduct() {
        products = new ArrayList<>();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void generate_sample_dataset() {
        products.clear();

        // FT: FO (Food)
        products.add(new Product("FT0009", "CIAO Tuna & Scallop", "", "", 20.000,
                "CIAO Tuna & Scallop Flavoured Packets...", "Weight: 60gr\nOrigin: Japan",
                4.8, 55, 61, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-01"), 10, 1000));
        products.add(new Product("FT0010", "Tuna Delight", "", "", 25.000,
                "Tuna Delight Packets...", "Weight: 70gr\nOrigin: Japan",
                4.7, 60, 50, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-02"), 15, 800));
        products.add(new Product("FT0011", "Salmon Treat", "", "", 22.000,
                "Salmon Treat Packets...", "Weight: 65gr\nOrigin: Japan",
                4.6, 45, 70, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-03"), 12, 900));
        products.add(new Product("FT0012", "Chicken Snack", "", "", 18.000,
                "Chicken Snack Packets...", "Weight: 55gr\nOrigin: Japan",
                4.5, 50, 60, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-04"), 20, 1000));
        products.add(new Product("FT0013", "Beef Bite", "", "", 20.000,
                "Beef Bite Packets...", "Weight: 60gr\nOrigin: Japan",
                4.8, 55, 65, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-05"), 10, 950));
        products.add(new Product("FT0014", "Fish Crunch", "", "", 23.000,
                "Fish Crunch Packets...", "Weight: 68gr\nOrigin: Japan",
                4.7, 40, 55, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-06"), 8, 850));

        // FT: TR (Treats)
        products.add(new Product("FT0015", "Chicken Jerky", "", "", 15.000,
                "Chicken Jerky Treats...", "Weight: 50gr\nOrigin: USA",
                4.6, 70, 80, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "TR", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-07"), 25, 1200));

        // PC: HE (Health)
        products.add(new Product("PC0009", "BIOLINE Ear Care Ear Drops", "", "", 150.000,
                "BIOLINE Ear Care ear drops...", "Volumetric: 50ml",
                4.7, 198, 22, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-03"), 5, 95));
        products.add(new Product("PC0010", "Flea & Tick Spray", "", "", 120.000,
                "Flea & Tick Spray...", "Volumetric: 100ml",
                4.5, 150, 30, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-08"), 10, 200));
        products.add(new Product("PC0011", "Vitamin Supplement", "", "", 180.000,
                "Vitamin Supplement for Pets...", "Weight: 100gr",
                4.8, 100, 40, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-09"), 8, 150));
        products.add(new Product("PC0012", "Dental Chews", "", "", 90.000,
                "Dental Chews for Pets...", "Weight: 80gr",
                4.6, 120, 50, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-10"), 15, 300));
        products.add(new Product("PC0013", "Joint Support", "", "", 200.000,
                "Joint Support Supplement...", "Weight: 120gr",
                4.7, 80, 25, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-11"), 5, 100));
        products.add(new Product("PC0014", "Skin Care Lotion", "", "", 130.000,
                "Skin Care Lotion for Pets...", "Volumetric: 75ml",
                4.5, 90, 35, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-12"), 12, 250));

        // PC: GR (Grooming)
        products.add(new Product("PC0015", "Pet Shampoo", "", "", 100.000,
                "Pet Shampoo for Shiny Coat...", "Volumetric: 200ml",
                4.6, 200, 60, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "GR", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-13"), 20, 400));

        // FU: BE (Bed)
        products.add(new Product("FU0009", "Sunny Daze Air Dog Bed", "", "", 1200.000,
                "Sunny Daze Raised Air Dog Bed...", "Size: Large",
                4.9, 178, 15, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FU", "BE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-04"), 3, 50));

        // FU: CR (Crates, Houses & Pens)
        products.add(new Product("FU0010", "Pet Crate", "", "", 800.000,
                "Pet Crate for Safety...", "Size: Medium",
                4.7, 100, 20, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FU", "CR", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-14"), 5, 80));

        // TO: TY (Toys)
        products.add(new Product("TO0009", "PAW Ultrasonic Dog Whistles", "", "", 200.000,
                "PAW Ultrasonic Dog Whistles...", "Color: Silver",
                4.2, 230, 18, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "TO", "TY", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-05"), 10, 120));

        // TO: TN (Training)
        products.add(new Product("TO0010", "Training Pad", "", "", 50.000,
                "Training Pad for Puppies...", "Size: Pack of 20",
                4.4, 150, 50, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "TO", "TN", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-15"), 30, 500));

        // AC: CL (Collars & Leashes)
        products.add(new Product("AC0009", "Polka Dot Collar", "", "", 250.000,
                "Pets at Home Polka Dot Bow Pink Cat Collar...", "Color: Pink",
                4.0, 167, 25, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "AC", "CL", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-06"), 8, 80));

        // AC: AC (Apparels & Costume)
        products.add(new Product("AC0010", "Pet Sweater", "", "", 300.000,
                "Pet Sweater for Winter...", "Size: Small",
                4.3, 120, 30, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "AC", "AC", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-16"), 10, 100));

        // AC: FE (Feeders)
        products.add(new Product("AC0011", "Automatic Feeder", "", "", 500.000,
                "Automatic Pet Feeder...", "Capacity: 2L",
                4.5, 80, 15, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "AC", "FE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-17"), 5, 50));

        // CK: CA (Carriers)
        products.add(new Product("CK0009", "Travel Dog Bottle", "", "", 350.000,
                "3 Peaks Multi-functional Travel Dog Bottle...", "Color: Black",
                4.6, 145, 30, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "CK", "CA", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-07"), 5, 60));

        // CK: KE (Kennels)
        products.add(new Product("CK0010", "Pet Kennel", "", "", 600.000,
                "Pet Kennel for Travel...", "Size: Large",
                4.8, 90, 20, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "CK", "KE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-18"), 3, 40));
    }

    private Date getDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }
}