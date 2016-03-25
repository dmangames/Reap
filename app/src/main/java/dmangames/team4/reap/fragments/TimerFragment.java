package dmangames.team4.reap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.events.ChooseActivityEvent;
import dmangames.team4.reap.util.SecondTimer;

import static dmangames.team4.reap.fragments.TimerFragment.State.NO_ACTIVITY;
import static dmangames.team4.reap.fragments.TimerFragment.State.POMODORO;
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
    public enum State {
        NO_ACTIVITY(0),
        CHOOSE_TIMER(1),
        POMODORO(2),
        COUNT_UP(3);

        public final int id;

        State(int id) {
            this.id = id;
        }

        public static State fromInt(int id) {
            if (values()[id].id == id)
                return values()[id];
            for (State s : values()) {
                if (s.id == id)
                    return s;
            }
            throw new IllegalArgumentException("Unknown id!");
        }
    }

    public static final String KEY_TIMER_STATE = "timer.state";
    public static final String KEY_TIMER_COLOR = "timer.color";
    public static final String KEY_TIMER_SECONDS = "timer.seconds";
    public static final String KEY_TIMER_ICON = "timer.icon";

    @Bind(R.id.fl_timer_container) FrameLayout container;
    @Bind(R.id.tv_timer_timer) TextView timerView;
    @Bind(R.id.iv_timer_icon) ImageView iconView;

    @Bind(R.id.ol_timer_pause) View pContainer;
    @Bind(R.id.tv_poverlay_timer) TextView pTimer;

    private State state;
    private SecondTimer timer;

    public static TimerFragment newInstance() {
        Bundle args = new Bundle(1);
        args.putInt(KEY_TIMER_STATE, NO_ACTIVITY.id);
        return getFragmentWithArgs(args);
    }

    public static TimerFragment newInstance(State state, @ColorRes int colorRes,
                                            @DrawableRes int iconRes, long seconds) {
        Bundle args = new Bundle(4);
        args.putInt(KEY_TIMER_STATE, state.id);
        args.putInt(KEY_TIMER_COLOR, colorRes);
        args.putInt(KEY_TIMER_ICON, iconRes);
        args.putLong(KEY_TIMER_SECONDS, seconds);

        return getFragmentWithArgs(args);
    }

    private static TimerFragment getFragmentWithArgs(Bundle args) {
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inf, parent, savedInstanceState);

        Bundle args = getArguments();
        state = State.fromInt(args.getInt(KEY_TIMER_STATE));
        if (state == NO_ACTIVITY) {
            timerView.setText(getString(R.string.no_timer));
            iconView.setImageResource(R.drawable.no_activity_icon);

            iconView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                }
            });
        } else {
            int color = getResources().getColor(args.getInt(KEY_TIMER_COLOR));
            long seconds = args.getLong(KEY_TIMER_SECONDS);

            timerView.setTextColor(color);

            if (state == POMODORO)
                timer = new SecondTimer(COUNT_DOWN, seconds, this);
            else timer = new SecondTimer(COUNT_UP, seconds, this);

            iconView.setImageResource(args.getInt(KEY_TIMER_ICON));
            timer.start();
        }

        return view;
    }

    @Subscribe public void onActivityChosen(ChooseActivityEvent event) {
        
    }

    @Override public void onTick(long secs) {
        timerView.setText(String.format("%02d:%02d", secs / 60, secs % 60));
    }

    @Override public void onFinish() {
        //TODO
    }

    @OnClick(R.id.iv_poverlay_pause) void pauseTimer() {

    }

    @OnClick(R.id.iv_poverlay_switch) void switchActivity() {

    }

    @OnClick(R.id.iv_timer_icon) void openChooseActivityScreen() {
        ChooseActivityFragment fragment = ChooseActivityFragment.newInstance();

        getFragmentManager().popBackStack();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_main_container, fragment)
                .commit();
    }
}
