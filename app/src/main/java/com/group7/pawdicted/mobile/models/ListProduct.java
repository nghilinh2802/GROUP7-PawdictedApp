package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        List<String> variantIdsFT0001 = new ArrayList<>(Arrays.asList("FT00011", "FT00012"));
        products.add(new Product("FT0001", "Pedigree Chicken and Vegetables Adult Dog Dry Food ", variantIdsFT0001,
                 300.000, "Pedigree Chicken and Vegetables Adult Dog Dry Food is crafted to provide your adult dog a balanced and nutritious diet. This premium dog food combines the goodness of real chicken with wholesome vegetables, offering a delicious and complete meal that caters to your dog's daily nutritional needs. This formula supports strong muscles, a healthy immune system, and overall well-being with a blend of high-quality proteins, essential vitamins, and minerals.\n" +
                "\n" +
                "Designed to promote healthy digestion and coat health, Pedigree Chicken and Vegetables Adult Dog Dry Food ensures your dog enjoys every bite. The carefully selected ingredients not only enhance the flavor but also provide the necessary nutrients for sustained energy and vitality. This food is ideal for adult dogs of all breeds and sizes, making it a versatile choice for dog owners who want the best for their pets.\n" +
                "\n" +
                "Key Features\n" +
                "\n" +
                "Real Chicken and Vegetables: Made with high-quality chicken and vegetables to offer a nutritious and tasty meal.\n" +
                "\n" +
                "Balanced Nutrition: Provides essential vitamins, minerals, and proteins to support overall health and well-being.\n" +
                "\n" +
                "Healthy Digestion: Includes dietary fiber to promote good digestive health.\n" +
                "\n" +
                "Strong Muscles: High-quality protein content aids in maintaining strong and lean muscles.\n" +
                "\n" +
                "Immune System Support: Fortified with vitamins and antioxidants to strengthen the immune system.\n" +
                "\n" +
                "Versatile: Suitable for adult dogs of all breeds and sizes.\n" +
                "\n" +
                "Palatable Formula: Delicious taste that dogs love, ensuring they enjoy every meal.\n" +
                "\n" +
                "Ingredients:\n" +
                "\n" +
                "Cereals and Cereal by-products, Chicken and Chicken by-products and/or Meat and Meat by-products, Soybean Meal, Soybean Oil, Di-calcium phosphate, Iodised Salt, Antioxidants, Distilled Monoglycerides, Choline Chloride, Vitamins and Minerals, Carrot Powder, Peas Powder, Zinc Sulphate Monohydrate, Permitted Preservatives & Flavors",
                "\n" +
                        "Food type: Dry food\n" +
                        "Size/Weight: 20kg/15kg/10kg\n" +
                        "Ingredients: Chicken, vegetables\n" +
                        "Health Benefits: Supports immune system\n" +
                        "Storage: Cool, dry place\n" +
                        "Manufacturing Location: Thailand\n" +
                        "Brand: Pedigree\n" +
                        "Certifications: Certificate of Authenticity",
                3.5, 167, 50, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                1, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),2, getDate("2025-17-06"), 5,998 ));
        products.add(new Product("FT0009", "CIAO Tuna & Scallop", new ArrayList<>(), 20.000,
                "CIAO Tuna & Scallop Flavoured Packets...", "Weight: 60gr\nOrigin: Japan",
                4.8, 55, 61, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "FO", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 9, getDate("2024-11-01"), 10, 1000));

        // FT: TR (Treats)
        products.add(new Product("FT0015", "Chicken Jerky", new ArrayList<>(), 15.000,
                "Chicken Jerky Treats...", "Weight: 50gr\nOrigin: USA",
                4.6, 70, 80, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FT", "TR", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-07"), 25, 1200));

        // PC: HE (Health)
        products.add(new Product("PC0009", "BIOLINE Ear Care Ear Drops",  new ArrayList<>(), 150.000,
                "BIOLINE Ear Care ear drops...", "Volumetric: 50ml",
                4.7, 198, 22, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "HE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-03"), 5, 95));

        // PC: GR (Grooming)
        products.add(new Product("PC0015", "Pet Shampoo", new ArrayList<>(),  100.000,
                "Pet Shampoo for Shiny Coat...", "Volumetric: 200ml",
                4.6, 200, 60, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                2, "PC", "GR", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 3, getDate("2024-11-13"), 20, 400));

        // FU: BE (Bed)
        products.add(new Product("FU0009", "Sunny Daze Air Dog Bed", new ArrayList<>(), 1200.000,
                "Sunny Daze Raised Air Dog Bed...", "Size: Large",
                4.9, 178, 15, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FU", "BE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 5, getDate("2024-11-04"), 3, 50));

        // FU: CR (Crates, Houses & Pens)
        products.add(new Product("FU0010", "Pet Crate", new ArrayList<>(),  800.000,
                "Pet Crate for Safety...", "Size: Medium",
                4.7, 100, 20, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "FU", "CR", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, getDate("2024-11-14"), 5, 80));

        // TO: TY (Toys)
        products.add(new Product("TO0009", "PAW Ultrasonic Dog Whistles",new ArrayList<>(), 200.000,
                "PAW Ultrasonic Dog Whistles...", "Color: Silver",
                4.2, 230, 18, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "TO", "TY", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, getDate("2024-11-05"), 10, 120));

        // TO: TN (Training)
        products.add(new Product("TO0010", "Training Pad", new ArrayList<>(),  50.000,
                "Training Pad for Puppies...", "Size: Pack of 20",
                4.4, 150, 50, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "TO", "TN", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 10, getDate("2024-11-15"), 30, 500));

        // AC: CL (Collars & Leashes)
        products.add(new Product("AC0009", "Polka Dot Collar", new ArrayList<>(),  250.000,
                "Pets at Home Polka Dot Bow Pink Cat Collar...", "Color: Pink",
                4.0, 167, 25, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "AC", "CL", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 20, getDate("2024-11-06"), 8, 80));

        // AC: AC (Apparels & Costume)
        products.add(new Product("AC0010", "Pet Sweater", new ArrayList<>(), 300.000,
                "Pet Sweater for Winter...", "Size: Small",
                4.3, 120, 30, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "AC", "AC", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 15, getDate("2024-11-16"), 10, 100));

        // AC: FE (Feeders)
        products.add(new Product("AC0011", "Automatic Feeder", new ArrayList<>(), 500.000,
                "Automatic Pet Feeder...", "Capacity: 2L",
                4.5, 80, 15, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "AC", "FE", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 18, getDate("2024-11-17"), 5, 50));

        // CK: CA (Carriers)
        products.add(new Product("CK0009", "Travel Dog Bottle", new ArrayList<>(), 350.000,
                "3 Peaks Multi-functional Travel Dog Bottle...", "Color: Black",
                4.6, 145, 30, "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
                0, "CK", "CA", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 16, getDate("2024-11-07"), 5, 60));

        // CK: KE (Kennels)
        products.add(new Product("CK0010", "Pet Kennel", new ArrayList<>(),600.000,
                "Animology Clean Sheets are high performance, super tough, dual-sided dog cleaning wipes containing an advanced no-rinse shampoo formulation. Perfect for on-the-go cleaning and in-between washes to keep your dog cleaner and fresher for longer. Vitamin enriched, the wipes will also help to de-grease, deodorise and keep your dog's coat in good condition. Animology Clean Sheets have a textured side to rub and scrub at dirt, and a smooth side for gentler wiping. Infused with our 'Signature'scent. The 80-pack contains our extra large Clean Sheets which are 200 x 270mm approx.",
                "80 Pack\n" +
                        "How to use: Squeeze tub and remove lid. Peel off foil seal. Pull first wipe from the centre of the roll. Thread through opening and replace lid onto tub. The wipes will help to de-grease, deodorise and keep your dog's coat in good condition. Animology Clean Sheets have a textured side to rub and scrub at dirt, and a smooth side for gentler wiping.",
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