package me.james.biuedittext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
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
    private static final int ANIMATION_DEFAULT = 0;
    private static final int ANIMATION_DROPOUT = 1;
    private static final int DEFAULT_DURATION = 600;
    private static final float DEFAULT_SCALE = 1.2f;

    private int biuTextColor;
    private float biuTextStartSize;
    private float biuTextScale;
    private int biuDuration;
    private int biuType;

    public BiuEditText(Context context) {
        super(context);
    }

    public BiuEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setlistener();
    }

    public BiuEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode())
            return;

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BiuEditStyle);
        biuTextColor = typedArray.getColor(R.styleable.BiuEditStyle_biu_text_color, getResources().getColor(R.color.white));
        biuTextStartSize = typedArray.getDimension(R.styleable.BiuEditStyle_biu_text_start_size, getResources().getDimension(R.dimen.biu_text_start_size));
        biuTextScale = typedArray.getFloat(R.styleable.BiuEditStyle_biu_text_scale, DEFAULT_SCALE);
        biuDuration = typedArray.getInt(R.styleable.BiuEditStyle_biu_duration, DEFAULT_DURATION);
        biuType = typedArray.getInt(R.styleable.BiuEditStyle_biu_type, 0);
        typedArray.recycle();

        contentContainer = (ViewGroup) ((Activity) getContext()).findViewById(android.R.id.content);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        height = windowManager.getDefaultDisplay().getHeight();
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
                    update(last, false);
                } else {
                    char last = cacheStr.charAt(cacheStr.length() - 1);
                    update(last, true);
                }
                cacheStr = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void update(char last, boolean isOpposite) {
        final TextView textView = new TextView(getContext());
        textView.setTextColor(biuTextColor);
        textView.setTextSize(biuTextStartSize);
        textView.setText(String.valueOf(last));
        textView.setGravity(Gravity.CENTER);
        contentContainer.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.measure(0, 0);
        playAnaimator(textView, isOpposite, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                contentContainer.removeView(textView);
            }
        });


    }

    private void playAnaimator(TextView textView, boolean isOpposite, AnimatorListenerAdapter listenerAdapter) {

        switch (biuType) {
            case ANIMATION_DEFAULT:
                playFlyUp(textView, isOpposite, listenerAdapter);
                break;
            case ANIMATION_DROPOUT:
                playFlyDown(textView, isOpposite, listenerAdapter);
                break;
            default:
                break;
        }

    }

    private void playFlyDown(TextView textView, boolean isOpposite, AnimatorListenerAdapter listenerAdapter) {
        int pos = getSelectionStart();
        Layout layout = getLayout();
        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = 0;
        if (isOpposite) {
            endX = new Random().nextInt(contentContainer.getWidth());
            endY = 0;
            startX = layout.getPrimaryHorizontal(pos) + 100;
            startY = getY() - 100;
        } else {
            startX = layout.getPrimaryHorizontal(pos) + 100;
            startY = -100;
            endX = startX;
            endY = getY() - 100;
        }
        final AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(textView, "translationX", startX, endX);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(textView, "alpha", 0, 1);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(textView, "translationY", startY, endY);
        translationY.setEvaluator(new BounceEaseOut(biuDuration));
        animSet.setDuration(biuDuration);
        animSet.addListener(listenerAdapter);
        animSet.playTogether(alpha, translationY, animX);
        animSet.start();
    }

    private void playFlyUp(TextView textView, boolean isOpposite, AnimatorListenerAdapter listenerAdapter) {
        int pos = getSelectionStart();
        Layout layout = getLayout();

        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = 0;
        if (isOpposite) {
            endX = new Random().nextInt(contentContainer.getWidth());
            endY = height / 3 * 2;
            startX = layout.getPrimaryHorizontal(pos) + 100;
            startY = getY();
        } else {

            startX = layout.getPrimaryHorizontal(pos) + 100;
            startY = height / 3 * 2;
            endX = startX;
            endY = getY();
        }
        final AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(textView, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(textView, "translationY", startY, endY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, biuTextScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, biuTextScale);

        animY.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(biuDuration);
        animSet.addListener(listenerAdapter);
        animSet.playTogether(animX, animY, scaleX, scaleY);
        animSet.start();
    }

}
