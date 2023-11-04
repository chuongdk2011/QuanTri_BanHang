package com.example.quantri_banhang.actitvity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quantri_banhang.Adapter.ChatAdapter;
import com.example.quantri_banhang.DTO.ChatDTO;
import com.example.quantri_banhang.DTO.FcmMessage;
import com.example.quantri_banhang.Interface.FcmApiService;
import com.example.quantri_banhang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    ImageView img_back;
    CardView btn_send;
    EditText ed_chat;
    String senderRoom,reciverRoom;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    String reciverUid;
    ArrayList<ChatDTO> list;
    ChatAdapter adapter;
    String SenderUID = "admin";
    RecyclerView rcv_chat;
    TextView tv_nameShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        initView();

        reciverUid = getIntent().getStringExtra("iduser");
        Log.d("chuongdk", "onCreate_id nguoi gui: "+reciverUid);
        senderRoom = SenderUID+reciverUid;
        reciverRoom = reciverUid+SenderUID;
        tv_nameShop.setText(getIntent().getStringExtra("nameU"));

        list = new ArrayList<>();
        adapter = new ChatAdapter(this,list);
        getListChat();
        rcv_chat.setAdapter(adapter);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "onClick: "+ed_chat.getText().toString());
                String message = ed_chat.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                } else {
                    addMessage();
                    sendNotification(reciverUid, message);

                }

            }
        });

    }
    private void initView() {
        img_back = findViewById(R.id.img_backChat);
        btn_send = findViewById(R.id.sendbtnn);
        ed_chat = findViewById(R.id.ed_msg);
        rcv_chat = findViewById(R.id.rcv_chat);
        tv_nameShop = findViewById(R.id.tv_nameShop);
    }

    public void sendFcmData(String fcmToken, String content) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FcmApiService apiService = retrofit.create(FcmApiService.class);

        FcmMessage message = new FcmMessage();
        message.setTo(fcmToken);

        Map<String, String> data = new HashMap<>();
        data.put("title", "Bạn có tin nhắn mới");
        data.put("message", content);
        Log.d("Chat", "sendFcmData: "+content);
        message.setData(data);

        Call<ResponseBody> call = apiService.sendFcmMessage(message);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    Log.d("ChatActivity", "Gửi thông báo FCM thành công");
                } else {

                    Log.e("ChatActivity", "Gửi thông báo FCM thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ChatActivity", "Lỗi khi gửi thông báo FCM: " + t.getMessage());
            }
        });
    }

    private void sendNotification(String recipientUID, String message) {


        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("userTokens").child(recipientUID);
        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String recipientToken = dataSnapshot.getValue(String.class);
                if (recipientToken != null) {

                    sendFcmData(recipientToken, message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Lỗi khi lấy mã FCM của người nhận: " + databaseError.getMessage());
            }
        });
    }
    private void getListChat() {
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ChatDTO messages = dataSnapshot.getValue(ChatDTO.class);
                    list.add(messages);
//                    sendNotification(reciverUid, ed_chat.getText().toString());
                }
                Log.d("chuongdk", "onDataChange: "+list.size());
                adapter.notifyDataSetChanged();
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addMessage() {
        String message = ed_chat.getText().toString();
        if (message.isEmpty()){
            Toast.makeText(ChatActivity.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
            return;
        }
        ed_chat.setText("");
        Date date = new Date();

        database= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("chats")
                .child(senderRoom)
                .child("messages");
        String id = databaseReference.push().getKey();
        ChatDTO messagess = new ChatDTO(id,message,SenderUID,date.getTime());
        databaseReference.child(id).setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                database.getReference().child("chats")
                        .child(reciverRoom)
                        .child("messages")
                        .child(id).setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            }
        });
    }



}