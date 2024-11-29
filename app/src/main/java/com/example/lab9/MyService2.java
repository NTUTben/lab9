package com.example.lab9;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService2 extends Service {

    private boolean flag = false;  // 使用局部 flag
    private int h = 0, m = 0, s = 0;
    private Handler handler = new Handler();  // 使用 Handler 來處理計時邏輯
    private Runnable timerRunnable;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 設置 flag 來控制計時器
        flag = intent.getBooleanExtra("flag", false);

        // 如果 flag 為 true，開始計時
        if (flag) {
            startTimer();
        } else {
            stopTimer();
        }

        return START_STICKY;
    }

    private void startTimer() {
        Log.d("MyService", "Timer started");

        // 設置計時邏輯
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // 每秒更新一次
                s++;
                if (s >= 60) {
                    s = 0;
                    m++;
                    if (m >= 60) {
                        m = 0;
                        h++;
                    }
                }

                // 發送更新的計時結果到 MainActivity
                Intent broadcastIntent = new Intent("MyMessage");
                Bundle bundle = new Bundle();
                bundle.putInt("H", h);
                bundle.putInt("M", m);
                bundle.putInt("S", s);
                broadcastIntent.putExtras(bundle);
                sendBroadcast(broadcastIntent);  // 發送廣播

                // 每1秒調用一次這個Runnable
                if (flag) {
                    handler.postDelayed(this, 1000);  // 延遲1秒後再次執行
                }
            }
        };

        // 開始計時
        handler.post(timerRunnable);
    }

    private void stopTimer() {
        Log.d("MyService", "Timer stopped");

        // 停止計時
        flag = false;
        handler.removeCallbacks(timerRunnable);  // 移除計時的回調
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止計時器和清理
        stopTimer();
        Log.d("MyService", "Service destroyed");
    }
}
