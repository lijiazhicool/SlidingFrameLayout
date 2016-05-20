package com.example.slidingframelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private boolean open = true;
    private SlidingFrameLayout mSlidingFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingFrameLayout = (SlidingFrameLayout)findViewById(R.id.frame);

        MainFragment mainFragment = new MainFragment();
        getFragmentManager().beginTransaction().add(R.id.frame, mainFragment, "mainFragment").commit();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (open) {
                    open = false;
                    mSlidingFrameLayout.setShow(false);
                } else {
                    open = true;
                    mSlidingFrameLayout.setShow(true);
                }
            }
        });
    }
}
