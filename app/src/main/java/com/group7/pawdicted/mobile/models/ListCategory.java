package com.group7.pawdicted.mobile.models;

import com.group7.pawdicted.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class ListCategory implements Serializable {

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
       Category c1= new Category("FT", "Food & Treats", "Nutrition and snacks for pets", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
               1, Arrays.asList("food", "snacks", "treats"));
       categories.add(c1);

       Category c2= new Category("PC", "Pet Care", "Health and hygiene care products", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg",
               2, Arrays.asList("health", "hygiene", "cleaning"));
       categories.add(c2);

       Category c3= new Category("FU", "Furniture", "Pet furniture for comfort", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 3, Arrays.asList("bed", "sofa", "rest"));
       categories.add(c3);

       Category c4= new Category("TO", "Toys", "Entertainment and toys for pets", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 4, Arrays.asList("toy", "play", "fun"));

       categories.add(c4);


       Category c5= new Category("AC", "Accessories", "Collars, leashes and other accessories", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 5, Arrays.asList("collar", "leash", "wear"));
       categories.add(c5);

       Category c6= new Category("CK", "Carriers & Kennels", "Travel and portable homes", "https://krill.vn/wp-content/uploads/2023/12/sua-tam-uot-krill-huong-nuoc-hoa-580ml-geotag-edit-1200x1200.jpg", 6, Arrays.asList("travel", "carrier", "kennel"));
       categories.add(c6);
    }
}
