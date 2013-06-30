package com.geekxx.PullHorizontalRefresh;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class MyActivity extends Activity implements PullHorizontalView.OnPullHorizontalListener, View.OnClickListener {

    private PullHorizontalView pullHorizontalView;

    private TextView tv_InnerText;

    private Button btnLabel;

    private int count = 1;

    static Random random = new Random();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnLabel = (Button) findViewById(R.id.act_main_TextView_State);
        btnLabel.setOnClickListener(this);

        pullHorizontalView = (PullHorizontalView) findViewById(R.id.act_main_PullHorizontalView);
        pullHorizontalView.setOnPullHorizontalListener(this);

        // todo: test a textview  wrap content width
        tv_InnerText = new TextView(this);
        tv_InnerText.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        tv_InnerText.setGravity(Gravity.CENTER);
        tv_InnerText.setText("000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        tv_InnerText.setTextColor(Color.BLACK);
        tv_InnerText.setBackgroundColor(Color.YELLOW);


        //  set a root view
        pullHorizontalView.setBehindRootView(tv_InnerText);

    }

    @Override
    public void onRefresh(PullHorizontalView view) {

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                // to imitate networking operation  , delay 2 second
                StringBuilder stringBuilder = new StringBuilder(tv_InnerText.getText().toString());
                if (random.nextBoolean()) {
                    for (int i = 0; i < 50; i++) {
                        stringBuilder.append(count);
                    }
                    //noinspection deprecation
                    btnLabel.setText("Refresh Success" + new Date().toLocaleString());
                    tv_InnerText.setText(stringBuilder);
                    // complete
                    pullHorizontalView.setRefreshComplete();
                    count++;
                } else {
                    GD.toast(MyActivity.this, "Refresh Fail");
                    //  like complete event, but never notify complete
                    pullHorizontalView.setRefreshOver();
                }

            }
        }, 2000);

    }

    @Override
    public void onClick(View v) {
         pullHorizontalView.setInitiativeRefresh();
    }
}
