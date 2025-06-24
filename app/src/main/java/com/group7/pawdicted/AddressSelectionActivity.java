package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group7.pawdicted.mobile.models.AddressItem;
import com.group7.pawdicted.mobile.adapters.AddressAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<AddressItem> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address_selection);

        // Áp dụng WindowInsets cho root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.addressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressList = new ArrayList<>();
        addressList.add(new AddressItem("Lê Nguyễn Hà Châu", "(+84) 967 663 867", "2 Đồng Khởi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh", true));
        addressList.add(new AddressItem("Lê Đoàn Nghi Linh", "(+84) 905 356 289", "300 Phạm Văn Bạch, Phường 15, Quận Tân Bình, TP. Hồ Chí Minh", false));
        addressList.add(new AddressItem("Tạ Tuyết Em", "(+84) 904 624 203", "669 Đỗ Mười, Linh Xuân, Thủ Đức, TP. Hồ Chí Minh", false));

        addressAdapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(addressAdapter);
    }

    public void open_new_address_activity(View view) {
        Intent intent = new Intent(this, NewAddressActivity.class);
        startActivity(intent);
    }
}

//package com.group7.pawdicted;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.group7.pawdicted.mobile.models.AddressItem;
//import com.group7.pawdicted.mobile.adapters.AddressAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AddressSelectionActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private AddressAdapter addressAdapter;
//    private List<AddressItem> addressList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_address_selection);  // Đảm bảo sử dụng đúng layout của AddressSelectionActivity
//
//        recyclerView = findViewById(R.id.addressRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        addressList = new ArrayList<>();
//        addressList.add(new AddressItem("Lê Nguyễn Hà Châu", "(+84) 967 663 867", "2 Đồng Khởi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh", true));
//        addressList.add(new AddressItem("Lê Đoàn Nghi Linh", "(+84) 905 356 289", "300 Phạm Văn Bạch, Phường 15, Quận Tân Bình, TP. Hồ Chí Minh", false));
//        addressList.add(new AddressItem("Tạ Tuyết Em", "(+84) 904 624 203", "669 Đỗ Mười, Linh Xuân, Thủ Đức, TP. Hồ Chí Minh", false));
//
//        addressAdapter = new AddressAdapter(addressList);
//        recyclerView.setAdapter(addressAdapter);
//
//        Button addNewAddressButton = findViewById(R.id.addNewAddressButton);
//        addNewAddressButton.setOnClickListener(v -> {
//            // Handle adding a new address
//        });
//    }
//
//    public void open_new_address_activity(View view) {
//        Intent intent = new Intent(this, NewAddressActivity.class);
//        startActivity(intent);
//    }
//}
