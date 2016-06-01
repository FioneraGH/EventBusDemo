package com.fionera.eventbusdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by fionera on 16-6-1.
 */

public class TestFragment
        extends Fragment
        implements View.OnClickListener {

    public static TestFragment newInstance(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", pos);
        TestFragment testFragment = new TestFragment();
        testFragment.setArguments(bundle);
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    private int position;
    private TextView textView;
    private ChangeReceiver changeReceiver;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
        }
        textView = (TextView) view.findViewById(R.id.tv_test);
        textView.setText(" " + position);
        textView.setOnClickListener(this);

        System.out.println("View created " + position);

        changeReceiver = new ChangeReceiver();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(changeReceiver, new IntentFilter("CHANGE_TEXT"));
    }

    @Override
    public void onDestroyView() {
        System.out.println("View destroyed " + position);
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(changeReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Fragment destroyed " + position);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("Fragment detached " + position);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), SecondActivity.class));
    }

    private class ChangeReceiver
            extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("reset", false)) {
                textView.setText("changed " + position + " " + 0);
                System.out.println("Receive Reset Msg");
            } else {
                textView.setText("changed " + position + " " + System.nanoTime());
                System.out.println("Receive Msg");
            }
        }
    }
}
