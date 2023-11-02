package com.example.quantri_banhang.Notify;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.quantri_banhang.R;
import com.example.quantri_banhang.actitvity.ChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "your_channel_id";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Nhận thông báo từ FCM: " + remoteMessage.getData());

        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Thông báo từ FCM: " + remoteMessage.getData());
            showNotification(remoteMessage.getData());
        }
    }

    private void showNotification(Map<String, String> data) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("message"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
    }
}
