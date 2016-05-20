package com.example.slidingframelayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * Created by bjhl on 16/5/20.
 */
public class DisplayUtils {
    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;
    private static float mScreenDensity = 0.0F;

    public DisplayUtils() {
    }

    public static int getScreenWidthPixels(Context context) {
        if(mScreenWidth == 0) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
        }

        return mScreenWidth;
    }

    public static int getScreenHeightPixels(Context context) {
        if(mScreenHeight == 0) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenHeight = dm.heightPixels;
        }

        return mScreenHeight;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static float getScreenDensity(Context context) {
        if(mScreenDensity == 0.0F) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenDensity = dm.density;
        }

        return mScreenDensity;
    }

    public static int getStatusBarHeight(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }
}
