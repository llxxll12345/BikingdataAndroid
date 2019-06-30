package com.example.fragment;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.example.R;
import com.example.utility.DensityUtil;


public class SlideMenu extends RelativeLayout{
    public static enum Positon {
        LEFT, RIGHT
    }
    private Context mContext;
    private Activity mActivity;
    private Scroller mScroller = null;
    private View mMenuView;
    private View mMaskView;
    private int mMenuWidth = 0;
    private int mScreenWidth = 0;
    private boolean mIsMoving = false;
    private boolean mShow = false;
    private int mDuration = 600;
    private Positon mPositon = Positon.LEFT;

    public SlideMenu(Context context) {
        this(context, null);
    }
    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    public static SlideMenu create(Activity activity) {
        SlideMenu view = new SlideMenu(activity);
        return view;
    }


    public static SlideMenu create(Activity activity, Positon positon) {
        SlideMenu view = new SlideMenu(activity);
        view.mPositon = positon;
        return view;
    }


    private void init(Context context) {
        // TODO Auto-generated method stub
        mContext = context;
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
        mScroller = new Scroller(context);
        mScreenWidth = DensityUtil.getScreenWidthAndHeight(context)[0];
        mMenuWidth = mScreenWidth * 7 / 9;

        attachToContentView((Activity) context, mPositon);
    }


    private void attachToContentView(Activity activity, Positon positon) {
        mPositon = positon;
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup contentView = ((ViewGroup) contentFrameLayout.getChildAt(0));
        mMaskView = new View(activity);
        mMaskView.setBackgroundColor(mContext.getColor(R.color.colorLightGray));
        contentView.addView(mMaskView, contentView.getLayoutParams());
        mMaskView.setVisibility(View.GONE);
        mMaskView.setClickable(true);
        mMaskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow()) {
                    dismiss();
                }
            }
        });
    }


    public void setMenuWidth(int width) {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = width;
        mMenuWidth = width;
        this.setLayoutParams(params);
    }


    public void setMenuView(Activity activity, View view) {
        mActivity = activity;
        mMenuView = view;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mMenuView, params);
        mMenuView.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mMenuWidth = mMenuView.getWidth();
                switch (mPositon) {
                    case LEFT:
                        SlideMenu.this.scrollTo(mScreenWidth, 0);
                        break;
                    case RIGHT:
                        SlideMenu.this.scrollTo(-mScreenWidth, 0);
                        break;
                }

            }
        });
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup contentView = contentFrameLayout;
        contentView.addView(this);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        switch (mPositon) {
            case LEFT:
                layoutParams.gravity = Gravity.LEFT;
                layoutParams.leftMargin = 0;
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.rightMargin = 0;
                break;
        }
        TextView titleFrameLayout = (TextView) activity.findViewById(android.R.id.title);
        if( titleFrameLayout != null){
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(mContext);
        }
        int flags =  mActivity.getWindow().getAttributes().flags;
        int flag = (flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(flag == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS){
            //说明状态栏使用沉浸式
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(mContext);
        }
        this.setLayoutParams(layoutParams);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isShow()){
                    return true;
                }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    public void show(){
        if(isShow() && !mIsMoving)
            return;
        switch (mPositon) {
            case LEFT:
                startScroll(mMenuWidth, -mMenuWidth, mDuration);
                break;
            case RIGHT:
                startScroll(-mMenuWidth, mMenuWidth, mDuration);
                break;
        }
        switchMaskView(true);
        mShow = true;
    }


    private void switchMaskView(boolean bShow){
        if(bShow) {
            mMaskView.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(mDuration);
            mMaskView.startAnimation(animation);
        } else{
            mMaskView.setVisibility(View.GONE);
        }
    }


    public void dismiss() {
        // TODO Auto-generated method stub
        if(!isShow() && !mIsMoving)
            return;
        switch (mPositon) {
            case LEFT:
                startScroll(SlideMenu.this.getScrollX(), mMenuWidth, mDuration);
                break;
            case RIGHT:
                startScroll(SlideMenu.this.getScrollX(), -mMenuWidth, mDuration);
                break;
        }
        switchMaskView(false);
        mShow = false;
    }


    public boolean isShow(){
        return mShow;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            mIsMoving = true;
        } else {
            mIsMoving = false;
        }
        super.computeScroll();
    }


    public void startScroll(int startX, int dx,int duration){
        mIsMoving = true;
        mScroller.startScroll(startX,0,dx,0,duration);
        invalidate();
    }
}
