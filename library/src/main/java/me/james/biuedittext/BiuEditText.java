package me.james.biuedittext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import static android.content.ContentValues.TAG;

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
                } else if (cacheStr.length() >= 1) {
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
        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = 0;
        float[] coordinate = getCursorCoordinate();
        Log.i("测试数据1", "X" + coordinate[0] + "Y" + coordinate[1]);
        if (isOpposite) {
            endX = new Random().nextInt(contentContainer.getWidth());
            endY = 0;
            startX = coordinate[0];
            startY = coordinate[1];
        } else {
            startX = coordinate[0];
            startY = -100;
            endX = startX;
            endY = coordinate[1];
        }
        final AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(textView, "translationX", startX, endX);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(textView, "translationY", startY, endY);
        translationY.setEvaluator(new BounceEaseOut(biuDuration));
        animSet.setDuration(biuDuration);
        animSet.addListener(listenerAdapter);
        animSet.playTogether(translationY, animX);
        animSet.start();
    }

    private void playFlyUp(TextView textView, boolean isOpposite, AnimatorListenerAdapter listenerAdapter) {

        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = 0;
        float[] coordinate = getCursorCoordinate();
        Log.i("测试数据2", "X" + coordinate[0] + "Y" + coordinate[1]);
        if (isOpposite) {
            endX = new Random().nextInt(contentContainer.getWidth());
            endY = height / 3 * 2;
            startX = coordinate[0];
            startY = coordinate[1];
        } else {

            startX = coordinate[0];
            startY = height / 3 * 2;
            endX = startX;
            endY = coordinate[1];
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

    /**
     * @return the coordinate of cursor. x=float[0]; y=float[1];
     *
     * thanks @covetcode for this beautiful method
     */
    private float[] getCursorCoordinate() {
     /*
       *以下通过反射获取光标cursor的坐标。
       * 首先观察到TextView的invalidateCursorPath()方法，它是光标闪动时重绘的方法。
       * 方法的最后有个invalidate(bounds.left + horizontalPadding, bounds.top + verticalPadding,
                   bounds.right + horizontalPadding, bounds.bottom + verticalPadding);
       *即光标重绘的区域，由此可得到光标的坐标
       * 具体的坐标在TextView.mEditor.mCursorDrawable里，获得Drawable之后用getBounds()得到Rect。
       * 之后还要获得偏移量修正，通过以下三个方法获得：
       * getVerticalOffset(),getCompoundPaddingLeft(),getExtendedPaddingTop()。
       *
      */

        int xOffset = 0;
        int yOffset = 0;
        Class<?> clazz = EditText.class;
        clazz = clazz.getSuperclass();
        try {
            Field editor = clazz.getDeclaredField("mEditor");
            editor.setAccessible(true);
            Object mEditor = editor.get(this);
            Class<?> editorClazz = Class.forName("android.widget.Editor");
            Field drawables = editorClazz.getDeclaredField("mCursorDrawable");
            drawables.setAccessible(true);
            Drawable[] drawable = (Drawable[]) drawables.get(mEditor);

            Method getVerticalOffset = clazz.getDeclaredMethod("getVerticalOffset", boolean.class);
            Method getCompoundPaddingLeft = clazz.getDeclaredMethod("getCompoundPaddingLeft");
            Method getExtendedPaddingTop = clazz.getDeclaredMethod("getExtendedPaddingTop");
            getVerticalOffset.setAccessible(true);
            getCompoundPaddingLeft.setAccessible(true);
            getExtendedPaddingTop.setAccessible(true);
            if (drawable != null) {
                Rect bounds = drawable[0].getBounds();
                Log.d(TAG, bounds.toString());
                xOffset = (int) getCompoundPaddingLeft.invoke(this) + bounds.left;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        float x = this.getX() + xOffset;
        float y = this.getY();

        return new float[]{x, y};
    }

}
