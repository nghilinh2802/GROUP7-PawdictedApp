package com.group7.pawdicted.mobile.connectors;

import com.group7.pawdicted.mobile.models.ListProduct;
import com.group7.pawdicted.mobile.models.Product;

import java.util.ArrayList;

public class ProductConnector {
    ListProduct listProduct;
    public ProductConnector()
    {
        listProduct= new ListProduct();
        listProduct.generate_sample_dataset();
    }
    public ArrayList<Product> get_all_products()
    {
        if (listProduct==null)
        {
            listProduct= new ListProduct();
            listProduct.generate_sample_dataset();
        }
        return listProduct.getProducts();
    }
    public ArrayList<Product> get_products_by_category(String category_id)
    {
        if (listProduct==null) {
            listProduct = new ListProduct();
            listProduct.generate_sample_dataset();
        }
        ArrayList<Product> results=new ArrayList<>();
        for (Product p: listProduct.getProducts())
        {
            if (p.getCategory_id()==category_id)
            {
                results.add(p);
            }
        }
        return results;
    }
}
