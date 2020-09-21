package com.zydl.util.rxview.progressing.style;


import com.zydl.util.rxview.progressing.sprite.Sprite;
import com.zydl.util.rxview.progressing.sprite.SpriteContainer;

/**
 * @author vondear
 */
public class MultiplePulseRing extends SpriteContainer {

    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new PulseRing(),
                new PulseRing(),
                new PulseRing(),
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnimationDelay(200 * (i + 1));
        }
    }
}
