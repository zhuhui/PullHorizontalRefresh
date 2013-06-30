package com.geekxx.PullHorizontalRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * Author: Gordon
 * Date: 13-6-29
 * Time: 上午10:32
 * this is a LinearLayout
 */
public class PullHorizontalView extends LinearLayout {

    public PullHorizontalView(Context context) {
        super(context);
        initView(context);
    }

    public PullHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullHorizontalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }


    /**
     * A view in parent right, like pull to refresh header, a stub
     */
    private AbsRightView rightView;

    /**
     * the rightView's real width
     */
    private int rightViewWidth;


    /**
     * Real wrapped view
     */
    private HorizontalView horizontalView;

    /**
     * distance
     */
    private static int TOUCH_SLOP;

    /**
     * implement the smooth scroll back
     */
    private Scroller scroller;

    /**
     * disallow Scroll event when the PHV is refreshing;
     */
    private boolean disallowScrollInRefreshing = true;

    public static final int STATE_RELEASE_TO_REFRESH = 0x100001;
    public static final int STATE_PULL_TO_REFRESH = 0x100002;
    public static final int STATE_REFRESHING = 0x100003;

    private OnPullHorizontalListener pullHorizontalListener;

    /**
     * current state , default PULL_TO_REFRESH
     */
    private int currState = STATE_PULL_TO_REFRESH;

    private void initView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        scroller = new Scroller(context);
        // init inner HorizontalScrollView
        horizontalView = new HorizontalView(context);
        LayoutParams lp = new LayoutParams(0, -1);
        lp.weight = 1;
        addView(horizontalView, lp);
        rightView = new DemoRightView(context);
        measureView(rightView);
        rightViewWidth = rightView.getMeasuredWidth();  // measure width
        GD.i("rightView width: " + rightViewWidth);
        addView(rightView, new LayoutParams(-2, -2));
        setPadding(0, 0, -rightViewWidth, 0);
        //noinspection deprecation
        TOUCH_SLOP = ViewConfiguration.getTouchSlop(); //  8px
    }


    //  in order to get measuredWidth
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
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


    private void pullEvent(MotionEvent ev) {
        final float thisX = ev.getRawX();
        int scrollPosition = (int) (startX - thisX) / 2; // /2 is the friction

        //  compute whether should to change state
        if (scrollPosition > rightViewWidth) {
            //  ready to refresh
            currState = STATE_RELEASE_TO_REFRESH;
            rightView.onReleaseToRefresh();
        } else {
            // nothing
            currState = STATE_PULL_TO_REFRESH;
            rightView.onPullLeft();
        }

        if (scrollPosition < 0) scrollPosition = 0;  // disallow scroll in right bound
        scrollTo(scrollPosition, 0);
    }

    // let the rightView back to hidden
    private void smoothScrollBack() {
        final int currScrollX = getScrollX();
        scroller.startScroll(currScrollX, 0, -currScrollX, 0, 500);
        invalidate();
    }

    // let the rightView scroll to refresh location
    private void smoothToRefresh() {
        final int currScrollX = getScrollX();
        scroller.startScroll(currScrollX, 0, rightViewWidth - currScrollX, 0, 500);
        invalidate();
    }


    /**
     * set UI refreshing state
     */
    private void setRefreshing() {
        currState = STATE_REFRESHING;
        rightView.onRefreshing();
        smoothToRefresh();
        if (null != pullHorizontalListener) {
            pullHorizontalListener.onRefresh(this);
        }
    }

    // dx is distanceX
    float startX, finalX, dx;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        if (currState == STATE_REFRESHING && disallowScrollInRefreshing) {
            return false;
        }
        return true;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float thisX = ev.getRawX();
        if (horizontalView.isReadyToPull()) {
            // fix width changed bug
            startX = thisX;
            // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
            switch (ev.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startX = thisX;
//                    break;
                case MotionEvent.ACTION_MOVE:
                    dx += Math.abs(thisX - finalX);
                    if (dx > TOUCH_SLOP) {
                        finalX = thisX;
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    dx = 0;
                    break;
            }
        }
        finalX = thisX;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        final float thisX = ev.getRawX();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = thisX;
                break;
            case MotionEvent.ACTION_MOVE:
                pullEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                //  **to compute whether fire refreshing
                finalX = 0;
                dx = 0;
                if (currState == STATE_RELEASE_TO_REFRESH) {
                    setRefreshing();
                    return true;
                } else {
                    //   when touch up restore state
                    smoothScrollBack();
                    return true;
                }
        }
        GD.w("come out");
        finalX = thisX;
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    public void setOnPullHorizontalListener(OnPullHorizontalListener listener) {
        this.pullHorizontalListener = listener;
    }


    /**
     * It means success to refresh, notify complete event to RightView.
     */
    public void setRefreshComplete() {
        smoothScrollBack();
        // when the data is refreshed , width would be measure  again
        horizontalView.requestMeasure();
        rightView.onRefreshComplete();
        currState = STATE_PULL_TO_REFRESH;

    }

    /**
     * It may be failed to refresh, maybe caused by Network, IO factor
     * or user force close
     */
    public void setRefreshOver() {
        smoothScrollBack();
        rightView.onRefreshOver();
        currState = STATE_PULL_TO_REFRESH;
    }


    public void setInitiativeRefresh(){
        scrollTo(rightViewWidth, 0);
        currState = STATE_REFRESHING;
        rightView.onRefreshing();
        if (null!= pullHorizontalListener){
            pullHorizontalListener.onRefresh(this);
        }
    }

    /**
     * get the wrapped HorizontalView
     */
    public HorizontalView getHorizontalView() {
        return horizontalView;
    }


    /**
     * @deprecated Recommend disallow it
     */
    public void disallowRefreshingScroll(boolean disallow) {
        this.disallowScrollInRefreshing = disallow;
    }

    /**
     * set the HorizontalScrollView's directly child. its only has one child
     * @param viewRoot which you wanna set
     */
    public void setBehindRootView(View viewRoot) {
        horizontalView.setWrappedLayout(viewRoot);
    }

    /*
     *  CLASS
     */

    public static interface OnPullHorizontalListener {

        /**
         * call onRefresh
         *
         * @param view self
         */
        public void onRefresh(PullHorizontalView view);
    }
}
