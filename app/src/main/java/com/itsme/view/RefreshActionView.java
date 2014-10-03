package com.itsme.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.itsme.R;

public class RefreshActionView extends ImageView {

    private Animation animation;
    private Handler handler;

    public RefreshActionView(Context context) {
        this(context, null);
    }

    public RefreshActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int dp = 10;
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        setPadding(px, 0, px, 0);

        setImageResource(R.drawable.ic_action_refresh);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        animation.setRepeatCount(Animation.INFINITE);

        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        startAnimation(animation);
    }

    public void stop() {
        clearAnimation();
    }

}
