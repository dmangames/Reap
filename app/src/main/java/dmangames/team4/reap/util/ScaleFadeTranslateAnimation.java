package dmangames.team4.reap.util;

import android.graphics.RectF;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import static android.view.animation.Animation.RELATIVE_TO_PARENT;

/**
 * Created by brian on 5/3/16.
 */
public class ScaleFadeTranslateAnimation {
    AnimationSet animation;

    public ScaleFadeTranslateAnimation(RectF initialPosition, float windowWidth,
                                       float windowHeight, boolean reverse) {
        if (reverse) {
            animation = new AnimationSet(true);
            animation.addAnimation(new TranslateAnimation(
                    RELATIVE_TO_PARENT, 0,
                    RELATIVE_TO_PARENT, initialPosition.left,
                    RELATIVE_TO_PARENT, 0,
                    RELATIVE_TO_PARENT, initialPosition.top));
            animation.addAnimation(new AlphaAnimation(1, 0));
            animation.addAnimation(new ScaleAnimation(1, initialPosition.right / windowWidth,
                    1, initialPosition.bottom / windowHeight));
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(500);
        } else {
            animation = new AnimationSet(true);
            animation.addAnimation(new TranslateAnimation(
                    RELATIVE_TO_PARENT, initialPosition.left,
                    RELATIVE_TO_PARENT, 0,
                    RELATIVE_TO_PARENT, initialPosition.top,
                    RELATIVE_TO_PARENT, 0));
            animation.addAnimation(new AlphaAnimation(0, 1));
            animation.addAnimation(new ScaleAnimation(initialPosition.right / windowWidth, 1,
                    initialPosition.bottom / windowHeight, 1));
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setDuration(500);
        }
    }

    public Animation get() {
        return animation;
    }
}
