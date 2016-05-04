package dmangames.team4.reap.fragments;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.adapters.PixelPortraitsAdapter;
import dmangames.team4.reap.adapters.PixelPortraitsAdapter.PixelPortraitItemListener;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.PixelPortrait;
import dmangames.team4.reap.util.AnimationEndListener;
import dmangames.team4.reap.util.ScaleFadeTranslateAnimation;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Brian on 4/28/2016.
 */
@Layout(R.layout.fragment_pixel_portraits)
public class PixelPortraitsFragment extends ReapFragment implements PixelPortraitItemListener {
    @Bind(R.id.rv_pxportrait_portraits) RecyclerView portraits;

    SingleOverlay overlay;

    public static PixelPortraitsFragment newInstance() {
        return new PixelPortraitsFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        overlay = new SingleOverlay(view);

        portraits.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        portraits.setAdapter(new PixelPortraitsAdapter(getActivity(), this));
    }

    @Override
    public void onPortraitSelected(RectF imgPos, ActivityObject object, PixelPortrait portrait) {
        overlay.animateVisibility(true, imgPos);
        overlay.setData(object, portrait);
    }

    @Override public boolean onBackPressed() {
        if (overlay.animate)
            return true;
        if (overlay.visible) {
            overlay.animateVisibility(false, null);
            return true;
        }
        return false;
    }

    class SingleOverlay {
        @Bind(R.id.ol_pxportrait_single) View container;
        @Bind(R.id.iv_pxportrait_large) ImageView largePortrait;
        @Bind(R.id.tv_pxportrait_name) TextView name;
        @Bind(R.id.tv_pxportrait_progress) TextView progress;

        boolean visible;
        boolean animate;

        private RectF formerPos;

        public SingleOverlay(View view) {
            ButterKnife.bind(this, view);
        }

        public void animateVisibility(final boolean visible, @Nullable RectF pos) {
            this.visible = visible;
            this.animate = true;
            Animation anim = new ScaleFadeTranslateAnimation(visible ? pos : formerPos,
                    container.getWidth(), container.getHeight(), !visible).get();
            anim.setAnimationListener(new AnimationEndListener() {
                @Override public void onAnimationEnd(Animation animation) {
                    container.setVisibility(visible ? VISIBLE : INVISIBLE);
                    animate = false;
                }
            });
            formerPos = visible ? pos : null;
            container.startAnimation(anim);
        }

        public void setData(ActivityObject object, PixelPortrait portrait) {
            portrait.loadInto(getActivity(), largePortrait);
            name.setText(object.getActivityName());
            progress.setText(String.format(Locale.US, "%,d/%,d hours",
                    object.getTimeSpent() / TimeUnit.MINUTES.toSeconds(1), 10000));
        }
    }
}
