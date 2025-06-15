package com.group7.pawdicted.mobile.models;

import java.io.Serializable;
import java.util.ArrayList;

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
        ChildCategory cc1= new ChildCategory("FO", "Food", "Food for pets. So marvelous", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                2, "FT");
        ChildCategory cc2 = new ChildCategory("TR", "Treats", " Treats for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                5, "FT");
        childCategories.add(cc1);
        childCategories.add(cc2);

        ChildCategory cc3= new ChildCategory("HE", "Health", "Good for your health. So good for jo pets. So marvelous", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                7, "PC");
        ChildCategory cc4 = new ChildCategory("GR", "Grooming", " Grooming for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                4, "PC");
        childCategories.add(cc3);
        childCategories.add(cc4);

        ChildCategory cc5= new ChildCategory("BE", "Bed", "Bed for pets. So marvelous", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                3, "FU");
        ChildCategory cc6 = new ChildCategory("CR", "Crates, Houses & Pens", "Crates, Houses & Pens for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                1, "FU");
        childCategories.add(cc5);
        childCategories.add(cc6);

        ChildCategory cc7= new ChildCategory("TY", "Toys", "Toys for pets. So marvelous", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                6, "TO");
        ChildCategory cc8 = new ChildCategory("TN", "Training", " Trainning for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                8, "TO");
        childCategories.add(cc7);
        childCategories.add(cc8);

        ChildCategory cc9= new ChildCategory("CL", "Collars & Leashes", "Bed for pets. So marvelous", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                12, "AC");
        ChildCategory cc10 = new ChildCategory("AC", "Apparels & Costume", "Crates, Houses & Pens for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                11, "AC");
        ChildCategory cc11 = new ChildCategory("FE", "Feeders", "Feeders for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                13, "AC");
        childCategories.add(cc9);
        childCategories.add(cc10);
        childCategories.add(cc11);

        ChildCategory cc12= new ChildCategory("CA", "Carriers", "Carriers for pets. So marvelous", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                10, "CK");
        ChildCategory cc13 = new ChildCategory("KE", "Kennels", " Kennels for my pets. Funny", "https://headsupfortails.com/cdn/shop/files/8906002482832_325256e8-336f-4804-a01c-8e4351d124b7.jpg?v=1748595262&width=990",
                9, "CK");
        childCategories.add(cc12);
        childCategories.add(cc13);




    }

}


