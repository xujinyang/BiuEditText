package jamesxu.biuedittext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by james on 22/11/15.
 */
public class BiuEditText extends EditText {
    private ViewGroup contentContainer;
    private int height;

    public BiuEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setlistener();
    }

    private void setlistener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    return;
                char last = s.charAt(s.length() - 1);
                update(last);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void update(char last) {
        final TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(30);
        textView.setText(String.valueOf(last));
        textView.setGravity(Gravity.CENTER);
        contentContainer.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.measure(0, 0);


        final int[] from = getFromLocation();
        final float startX = from[0];
        final float startY = from[1];
        final float endX = from[0];
        final float endY = 0;

        final AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(textView, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(textView, "translationY", startY, endY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 0.6f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 0.6f, 1.2f);

        animY.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(600);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                contentContainer.removeView(textView);
            }
        });
        animSet.playTogether(animX, animY, scaleX, scaleY);
        animSet.start();
    }

    private int[] getFromLocation() {
        int[] location = new int[2];
        location[0] = new Random().nextInt(contentContainer.getWidth());
        location[1] = height / 3 * 2;
        return location;
    }


    private void init() {
        contentContainer = (ViewGroup) ((MainActivity) getContext()).findViewById(android.R.id.content);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        height = windowManager.getDefaultDisplay().getHeight();
    }

}
