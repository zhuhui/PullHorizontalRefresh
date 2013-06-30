package com.geekxx.PullHorizontalRefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.*;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created with IntelliJ IDEA.
 * Author: Gordon
 * Date: 13-6-29
 * Time: 上午10:34
 * Wrapped exact HorizontalScrollView
 */
public class HorizontalView extends HorizontalScrollView {
    public HorizontalView(Context context) {
        super(context);
        initView(context);
    }

    public HorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HorizontalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * this widget width
     */
    private int selfWidth;

    /**
     * inner child's width
     */
    private int internalWidth;


    /**
     * the scrollX max value,  max = internalWidth - selfWidth
     */
    private int maxScrollX;


    /**
     * Directly one unique child, normally is a ViewGroup
     */
    private View wrappedLayout;

    private void initView(Context context) {
        GD.i("HorizontalView initial");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);


        // init wrapped layout
        wrappedLayout = new LinearLayout(context);
        ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,  //  important !
                ViewGroup.LayoutParams.MATCH_PARENT);
        wrappedLayout.setLayoutParams(lp2);


        addView(wrappedLayout);

        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //noinspection deprecation
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                selfWidth = getWidth();
                GD.i("控件宽度" + selfWidth);
                reMeasure();
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        // disallow scroll over, that more beautiful
        if (Build.VERSION.SDK_INT>8){
            setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }


    /**
     *  measure the wrapped layout width again
     */
    private void reMeasure() {
        measureView(wrappedLayout);
        internalWidth = wrappedLayout.getMeasuredWidth();
        maxScrollX = internalWidth - selfWidth;
        GD.i("current wrapped Layout width" + internalWidth);
        GD.i("max-scrollX" + maxScrollX);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            //  if you wanna measure height , you wan tune it
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        int childHeightSpec = ViewGroup.getChildMeasureSpec(0, 0, p.height);
        int lpWidth = p.width;
        int childWidthSpec;
        if (lpWidth > 0) {
            childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true; // eat it
    }

    /**
     * return the current state ready to pull
     */
    protected boolean isReadyToPull() {
        return getScrollX() >= maxScrollX;   // important =
    }

    /**
     * get the wrapped directly child view
     */
    public View getDirectlyView() {
        return wrappedLayout;
    }

    public void setWrappedLayout(View viewGroup){
        removeAllViews();
        wrappedLayout = viewGroup;
        addView(wrappedLayout);
        reMeasure();
    }

    public void requestMeasure(){
         reMeasure();
    }
}
