package com.smart.util.rxview.progressing.style;


import com.smart.util.rxview.progressing.sprite.Sprite;
import com.smart.util.rxview.progressing.sprite.SpriteContainer;

/**
 * @author vondear
 */
public class MultiplePulse extends SpriteContainer {
    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new Pulse(),
                new Pulse(),
                new Pulse(),
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnimationDelay(200 * (i + 1));
        }
    }
}
