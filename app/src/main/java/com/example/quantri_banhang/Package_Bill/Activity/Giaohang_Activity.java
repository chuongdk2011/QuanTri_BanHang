package com.example.quantri_banhang.Package_Bill.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quantri_banhang.DTO.BillDTO;
import com.example.quantri_banhang.Package_Bill.Adapter.Bill_Adapter;
import com.example.quantri_banhang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Giaohang_Activity extends AppCompatActivity {
    String TAG = "Giaohang_Activity";
    ArrayList<BillDTO> list;
    Bill_Adapter adapter;
    ListView rc_listcart;
    ImageView id_back;
    public void Anhxa(){
        rc_listcart = findViewById(R.id.list_donhang);
        id_back = findViewById(R.id.id_back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giaohang);
        Anhxa();
        list = new ArrayList<>();
        adapter = new Bill_Adapter(Giaohang_Activity.this, list);
        rc_listcart.setAdapter(adapter);
        getdata();
        id_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getdata(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefId = database.getReference("BillProduct");
        myRefId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BillDTO billDTO = dataSnapshot.getValue(BillDTO.class);
                    if (billDTO.getStatus() == 3){
                        list.add(billDTO);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "err: " + error);
            }
        });
    }
}