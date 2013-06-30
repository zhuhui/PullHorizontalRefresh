package com.geekxx.PullHorizontalRefresh;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * Author: Gordon
 * Date: 13-6-29
 * Time: 下午4:21
 * Demo of AbsRightView
 */
public class DemoRightView extends AbsRightView {

    public DemoRightView(Context context) {
        super(context);
        initView(context);
    }

    private TextView tv_Label;

    private ProgressBar progressBar;

    private ImageView imgv_Arrow;

    private  Animation rotateAnimation, resetRotateAnimation;

    private void initView(Context context){
        setContentView(R.layout.view_demo_rightview);
        tv_Label = (TextView) findViewById(R.id.view_demo_rightview_TextView_Label);
        progressBar = (ProgressBar) findViewById(R.id.view_demo_rightview_ProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imgv_Arrow = (ImageView) findViewById(R.id.view_demo_rightview_arrow);


        //  init animation
        final Interpolator interpolator = new LinearInterpolator();
        rotateAnimation = new RotateAnimation(90, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setInterpolator(interpolator);
        rotateAnimation.setDuration(150);
        rotateAnimation.setFillAfter(true);

        resetRotateAnimation = new RotateAnimation(-90, 180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        resetRotateAnimation.setInterpolator(interpolator);
        resetRotateAnimation.setDuration(150);
        resetRotateAnimation.setFillAfter(true);
        imgv_Arrow.startAnimation(resetRotateAnimation);
    }


    @Override
    public void onReleaseToRefresh() {
        tv_Label.setText("Release\nto\nRefresh");
        imgv_Arrow.clearAnimation();
        imgv_Arrow.startAnimation(rotateAnimation);
    }

    @Override
    public void onPullLeft() {
        tv_Label.setText("pull\nto\nRefresh");
        imgv_Arrow.clearAnimation();
        imgv_Arrow.startAnimation(resetRotateAnimation);

    }

    @Override
    public void onPullingLeft(int scrollX) {

    }

    @Override
    public void onRefreshing() {
        progressBar.setVisibility(VISIBLE);
        imgv_Arrow.clearAnimation();
        imgv_Arrow.setVisibility(View.GONE);
        tv_Label.setText("Refreshing");
    }

    @Override
    public void onRefreshComplete() {
        tv_Label.setText("Release\nto\nRefresh");
        progressBar.setVisibility(GONE);
        imgv_Arrow.setVisibility(VISIBLE);
        imgv_Arrow.clearAnimation();
    }

    @Override
    public void onRefreshOver() {
        tv_Label.setText("Release\nto\nRefresh");
        progressBar.setVisibility(GONE);
        imgv_Arrow.setVisibility(VISIBLE);
        imgv_Arrow.clearAnimation();
    }
}
