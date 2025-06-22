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
        ChildCategory cc1= new ChildCategory("FO", "Food", "Food for pets. So marvelous",
                "https://m.media-amazon.com/images/I/71iGPCE0igL._SL1500_.jpg",
                2, "FT");
        ChildCategory cc2 = new ChildCategory("TR", "Treats", " Treats for my pets. Funny",
                "https://m.media-amazon.com/images/I/81s41RQ4pIL._AC_SL1500_.jpg",
                5, "FT");
        childCategories.add(cc1);
        childCategories.add(cc2);

        ChildCategory cc3= new ChildCategory("HE", "Health", "Good for your health. So good for jo pets. So marvelous",
                "https://www.paramountpethealth.com/cdn/shop/products/liquidvitaminsfordogs.jpg?v=1746552547",
                7, "PC");
        ChildCategory cc4 = new ChildCategory("GR", "Grooming", " Grooming for my pets. Funny",
                "https://cdn.petsathome.com/public/images/products/900_7143869.jpg",
                4, "PC");
        childCategories.add(cc3);
        childCategories.add(cc4);

        ChildCategory cc5= new ChildCategory("BE", "Bed", "Bed for pets. So marvelous",
                "https://www.vietpet.net/wp-content/uploads/2020/08/o-dem-cho-cho-meo-kh-pet-products-bolster-cat-dog-bed.jpg",
                3, "FU");
        ChildCategory cc6 = new ChildCategory("CR", "Crates, Houses & Pens", "Crates, Houses & Pens for my pets. Funny",
                "https://dogo.app/wp-content/uploads/2025/03/91DcUjbqWEL._AC_SL1500_.jpg",
                1, "FU");
        childCategories.add(cc5);
        childCategories.add(cc6);

        ChildCategory cc7= new ChildCategory("TY", "Toys", "Toys for pets. So marvelous",
                "https://i5.walmartimages.com/seo/Multipet-Smiling-Loofa-Plush-Dog-Toy-6-inch-Colorful-Plaid_6ca8c26c-4432-4478-98ff-6750ed30d291.0fad83d63ec96ec8263c8b3b800374e2.jpeg?odnHeight=320&odnWidth=320&odnBg=FFFFFF",
                6, "TO");
        ChildCategory cc8 = new ChildCategory("TN", "Training", " Trainning for my pets. Funny",
                "https://www.petland.ca/cdn/shop/files/company-of-animals-company-of-animals-pet-corrector-training-tool-29647804792934.png?v=1691084887",
                8, "TO");
        childCategories.add(cc7);
        childCategories.add(cc8);

        ChildCategory cc9= new ChildCategory("CL", "Collars & Leashes", "Bed for pets. So marvelous",
                "https://m.media-amazon.com/images/I/71RgH9AogIL._AC_SL1500_.jpg",
                12, "AC");
        ChildCategory cc10 = new ChildCategory("AC", "Apparels & Costume", "Crates, Houses & Pens for my pets. Funny",
                "https://images.halloweencostumes.eu/products/15022/1-1/holy-hound-pet-costume.jpg",
                11, "AC");
        ChildCategory cc11 = new ChildCategory("FE", "Feeders", "Feeders for my pets. Funny",
                "https://m.media-amazon.com/images/I/61gueG5uX1L.jpg",
                13, "AC");
        childCategories.add(cc9);
        childCategories.add(cc10);
        childCategories.add(cc11);

        ChildCategory cc12= new ChildCategory("CA", "Carriers", "Carriers for pets. So marvelous",
                "https://img.lazcdn.com/g/p/f0a6adea2b9541d3a4816e64033dbc8c.jpg_360x360q75.jpg",
                10, "CK");
        ChildCategory cc13 = new ChildCategory("KE", "Kennels", " Kennels for my pets. Funny",
                "https://www.petjoint.com.au/cdn/shop/products/wooden_dog_kennel_medium_a3f86c94-dcf1-4b55-b4ca-2f309ac8fb50.jpg?v=1527261808",
                9, "CK");
        childCategories.add(cc12);
        childCategories.add(cc13);



    }

}


