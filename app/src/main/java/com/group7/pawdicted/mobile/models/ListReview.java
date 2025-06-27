package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ListReview {
    private ArrayList<Review> reviews;
    private Random random = new Random();

    private List<String> customerIds = Arrays.asList(
            "0033cbce-20e5-4106-92fc-9f40aae2464c", "3175eda9-de0b-4104-86ae-31f2e00d6969",
            "320edde0-23ce-4f6f-aac2-4dee6c67fac2", "3bb4707a-334f-4083-a7a2-34c4639e7f2c",
            "3ec21d4b-29c6-4618-bc94-a49bb291a5d1", "5523eb10-397d-457a-847c-b68b73f3130a",
            "56130dfb-32f9-4a96-8310-72ac61397681", "6190919f-f9cb-4afc-8c8c-393689448f8a",
            "79a39073-7ae3-4ad1-a256-b7725dc76452", "7f9f46a1-5a03-44dc-ab7d-8702a50a42f8",
            "8b32b1ea-12ec-4ff2-b004-60e11236f5e6", "97f17b98-90e9-4250-933d-66b8d216c3a5",
            "EQVdA3FW7yPkKo5jqXAdyk3xpRB3", "OvSBfpxhqHcar9qS3hWkfNK6A6H2",
            "UDgakvhnVhZw4DNgBV9uCetwYL73", "a000b440-c977-41e6-a6f7-c5e07ac9c07e",
            "a079c73b-365c-4bf6-9048-8a6d0ef19f05", "a9706563-eea2-46f6-a0e3-022d36ea686d",
            "afa913e2-5ae0-4f92-9aef-00fca4916f2f", "bc768ae2-3634-439c-828c-6a35874652b1",
            "bfcaaadc-a2fe-4796-b0d2-799d4d84b35a", "c4ff70de-54f2-46e8-98a5-2f40846e1f0c",
            "d730f7d6-38f4-4bc7-809f-8abbe19467a3", "d9ab11d2-aa7d-4f35-a143-a475518590c5",
            "e41af502-94d1-4726-ba3f-fdd62d9d593a", "e7cc6ba1-fbc8-40a1-a4b2-b1ed136b5cb6",
            "ec6d52f4-fbcb-4823-896a-31bb580ff69a"
    );

    private List<String> productIds = Arrays.asList(
            "FT0001", "FT0002", "FT0003", "FT0004", "FT0005", "FT0006", "FT0007", "FT0008", "FT0009",
            "FT0010", "FT0011", "FT0012", "FT0013", "FT0014", "FT0015", "FT0016", "FT0017", "FT0018",
            "FT0019", "FT0020",
            "PC0001", "PC0002", "PC0003", "PC0004", "PC0005", "PC0006", "PC0007", "PC0008", "PC0009",
            "PC0010", "PC0011", "PC0012", "PC0013", "PC0014", "PC0015", "PC0016", "PC0017", "PC0018",
            "PC0019", "PC0020", "PC0021", "PC0022", "PC0023", "PC0024", "PC0025",
            "AC0001", "AC0002", "AC0003", "AC0004", "AC0005", "AC0006", "AC0007", "AC0008", "AC0009",
            "AC0010", "AC0011", "AC0012", "AC0013", "AC0014", "AC0015", "AC0016", "AC0017", "AC0018",
            "AC0019", "AC0020", "AC0021", "AC0022", "AC0023", "AC0024", "AC0025", "AC0026", "AC0027",
            "AC0028", "AC0029", "AC0030",
            "FU0001", "FU0002", "FU0003", "FU0004", "FU0005", "FU0006", "FU0007", "FU0008", "FU0009",
            "FU0010", "FU0011", "FU0012", "FU0013", "FU0014", "FU0015", "FU0016", "FU0017", "FU0018",
            "FU0019", "FU0020", "FU0021", "FU0022", "FU0023", "FU0024", "FU0025",
            "TO0001", "TO0002", "TO0003", "TO0004", "TO0005", "TO0006", "TO0007", "TO0008", "TO0009",
            "TO0010", "TO0011", "TO0012", "TO0013", "TO0014", "TO0015", "TO0016", "TO0017", "TO0018",
            "TO0019", "TO0020", "TO0021", "TO0022", "TO0023", "TO0024", "TO0025",
            "CK0001", "CK0002", "CK0003", "CK0004", "CK0005", "CK0006", "CK0007", "CK0008", "CK0009",
            "CK0010", "CK0011", "CK0012", "CK0013", "CK0014", "CK0015", "CK0016", "CK0017", "CK0018",
            "CK0019", "CK0020", "CK0021", "CK0022", "CK0023", "CK0024", "CK0025"
    );

    public ListReview() {
        reviews = new ArrayList<>();
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    private long getTimestamp(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    public void generate_sample_dataset() {
        reviews.clear();

        // Phân bổ khách hàng theo danh mục
        Map<String, List<String>> categoryCustomers = new HashMap<>();
        categoryCustomers.put("FT", Arrays.asList(
                "0033cbce-20e5-4106-92fc-9f40aae2464c", "3175eda9-de0b-4104-86ae-31f2e00d6969",
                "320edde0-23ce-4f6f-aac2-4dee6c67fac2", "3bb4707a-334f-4083-a7a2-34c4639e7f2c",
                "3ec21d4b-29c6-4618-bc94-a49bb291a5d1"));
        categoryCustomers.put("PC", Arrays.asList(
                "5523eb10-397d-457a-847c-b68b73f3130a", "56130dfb-32f9-4a96-8310-72ac61397681",
                "6190919f-f9cb-4afc-8c8c-393689448f8a", "79a39073-7ae3-4ad1-a256-b7725dc76452",
                "7f9f46a1-5a03-44dc-ab7d-8702a50a42f8"));
        categoryCustomers.put("AC", Arrays.asList(
                "8b32b1ea-12ec-4ff2-b004-60e11236f5e6", "97f17b98-90e9-4250-933d-66b8d216c3a5",
                "EQVdA3FW7yPkKo5jqXAdyk3xpRB3", "OvSBfpxhqHcar9qS3hWkfNK6A6H2",
                "UDgakvhnVhZw4DNgBV9uCetwYL73"));
        categoryCustomers.put("FU", Arrays.asList(
                "a000b440-c977-41e6-a6f7-c5e07ac9c07e", "a079c73b-365c-4bf6-9048-8a6d0ef19f05",
                "a9706563-eea2-46f6-a0e3-022d36ea686d", "afa913e2-5ae0-4f92-9aef-00fca4916f2f"));
        categoryCustomers.put("TO", Arrays.asList(
                "bc768ae2-3634-439c-828c-6a35874652b1", "bfcaaadc-a2fe-4796-b0d2-799d4d84b35a",
                "c4ff70de-54f2-46e8-98a5-2f40846e1f0c", "d730f7d6-38f4-4bc7-809f-8abbe19467a3"));
        categoryCustomers.put("CK", Arrays.asList(
                "d9ab11d2-aa7d-4f35-a143-a475518590c5", "e41af502-94d1-4726-ba3f-fdd62d9d593a",
                "e7cc6ba1-fbc8-40a1-a4b2-b1ed136b5cb6", "ec6d52f4-fbcb-4823-896a-31bb580ff69a"));

        // Tạo danh sách sản phẩm theo danh mục
        Map<String, List<String>> categoryProducts = new HashMap<>();
        for (String category : Arrays.asList("FT", "PC", "AC", "FU", "TO", "CK")) {
            categoryProducts.put(category, new ArrayList<>());
        }
        for (String productId : productIds) {
            String category = productId.substring(0, 2);
            categoryProducts.get(category).add(productId);
        }

        // Tạo dữ liệu mua (giả lập also_buy)
        Map<String, List<String>> customerBuys = new HashMap<>();
        for (String customerId : customerIds) {
            String primaryCategory = null;
            for (Map.Entry<String, List<String>> entry : categoryCustomers.entrySet()) {
                if (entry.getValue().contains(customerId)) {
                    primaryCategory = entry.getKey();
                    break;
                }
            }
            List<String> boughtProducts = new ArrayList<>();
            List<String> primaryProducts = new ArrayList<>(categoryProducts.get(primaryCategory));
            Collections.shuffle(primaryProducts, random);
            for (int i = 0; i < Math.min(3, primaryProducts.size()); i++) {
                boughtProducts.add(primaryProducts.get(i));
            }
            customerBuys.put(customerId, boughtProducts);
        }

        // Tạo ánh xạ biến thể
        Map<String, Integer> productCounts = new HashMap<>();
        for (String productId : productIds) {
            productCounts.put(productId, productCounts.getOrDefault(productId, 0) + 1);
        }

        Map<String, String> variantMapping = new HashMap<>();
        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
            String productId = entry.getKey();
            int count = entry.getValue();
            if (count > 1) {
                for (int i = 1; i <= count; i++) {
                    variantMapping.put(productId + "-" + (i - 1), productId + i);
                }
            } else {
                variantMapping.put(productId + "-0", null);
            }
        }

        // Danh sách comment
        Map<Integer, List<String>> comments = new HashMap<>();
        comments.put(5, Arrays.asList(
                "This product completely transformed my pet's energy levels! Highly recommended.",
                "Amazing quality, my dog loves it and I see a big difference!",
                "Perfect for my pet, couldn't be happier with this purchase!",
                "My cat devoured it in seconds! Will buy again.",
                "Fantastic treat for training. No stomach issues so far."
        ));
        comments.put(4, Arrays.asList(
                "Really good product, but the delivery took a bit long.",
                "My pet enjoys this, though the packaging could be better.",
                "Solid choice for my pet, but I wish it was a bit cheaper.",
                "Good size and easy to clean. Worth the price.",
                "Nice quality fabric. Slightly tight fit."
        ));
        comments.put(3, Arrays.asList(
                "Decent product, but it didn't quite meet my expectations.",
                "Okay quality, my pet uses it but nothing special.",
                "Works fine, but needs some improvements.",
                "Not bad but wish the pieces were larger.",
                "A few issues with durability, but still usable."
        ));

        // Tạo 81 review
        long startTime = 1622505600000L; // 1/6/2025
        long endTime = 1624838400000L; // 27/6/2025
        int reviewIndex = 1;

        for (String customerId : customerBuys.keySet()) {
            List<String> boughtProducts = customerBuys.get(customerId);
            Collections.shuffle(boughtProducts, random);
            for (int i = 0; i < Math.min(3, boughtProducts.size()); i++) {
                String productId = boughtProducts.get(i);
                String reviewId = String.format("R%03d", reviewIndex++);
                int rating = random.nextInt(3) + 3; // 3-5
                String productVariation = variantMapping.get(productId + "-0");
                String comment = comments.get(rating).get(random.nextInt(comments.get(rating).size()));
                long timestamp = startTime + (long) (random.nextDouble() * (endTime - startTime));

                reviews.add(new Review(reviewId, customerId, rating, productId, productVariation, comment, timestamp));
            }
        }
    }
}