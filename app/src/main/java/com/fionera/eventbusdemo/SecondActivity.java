package com.fionera.eventbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by fionera on 16-6-1.
 */

public class SecondActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.btn_send_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(SecondActivity.this)
                        .sendBroadcast(new Intent("CHANGE_TEXT").putExtra("reset", false));
                Snackbar.make(getWindow().getDecorView(), "已发送", Snackbar.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_send_local_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(SecondActivity.this)
                        .sendBroadcast(new Intent("CHANGE_TEXT").putExtra("reset", true));
                Snackbar.make(getWindow().getDecorView(), "已发送Reset", Snackbar.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_send_change_adapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(SecondActivity.this)
                        .sendBroadcast(new Intent("CHANGE_ADAPTER"));
                Snackbar.make(getWindow().getDecorView(), "已发送Adapter", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        findViewById(R.id.btn_send_bus_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.eventBus.post(new MessageEvent().setParam("这是消息"));
                Snackbar.make(getWindow().getDecorView(), "已发送Event", Snackbar.LENGTH_INDEFINITE)
                        .setAction("关闭", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });
    }

}
