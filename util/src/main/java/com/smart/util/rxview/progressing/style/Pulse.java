package com.zydl.util.rxview.progressing.style;

import android.animation.ValueAnimator;

import com.zydl.util.rxview.progressing.animation.SpriteAnimatorBuilder;
import com.zydl.util.rxview.progressing.sprite.CircleSprite;


/**
 * @author vondear
 */
public class Pulse extends CircleSprite {

    public Pulse() {
        setScale(0f);
    }

    @Override
    public ValueAnimator onCreateAnimation() {
        float fractions[] = new float[]{0f, 1f};
        return new SpriteAnimatorBuilder(this).
                scale(fractions, 0f, 1f).
                alpha(fractions, 255, 0).
                duration(1000).
                easeInOut(fractions)
                .build();
    }
}
