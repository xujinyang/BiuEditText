package me.james.biuedittext;

import android.animation.TypeEvaluator;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class BaseEasingMethod implements TypeEvaluator<Number> {
    protected float mDuration;
    private ArrayList<EasingListener> mListeners = new ArrayList();

    public void addEasingListener(BaseEasingMethod.EasingListener l) {
        this.mListeners.add(l);
    }

    public void addEasingListeners(BaseEasingMethod.EasingListener... ls) {
        BaseEasingMethod.EasingListener[] arr$ = ls;
        int len$ = ls.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            BaseEasingMethod.EasingListener l = arr$[i$];
            this.mListeners.add(l);
        }

    }

    public void removeEasingListener(BaseEasingMethod.EasingListener l) {
        this.mListeners.remove(l);
    }

    public void clearEasingListeners() {
        this.mListeners.clear();
    }

    public BaseEasingMethod(float duration) {
        this.mDuration = duration;
    }

    public void setDuration(float duration) {
        this.mDuration = duration;
    }

    public final Float evaluate(float fraction, Number startValue, Number endValue) {
        float t = this.mDuration * fraction;
        float b = startValue.floatValue();
        float c = endValue.floatValue() - startValue.floatValue();
        float d = this.mDuration;
        float result = this.calculate(t, b, c, d).floatValue();
        Iterator i$ = this.mListeners.iterator();

        while(i$.hasNext()) {
            BaseEasingMethod.EasingListener l = (BaseEasingMethod.EasingListener)i$.next();
            l.on(t, result, b, c, d);
        }

        return Float.valueOf(result);
    }

    public abstract Float calculate(float var1, float var2, float var3, float var4);

    public interface EasingListener {
        void on(float var1, float var2, float var3, float var4, float var5);
    }
}