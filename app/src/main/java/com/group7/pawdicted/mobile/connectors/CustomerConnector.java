package com.group7.pawdicted.mobile.connectors;

import com.group7.pawdicted.mobile.models.Customer;
import com.group7.pawdicted.mobile.models.ListCustomer;

import java.util.ArrayList;

public class CustomerConnector {
    ListCustomer listCustomer;
    public CustomerConnector()
    {
        listCustomer= new ListCustomer();
        listCustomer.generate_sample_dataset();
    }
    public ArrayList<Customer> get_all_customers()
    {
        if (listCustomer==null)
        {
            listCustomer=new ListCustomer();
            listCustomer.generate_sample_dataset();
        }
        return listCustomer.getCustomers();
    }

    public void addCustomer (Customer c)
    {
        listCustomer.addCustomer(c);
    }
}
