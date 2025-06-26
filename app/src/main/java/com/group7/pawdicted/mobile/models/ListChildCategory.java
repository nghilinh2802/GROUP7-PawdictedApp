package com.group7.pawdicted.mobile.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListChildCategory implements Serializable {
    private ArrayList<ChildCategory> childCategories;
    public ListChildCategory()
    {
        childCategories=new ArrayList<>();
    }

    public ArrayList<ChildCategory> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(ArrayList<ChildCategory> childCategories) {
        this.childCategories = childCategories;
    }

    public void addChildCategory (ChildCategory cc)
    {
        childCategories.add(cc);
    }

    public void generate_sample_dataset()
    {
        // Food & Treats
        ChildCategory cc1 = new ChildCategory("DF", "Dry Food", "Dry Food for pets. So marvelous",
                "https://scrumbles.co.uk/cdn/shop/files/chicken-dry-dog-fooddry-dog-foodscrumbles-natural-pet-fooddac2-799661.png?v=1748194581",
                2, "FT");
        ChildCategory cc2 = new ChildCategory("WF", "Wet Food", "Wet Food for pets. So marvelous",
                "https://catsmart.com.sg/image/cache/catalog/Purina-One-Wet-Food-Pouch-Indoor-Advantage-85g-12-packs-500x500.png",
                2, "FT");
        ChildCategory cc3 = new ChildCategory("TR", "Treats", "Treats for my pets. Funny",
                "https://www.petmart.vn/wp-content/uploads/2014/10/banh-thuong-cho-meo-vi-gan-thit-vegebrand-orgo-cat-treats-poultry-liver.jpg",
                5, "FT");
        childCategories.add(cc1);
        childCategories.add(cc2);
        childCategories.add(cc3);

        // Pet Care
        ChildCategory cc4 = new ChildCategory("DC", "Dental Care", "Dental care products for pets",
                "https://flipfit-cdn.akamaized.net/flipfit-prod-tmp/items/1725441737899-440392661WHI1V1.webp",
                7, "PC");
        ChildCategory cc5 = new ChildCategory("SV", "Supplements & Vitamins", "Supplements and vitamins for pets",
                "https://www.paramountpethealth.com/cdn/shop/products/liquidvitaminsfordogs.jpg?v=1746552547",
                4, "PC");
        ChildCategory cc6 = new ChildCategory("FT", "Flea & Tick Control", "Flea and tick control products",
                "https://mypetlife.co/cdn/shop/files/Flea_Tick_Daily_Spray_PDP_6.12.24_1-100.jpg?v=1718373670&width=1500",
                8, "PC");
        ChildCategory cc7 = new ChildCategory("SC", "Shampoos & Conditioners", "Shampoos and conditioners for pets",
                "https://image-general.sittovietnam.vn/uploads/images/0554F173-26BE-4A13-8499-94A96777B1FA/9a015a73-3e9f-44de-934b-68c41b832bf0.png",
                6, "PC");
        ChildCategory cc8 = new ChildCategory("BC", "Brushes & Combs", "Brushes and combs for pet grooming",
                "https://www.hartz.com/wp-content/uploads/2022/07/3270083801_hartz-groomers-best-combo-brush-for-dogs-front-1300x1300-1.jpg",
                9, "PC");
        ChildCategory cc9 = new ChildCategory("NC", "Nail Care", "Nail care products for pets",
                "https://thestockshop.org/cdn/shop/files/unnamed_ac49b6bc-8714-48a8-a581-035296f3ef55_500x500.jpg?v=1742576283",
                10, "PC");
        ChildCategory cc10 = new ChildCategory("DT", "Deodorant Tools", "Deodorant tools for pets",
                "https://petsup.ph/cdn/shop/files/deo-01_0c9e2370-2788-44a1-9bd3-d76dbc47c989.jpg?v=1707706543&width=1920",
                11, "PC");
        childCategories.add(cc4);
        childCategories.add(cc5);
        childCategories.add(cc6);
        childCategories.add(cc7);
        childCategories.add(cc8);
        childCategories.add(cc9);
        childCategories.add(cc10);

        // Toys
        ChildCategory cc11 = new ChildCategory("TY", "Toys", "Toys for pets. So marvelous",
                "https://i5.walmartimages.com/seo/Multipet-Smiling-Loofa-Plush-Dog-Toy-6-inch-Colorful-Plaid_6ca8c26c-4432-4478-98ff-6750ed30d291.0fad83d63ec96ec8263c8b3b800374e2.jpeg?odnHeight=320&odnWidth=320&odnBg=FFFFFF",
                6, "TO");
        ChildCategory cc12 = new ChildCategory("TN", "Training", "Training for my pets. Funny",
                "https://www.petland.ca/cdn/shop/files/company-of-animals-company-of-animals-pet-corrector-training-tool-29647804792934.png?v=1691084887",
                8, "TO");
        childCategories.add(cc11);
        childCategories.add(cc12);

        // Accessories
        ChildCategory cc13 = new ChildCategory("CL", "Collars & Leashes", "Collars and leashes for pets",
                "https://m.media-amazon.com/images/I/71RgH9AogIL._AC_SL1500_.jpg",
                12, "AC");
        ChildCategory cc14 = new ChildCategory("AC", "Apparel & Costume", "Apparel and costumes for pets",
                "https://images.halloweencostumes.eu/products/15022/1-1/holy-hound-pet-costume.jpg",
                11, "AC");
        ChildCategory cc15 = new ChildCategory("FE", "Feeders", "Feeders for my pets. Funny",
                "https://m.media-amazon.com/images/I/61gueG5uX1L.jpg",
                13, "AC");
        childCategories.add(cc13);
        childCategories.add(cc14);
        childCategories.add(cc15);

        // Furniture
        ChildCategory cc16 = new ChildCategory("BE", "Bedding", "Bedding for pets. So marvelous",
                "https://www.vietpet.net/wp-content/uploads/2020/08/o-dem-cho-cho-meo-kh-pet-products-bolster-cat-dog-bed.jpg",
                3, "FU");
        ChildCategory cc17 = new ChildCategory("CH", "Crates, Houses & Pens", "Crates, houses, and pens for pets",
                "https://dogo.app/wp-content/uploads/2025/03/91DcUjbqWEL._AC_SL1500_.jpg",
                1, "FU");
        childCategories.add(cc16);
        childCategories.add(cc17);

        // Carriers & Kennels
        ChildCategory cc18 = new ChildCategory("CA", "Carriers", "Carriers for pets. So marvelous",
                "https://img.lazcdn.com/g/p/f0a6adea2b9541d3a4816e64033dbc8c.jpg_360x360q75.jpg",
                10, "CK");
        ChildCategory cc19 = new ChildCategory("KE", "Kennels", "Kennels for my pets. Funny",
                "https://www.petjoint.com.au/cdn/shop/products/wooden_dog_kennel_medium_a3f86c94-dcf1-4b55-b4ca-2f309ac8fb50.jpg?v=1527261808",
                9, "CK");
        childCategories.add(cc18);
        childCategories.add(cc19);
    }

}


