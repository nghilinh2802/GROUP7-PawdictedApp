package com.group7.pawdicted.mobile.models;

import java.util.ArrayList;
import java.util.Date;

public class ListReview {
    private ArrayList<Review> reviews;

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
        reviews.add(new Review("R001", "C001", 5, "FT0001", "FT00011",
                "This dry food completely transformed my dog's energy levels! Highly recommended.",
                getTimestamp("2024-05-12")));

        reviews.add(new Review("R002", "C002", 4, "FT0001", "FT00012",
                "My dog likes the flavor, but I wish the packaging was resealable.",
                getTimestamp("2024-05-13")));

        reviews.add(new Review("R003", "C003", 5, "FT0001", "FT00011",
                "Affordable, nutritious, and my dog never skips a meal with it.",
                getTimestamp("2024-05-14")));

        reviews.add(new Review("R004", "C004", 3, "FT0001", "FT00012",
                "It's okay, but caused a bit of digestion issue initially.",
                getTimestamp("2024-05-15")));
        reviews.add(new Review("R005", "C001", 5, "FT0009", "",
                "My cat devoured it in seconds! Will buy again.",
                getTimestamp("2024-05-12")));

        reviews.add(new Review("R006", "C002", 4, "FT0009", "",
                "Great treat, but slightly pricey for the portion.",
                getTimestamp("2024-05-13")));

        reviews.add(new Review("R007", "C003", 5, "FT0009", "",
                "CIAO never disappoints. Smells fresh, and my cat is always excited.",
                getTimestamp("2024-05-14")));

        reviews.add(new Review("R008", "C005", 4, "FT0009", "",
                "Very convenient packaging. My kitten loves the scallop flavor.",
                getTimestamp("2024-05-15")));
        reviews.add(new Review("R009", "C001", 4, "FT0015", "",
                "Nice chewy texture. My puppy treats it as a reward.",
                getTimestamp("2024-05-16")));

        reviews.add(new Review("R010", "C002", 5, "FT0015", "",
                "Absolutely love this product. 100% natural and my dog is happy.",
                getTimestamp("2024-05-17")));

        reviews.add(new Review("R011", "C003", 3, "FT0015", "",
                "Not bad but wish the jerky pieces were larger.",
                getTimestamp("2024-05-18")));

        reviews.add(new Review("R012", "C004", 5, "FT0015", "",
                "Fantastic treat for training. No stomach issues so far.",
                getTimestamp("2024-05-19")));
        reviews.add(new Review("R013", "C001", 5, "PC0009", "", "Works great for cleaning my dog's ears. Smells fresh too.", getTimestamp("2024-05-20")));
        reviews.add(new Review("R014", "C003", 4, "PC0009", "", "Helped reduce itching within a few uses. Good quality.", getTimestamp("2024-05-21")));
        reviews.add(new Review("R015", "C004", 5, "PC0009", "", "Highly effective and vet-recommended. Will buy again.", getTimestamp("2024-05-22")));
        reviews.add(new Review("R016", "C005", 3, "PC0009", "", "Works fine, but applicator could be improved.", getTimestamp("2024-05-23")));

        reviews.add(new Review("R017", "C001", 4, "PC0015", "", "Left my cat's coat silky smooth and smells amazing.", getTimestamp("2024-05-24")));
        reviews.add(new Review("R018", "C002", 5, "PC0015", "", "No irritation at all. Lathers well and cleans thoroughly.", getTimestamp("2024-05-25")));
        reviews.add(new Review("R019", "C003", 3, "PC0015", "", "It’s okay, but didn't help much with shedding.", getTimestamp("2024-05-26")));
        reviews.add(new Review("R020", "C004", 5, "PC0015", "", "Perfect for my golden retriever. Highly recommend.", getTimestamp("2024-05-27")));

        reviews.add(new Review("R021", "C001", 5, "FU0009", "", "My dog loves this bed. Super comfy and durable.", getTimestamp("2024-05-28")));
        reviews.add(new Review("R022", "C003", 4, "FU0009", "", "Good size and easy to clean. Worth the price.", getTimestamp("2024-05-29")));
        reviews.add(new Review("R023", "C004", 5, "FU0009", "", "It’s elevated design keeps my dog cool.", getTimestamp("2024-05-30")));
        reviews.add(new Review("R024", "C005", 4, "FU0009", "", "Solid frame and great for summer use.", getTimestamp("2024-05-31")));

        reviews.add(new Review("R025", "C001", 5, "FU0010", "", "Secure and sturdy. My dog feels safe in it.", getTimestamp("2024-06-01")));
        reviews.add(new Review("R026", "C002", 3, "FU0010", "", "Could use better handles, but otherwise fine.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R027", "C004", 4, "FU0010", "", "Easy to fold and store. Space saver!", getTimestamp("2024-06-02")));
        reviews.add(new Review("R028", "C005", 5, "FU0010", "", "Perfect for travel. Lightweight yet strong.", getTimestamp("2024-06-02")));

        reviews.add(new Review("R029", "C001", 5, "TO0009", "", "Effective in getting my dog’s attention during training.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R030", "C003", 4, "TO0009", "", "Well-made and affordable. Does the job.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R031", "C004", 4, "TO0009", "", "Takes some practice, but works well.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R032", "C005", 5, "TO0009", "", "Great tool for training sessions. Compact and loud enough.", getTimestamp("2024-06-02")));

        reviews.add(new Review("R033", "C001", 4, "TO0010", "", "Absorbs well and no leakage. Good product.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R034", "C002", 5, "TO0010", "", "My puppy learned fast with these. Recommended!", getTimestamp("2024-06-02")));
        reviews.add(new Review("R035", "C003", 4, "TO0010", "", "Good size and odor control. Will repurchase.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R036", "C005", 3, "TO0010", "", "A few pads tore during use, but still useful.", getTimestamp("2024-06-02")));

        reviews.add(new Review("R037", "C001", 5, "AC0009", "", "Super cute and fits perfectly on my cat.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R038", "C002", 4, "AC0009", "", "Love the design. Material is comfortable.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R039", "C004", 3, "AC0009", "", "Looks good, but the clip isn’t very sturdy.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R040", "C005", 5, "AC0009", "", "Easy to put on and doesn’t irritate my pet.", getTimestamp("2024-06-02")));

        reviews.add(new Review("R041", "C001", 5, "AC0010", "", "Keeps my pup warm and looks adorable.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R042", "C003", 4, "AC0010", "", "Nice quality fabric. Slightly tight fit.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R043", "C004", 5, "AC0010", "", "Stylish and cozy. Great for winter walks.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R044", "C005", 3, "AC0010", "", "Colors faded after one wash, otherwise great.", getTimestamp("2024-06-02")));

        reviews.add(new Review("R045", "C001", 4, "AC0011", "", "Helpful when I’m away. Dispenses reliably.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R046", "C002", 5, "AC0011", "", "No spills, easy to program. Time saver!", getTimestamp("2024-06-02")));
        reviews.add(new Review("R047", "C003", 4, "AC0011", "", "Setup took time, but works flawlessly.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R048", "C004", 5, "AC0011", "", "Love it! My cat waits excitedly every day.", getTimestamp("2024-06-02")));

        reviews.add(new Review("R049", "C001", 5, "CK0009", "", "So convenient for walks. No leaks at all!", getTimestamp("2024-06-02")));
        reviews.add(new Review("R050", "C002", 4, "CK0009", "", "Fits well in my backpack. Great design.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R051", "C003", 5, "CK0009", "", "Very handy. Easy for my dog to drink.", getTimestamp("2024-06-02")));
        reviews.add(new Review("R052", "C005", 4, "CK0009", "", "Good bottle, wish it came in bigger size.", getTimestamp("2024-06-02")));


    }
}
