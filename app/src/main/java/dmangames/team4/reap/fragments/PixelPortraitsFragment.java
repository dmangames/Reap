package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.adapters.PixelPortraitsAdapter;
import dmangames.team4.reap.annotations.Layout;

/**
 * Created by Brian on 4/28/2016.
 */
@Layout(R.layout.fragment_pixel_portraits)
public class PixelPortraitsFragment extends ReapFragment {
    @Bind(R.id.rv_pxportrait_portraits) RecyclerView portraits;

    public static PixelPortraitsFragment newInstance() {
        return new PixelPortraitsFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        portraits.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        portraits.setAdapter(new PixelPortraitsAdapter(getActivity()));
    }
}
