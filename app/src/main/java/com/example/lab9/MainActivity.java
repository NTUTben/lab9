package com.example.lab9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tv_clock;
    private Button btn_start;
    private Boolean flag = false;

    // 定義接收器來處理計時更新
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if (b != null) {
                // 更新顯示時間
                tv_clock.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        b.getInt("H"), b.getInt("M"), b.getInt("S")));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tv_clock = findViewById(R.id.tv_clock);
        btn_start = findViewById(R.id.btn_start);

        // 註冊接收器，接收時間更新
        registerReceiver(receiver, new IntentFilter("MyMessage"),RECEIVER_EXPORTED);

        // 設置按鈕的初始文本狀態
        btn_start.setText(flag ? "暫停" : "開始");

        // 設置按鈕的點擊事件
        btn_start.setOnClickListener(v -> {
            // 切換 flag 的值
            flag = !flag;

            if (flag) {
                btn_start.setText("暫停");
                Toast.makeText(this, "計時開始", Toast.LENGTH_SHORT).show();
            } else {
                btn_start.setText("開始");
                Toast.makeText(this, "計時暫停", Toast.LENGTH_SHORT).show();
            }

            // 啟動服務並傳遞 flag
            Intent serviceIntent = new Intent(this, MyService2.class);
            serviceIntent.putExtra("flag", flag);
            startService(serviceIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注銷接收器，防止內存泄漏
        unregisterReceiver(receiver);
    }
}
