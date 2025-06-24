package com.group7.pawdicted.mobile.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListCustomer {
    private ArrayList<Customer> customers;

    public ListCustomer() {
        customers = new ArrayList<>();
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    public void generate_sample_dataset() {
        customers.add(new Customer("C001", "Alice Nguyen", "alice@gmail.com", "aliceng", "0912345678", "123 Hanoi", "Female", getDate("1992-06-12"), getDate("2024-05-10"), "avatar1.jpg", "user"));
        customers.add(new Customer("C002", "Bob Tran", "bobtran@gmail.com", "bobby", "0987654321", "456 HCMC", "Male", getDate("1990-03-25"), getDate("2024-05-12"), "avatar2.jpg", "user"));
        customers.add(new Customer("C003", "Cathy Le", "cathy@gmail.com", "cathycat", "0933123123", "789 Da Nang", "Female", getDate("1995-11-03"), getDate("2024-05-15"), "avatar3.jpg", "user"));
        customers.add(new Customer("C004", "Daniel Pham", "daniel@gmail.com", "danp", "0944112233", "321 Can Tho", "Male", getDate("1988-09-15"), getDate("2024-06-01"), "avatar4.jpg", "admin"));
        customers.add(new Customer("C005", "Emma Hoang", "emma@gmail.com", "emmah", "0909123456", "654 Hue", "Female", getDate("1996-02-20"), getDate("2024-06-10"), "avatar5.jpg", "user"));
    }

    private Date getDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            System.err.println("Invalid date format: " + dateStr);
            return new Date(); // hoặc null tùy ý
        }
    }
}
