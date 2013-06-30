package com.geekxx.PullHorizontalRefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created with IntelliJ IDEA.
 * Author: Gordon
 * Date: 13-6-29
 * Time: 下午3:58
 * Todo:
 */
public abstract class AbsRightView extends FrameLayout {

    public AbsRightView(Context context) {
        super(context);
    }

    protected void setContentView(int resID){
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resID, this, true);
    }

    /**
     * when the user is pull left, as soon as user finger leave the screen,
     * will fire refresh event
     */
    public abstract void onReleaseToRefresh();

    /**
     * when the user is pull left, but distance less than condition
     */
    public abstract void onPullLeft();

    /**
     * when the user is pulling left
     * @param scrollX PullHorizontalView scrollX
     */
    public abstract void onPullingLeft(int scrollX);

    public abstract void onRefreshing();

    /**
     * when the PHV had refreshed complete, in this method should implement restore state,
     * such as save the refresh date
     */
    public abstract void onRefreshComplete();


    /**
     * when the PHV had refresh stopped, caused by user initiative to interrupt the refreshing state
     * or cased by Exception
     * You should restore state. but need not to treat it to onRefreshComplete()
     */
    public abstract void onRefreshOver();

}
