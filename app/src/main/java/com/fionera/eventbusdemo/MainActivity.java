package com.fionera.eventbusdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity {

    @Subscribe
    public void onEvent(MessageEvent event) {
        System.out.println("Receive EventBus Msg");
        textView.setText(event.getParam());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initBind();

        initReceiver();

        App.eventBus.register(this);
    }

    private TextView textView;
    private ViewPager viewPager;

    private void initView() {
        textView = (TextView) findViewById(R.id.tv_title);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void initBind() {
        /**
         * FragmentPagerAdapter，Fragment会一直被维护，但View可能被销毁
         */
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            private String[] titles = {"zero", "one", "two", "three"};

            private List<TestFragment> testFragmentList = new ArrayList<>();

            {
                testFragmentList.add(TestFragment.newInstance(0));
                testFragmentList.add(TestFragment.newInstance(1));
                testFragmentList.add(TestFragment.newInstance(2));
                testFragmentList.add(TestFragment.newInstance(3));
            }

            @Override
            public Fragment getItem(int position) {
                System.out.println("Get Fragment " + position);
                /**
                 * 新实例和可维护状态集的差别，FragmentManger在ViewPager中的管理:
                 * 当destroyItem方法没有被置空的时候，两者没有明显差距，getItem方法都只调用了一次，
                 * 仅发生在该页面从来没被创建过，所有的Fragment在超过了ViewPager的offset值之后都被销毁和重建
                 * 而如果置空了destroyItem方法，也不会对这一点造成什么影响，getItem在调用之后之所以不会再调用，
                 * 是因为ViewPager里的FragmentManager实际上通过tag维护了所有的Fragment，除非该Fragment是null，
                 * 否则永远不会再回调该方法，这也很好的验证了前面的状况。但是有一点要注意，
                 * 维护了Fragment不代表它对应的View不会被销毁重建，这些可以通过Fragment的生命周期验证，
                 * 因此有一点一定要明确Fragment ！= View
                 */
                //                return TestFragment.newInstance(position);
                return testFragmentList.get(position);
            }

            /**
             * 如果不设置destroyItem为空，那么FragmentManager就会将超过offset的Fragment杀死
             * 但是这里仍然有一个问题，Fragment虽然没有被杀死，从状态维护上来看，虽然子控件属性发生了变化，
             * 但是他的宽高却没有发生变化，个人理解是一种状态丢失（WW），两种类型的本地广播应证了这件事情。
             * 总的来说置空destroyItem回调是不正确的，这个通过源码能看得出来。
             */
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
                System.out.println("Destroy Fragment " + position);
            }

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        /**
         * 如果不设置，每次创建Fragment会报expect state 3 found 2，超过offset的Fragment对应的View被销毁，
         * 因此在destroyView解注册的广播会失效无法接收消息。
         * 而如果因为offset不够，也就是说后来的Fragment没有创建，这样可能Fragment不会按你预期的那样，
         * 对广播做出相应的反应，原因很简单，他还没有被创建。
         */
        //        viewPager.setOffscreenPageLimit(3);
    }

    private ChangeAdapterReceiver changeAdapterReceiver;

    private void initReceiver() {
        changeAdapterReceiver = new ChangeAdapterReceiver();
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver(changeAdapterReceiver, new IntentFilter("CHANGE_ADAPTER"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainActivity.this)
                .unregisterReceiver(changeAdapterReceiver);
        App.eventBus.unregister(this);
    }

    private class ChangeAdapterReceiver
            extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Receive Change Adapter Msg");
            /**
             * FragmentStatePagerAdapter，View被销毁伴随着Fragment整个被销毁(走完整个生命周期)
             */
            viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                private String[] titles = {"zero", "one", "two", "three", "four"};

                @Override
                public Fragment getItem(int position) {
                    System.out.println("Get Fragment State " + position);
                    return TestFragment.newInstance(position);
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    super.destroyItem(container, position, object);
                    System.out.println("Destroy Fragment State" + position);
                }

                @Override
                public int getCount() {
                    return titles.length;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return titles[position];
                }
            });
        }
    }
}
