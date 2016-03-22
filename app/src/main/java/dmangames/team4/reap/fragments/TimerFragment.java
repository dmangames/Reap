package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.util.SecondTimer;

import static dmangames.team4.reap.util.SecondTimer.SecondListener;
import static dmangames.team4.reap.util.SecondTimer.Type;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_UP;

/**
 * Timer fragment.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
@Layout(R.layout.fragment_timer)
public class TimerFragment extends ReapFragment implements SecondListener {
    public static final String KEY_TIMER_SECONDS = "length";
    public static final String KEY_TIMER_TYPE = "type";
    public static final String KEY_TIMER_COLOR = "color";
    public static final String KEY_ICON = "icon";

    @Bind(R.id.tv_timer_timer) TextView timerView;
    @Bind(R.id.iv_timer_icon) ImageView iconView;

    private SecondTimer timer;

    public static TimerFragment newInstance(Type timerType, long seconds,
                                            @ColorRes int colorRes, @DrawableRes int iconRes) {
        Bundle args = new Bundle(4);
        args.putBoolean(KEY_TIMER_TYPE, timerType.id);
        args.putLong(KEY_TIMER_SECONDS, seconds);
        args.putInt(KEY_TIMER_COLOR, colorRes);
        args.putInt(KEY_ICON, iconRes);

        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inf, parent, savedInstanceState);

        Bundle args = getArguments();
        Type timerType = args.getBoolean(KEY_TIMER_TYPE) == COUNT_DOWN.id ? COUNT_DOWN : COUNT_UP;
        long timerSeconds = args.getLong(KEY_TIMER_SECONDS);
        int timerColorRes = args.getInt(KEY_TIMER_COLOR);
        int timerIconRes = args.getInt(KEY_ICON);

        timerView.setTextColor(getResources().getColor(timerColorRes));
        iconView.setImageResource(timerIconRes);

        timer = new SecondTimer(timerType, timerSeconds, this);
        timer.start();

        return view;
    }

    @Override public void onTick(long secs) {
        timerView.setText(String.format("%02d:%02d", secs / 60, secs % 60));
    }

    @Override public void onFinish() {
        //TODO
    }
}
