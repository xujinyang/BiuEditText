package me.james.biuedittext;

public class BounceEaseOut extends BaseEasingMethod {
    public BounceEaseOut(float duration) {
        super(duration);
    }

    public Float calculate(float t, float b, float c, float d) {
        return (t /= d) < 0.36363637F?Float.valueOf(c * 7.5625F * t * t + b):(t < 0.72727275F?Float.valueOf(c * (7.5625F * (t -= 0.54545456F) * t + 0.75F) + b):((double)t < 0.9090909090909091D?Float.valueOf(c * (7.5625F * (t -= 0.8181818F) * t + 0.9375F) + b):Float.valueOf(c * (7.5625F * (t -= 0.95454544F) * t + 0.984375F) + b)));
    }
}