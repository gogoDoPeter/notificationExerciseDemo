package com.example.notificationtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private static boolean isCheckPermission = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "recommend";
            channelName = "推荐消息";
            importance = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(channelId, channelName, importance);
        }

        if(isCheckPermission){
            checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION);
        }
        // 检查是否获得了权限（Android6.0运行时权限）
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // 没有获得授权，申请授权
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
//                // 返回值：
//                //如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                //如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                //如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
//                // 弹窗需要解释为何需要该权限，再次请求授权
//                Toast.makeText(this, "请授权！", Toast.LENGTH_LONG).show();
//                // 帮跳转到该应用的设置界面，让用户手动授权
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", "com.bw.movie", null);
//                intent.setData(uri);
//                startActivity(intent);
//            } else {
//                // 不需要解释为何需要该权限，直接请求授权
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
//            }
//        } else {
//            // 已经获得授权，可以打电话
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            Uri data = Uri.parse("tel:" + 10010);
//            intent.setData(data);
//            startActivity(intent);
//        }
    }

    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL_PERMISSION){
            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                Toast.makeText(this,"Grant fail",Toast.LENGTH_SHORT).show();
            } else {//成功
                Toast.makeText(this,"Grant success",Toast.LENGTH_SHORT).show();
                isCheckPermission = false;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void sendChatMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("chat");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                //跳转到具体的channel id设置页面
//                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
//                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
//                startActivity(intent);

                //跳转到该应用的setting页面
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);


                Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
            }
        }

        Intent intent = new Intent(this, TestActivity.class);

        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_CALL);
        intent2.setData(Uri.parse("tel:"+10086));

//        拨打电话（跳转到拨号界面，用户手动点击拨打）
        Intent intent3 = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + 119);
        intent3.setData(data);
//        startActivity(intent3);

        //需要添加打电话的权限 Task
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "chat")
                .setContentTitle("收到一条聊天消息")
                .setContentText("今天中午吃什么？")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);
    }

    public void sendSubscribeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "subscribe")
                .setContentTitle("收到一条订阅消息")
                .setContentText("地铁沿线30万商铺抢购中！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .setNumber(2)
                .build();
        manager.notify(2, notification);
    }

    public void sendRecommendMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "recommend")
                .setContentTitle("早餐推荐")
                .setContentText("生煎包+卤蛋")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .build();
        manager.notify(3, notification);
    }

    public void closeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //取消通知
        manager.cancel(3);
    }
}
