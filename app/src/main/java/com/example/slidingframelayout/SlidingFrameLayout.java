package com.example.slidingframelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by ljz on 16/5/11.
 */
public class SlidingFrameLayout extends FrameLayout {

    //状态
    public static enum State {
        OPened, Moving, Closed
    }

    private boolean mIsBeingDragged = false;
    private Scroller mScroller;
    private int mTouchSlop;
    private int mLastMotionX;
    private int mLastMotionY;

    // 屏幕宽度
    private int mScreenwidth = 0;
    private State mCurrentState = State.OPened;

    private StateChangedListner mStateChangedListener;

    //右侧边缘可见部分，用于可点击拉出功能：使用时可把这部分背景透明即可
    private int mVisibleDp = 3;


    public void setOnStateChangedListener(StateChangedListner listener) {
        mStateChangedListener = listener;
    }

    public SlidingFrameLayout(Context context) {
        super(context);
    }

    public SlidingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenwidth = wm.getDefaultDisplay().getWidth();
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // need this since otherwise this View jumps back to its original position
        // ignoring its displacement
        // when (re-)doing layout, e.g. when a fragment transaction is committed
        // TODO: 16/5/20   关键代码－－－此处不能调用layout会导致循环调用OOM
        if (changed && mCurrentState == State.Closed){
            offsetLeftAndRight(mScreenwidth -DisplayUtils.dip2px(getContext(), mVisibleDp)-left);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = (int) x;
                mLastMotionY = (int) y;
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float yDiff = Math.abs(y - mLastMotionY);
                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    mIsBeingDragged = true;
                }
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 计算移动的距离
                int offX = x - (int) mLastMotionX;
                int offY = y - (int) mLastMotionY;
                if (Math.abs(offX) > Math.abs(offY)) {
                    if (getLeft() + offX < mScreenwidth - getWidth()) {
                        layout(mScreenwidth - getWidth(), getTop(), mScreenwidth, getBottom());
                        mCurrentState = State.OPened;
                    } else if (getRight() + offX > mScreenwidth + getWidth()) {
                        layout(mScreenwidth, getTop(), mScreenwidth + getWidth(), getBottom());
                        mCurrentState = State.Closed;
                    } else {
                        mCurrentState = State.Moving;
                        offY = 0;
                        // 1. 调用layout方法来重新放置它的位置
                        layout(getLeft() + offX, getTop() + offY, getRight() + offX, getBottom() + offY);
                        // 2. 调用layout方法来重新放置它的位置
                        // offsetLeftAndRight(offX);
                        // offsetTopAndBottom(offY);
                        // 3. 调用layout方法来重新放置它的位置
                        // ViewGroup.MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
                        // mlp.leftMargin = getLeft() + offX;
                        // mlp.topMargin = getTop() + offY;
                        // setLayoutParams(mlp);
                        // 4.scrollTo和scrollBy
                        // ((View) getParent()).scrollBy(-offX,- offY);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 平滑过度
                if (mScreenwidth - getLeft() < getWidth() / 2) {
                    smoothScrollTo(getLeft(), mScreenwidth - DisplayUtils.dip2px(getContext(), mVisibleDp));
                    mCurrentState = State.Closed;
                    if (null != mStateChangedListener) {
                        mStateChangedListener.onStateChanged(State.Closed);
                    }
                } else {
                    smoothScrollTo(getLeft(), mScreenwidth - getWidth());
                    mCurrentState = State.OPened;
                    if (null != mStateChangedListener) {
                        mStateChangedListener.onStateChanged(State.OPened);
                    }
                }
                break;
        }
        return true;
    }

    private void smoothScrollTo(int srcX, int desX) {
        mScroller.startScroll(srcX, 0, desX - srcX, 0, Math.abs(desX - srcX) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            layout(mScroller.getCurrX(), getTop(), mScroller.getCurrX() + getWidth(), getBottom());
            postInvalidate();
        }
    }

    public boolean isShow(){
        return mCurrentState == State.OPened;
    }

    public void setShow(boolean isShow) {
        if (isShow && mCurrentState == State.OPened) {
            return;
        }
        if (!isShow && mCurrentState == State.Closed) {
            return;
        }
        if (isShow) {
            mCurrentState = State.OPened;
            smoothScrollTo(getLeft(), mScreenwidth - getWidth());
            if (mStateChangedListener != null) {
                mStateChangedListener.onStateChanged(State.OPened);
            }
        } else {
            mCurrentState = State.Closed;
            smoothScrollTo(getLeft(), mScreenwidth - DisplayUtils.dip2px(getContext(), mVisibleDp));
            if (mStateChangedListener != null) {
                mStateChangedListener.onStateChanged(State.Closed);}
        }
    }

    public interface StateChangedListner {
        void onStateChanged(State state);
    }

}
