package jamesxu.biuedittext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.Layout;
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
    private String cacheStr = "";

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

                if (cacheStr.length() < s.length()) {
                    char last = s.charAt(s.length() - 1);
                    update(last, true);
                } else {
                    char last = cacheStr.charAt(cacheStr.length() - 1);
                    update(last, false);
                }
                cacheStr = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void update(char last, boolean isUp) {
        final TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(30);
        textView.setText(String.valueOf(last));
        textView.setGravity(Gravity.CENTER);
        contentContainer.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.measure(0, 0);


        int pos = getSelectionStart();
        Layout layout = getLayout();
        int line = layout.getLineForOffset(pos);
        int baseline = layout.getLineBaseline(line);
        int ascent = layout.getLineAscent(line);

        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = 0;
        if (isUp) {
            startX = layout.getPrimaryHorizontal(pos) + 100;
            startY = height / 3 * 2;
            endX = startX;
            endY = baseline + ascent;
        } else {
            endX = new Random().nextInt(contentContainer.getWidth());
            endY = height / 3 * 2;
            startX = layout.getPrimaryHorizontal(pos) + 70;
            startY = baseline + ascent;
        }


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


    private void init() {
        contentContainer = (ViewGroup) ((MainActivity) getContext()).findViewById(android.R.id.content);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        height = windowManager.getDefaultDisplay().getHeight();
    }

}
