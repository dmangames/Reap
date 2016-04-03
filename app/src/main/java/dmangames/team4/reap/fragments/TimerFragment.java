package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.annotations.HasBusEvents;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.events.ChooseActivityObjectEvent;
import dmangames.team4.reap.events.SwitchFragmentEvent;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.views.TimerIndicatorView;

import static dmangames.team4.reap.fragments.TimerFragment.State.CHOOSE_TIMER;
import static dmangames.team4.reap.fragments.TimerFragment.State.HOUR;
import static dmangames.team4.reap.fragments.TimerFragment.State.NO_ACTIVITY;
import static dmangames.team4.reap.fragments.TimerFragment.State.POMODORO;
import static dmangames.team4.reap.util.SecondTimer.SecondListener;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_UP;

/**
 * Timer fragment.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
@HasBusEvents
@Layout(R.layout.fragment_timer)
public class TimerFragment extends ReapFragment implements SecondListener {
    public enum State {
        NO_ACTIVITY(0),
        CHOOSE_TIMER(1),
        POMODORO(2),
        HOUR(3);

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
    public static final String KEY_TIMER_ACTIVITY = "timer.activity";

    @Bind(R.id.fl_timer_container) FrameLayout container;
    @Bind(R.id.tv_timer_timer) TextView timerView;
    @Bind(R.id.iv_timer_icon) TimerIndicatorView iconView;

    @Bind(R.id.ol_timer_pause) View pContainer;
    @Bind(R.id.tv_poverlay_timer) TextView pTimer;
    @Bind(R.id.ll_timer_chooser) View timerChooser;

    private State state;
    private SecondTimer timer;
    private ActivityObject activityObject;
    private boolean pomodoroBreak = false;

    public static TimerFragment newInstance() {
        Bundle args = new Bundle(1);
        args.putInt(KEY_TIMER_STATE, NO_ACTIVITY.id);
        return getFragmentWithArgs(args);
    }

    public static TimerFragment newInstance(State state, String activityName) {
        Bundle args = new Bundle(4);
        args.putInt(KEY_TIMER_STATE, state.id);
        args.putString(KEY_TIMER_ACTIVITY, activityName);

        return getFragmentWithArgs(args);
    }

    private static TimerFragment getFragmentWithArgs(Bundle args) {
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("Timer Fragment", "Fragment started");

        Bundle args = getArguments();
        state = State.fromInt(args.getInt(KEY_TIMER_STATE));
        timer = new SecondTimer(COUNT_UP, 3600, this);
        iconView.setTimer(timer);

        //Create activity object if none exists
        if (activityObject == null){
            activityObject = new ActivityObject("Null", 0);
        }

        if (state == NO_ACTIVITY) {
            timerView.setText(getString(R.string.no_timer));
            iconView.setImageResource(R.drawable.no_activity_icon);

            iconView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    saveSecondTimer();
                    ((MainActivity) getActivity()).postToBus(
                            new SwitchFragmentEvent(ChooseActivityFragment.newInstance(), true, true));
                }
            });
        }

        Log.d("Timer Fragment", "Fragment view loaded");
    }

    /*
    ## SecondTimer is saved onStop and whenever the ActivityObject changes
    */
    private void saveSecondTimer() {
        Log.d("timer", "Saving Timer");
        if(timer != null)
            activityObject.addTimeSpent(timer.getTotalSeconds());
    }

    @Override public void onStop() {
        super.onStop();
        saveSecondTimer();
    }

    private void restartSecondTimer() {
        timer.reset();
        timer.start();
    }
    private void stopSecondTimer() {
        Log.d("timer", "Stopping timer");
        if(timer != null)
            timer.stop();
    }

    @Subscribe(sticky = true) public void onActivityChosen(ChooseActivityObjectEvent event) {
        Log.d(tag(), "Chose activity " + event.object.getActivityName());
        saveSecondTimer();
        stopSecondTimer();

        activityObject = event.object;
        bus.removeStickyEvent(event);

        state = CHOOSE_TIMER;
        iconView.setOnClickListener(null);
        iconView.setImageResource(activityObject.getIconRes());
        timerChooser.setVisibility(View.VISIBLE);
    }

    @Override public void onTimerTick(long secs) {
        timerView.setText(String.format("%02d:%02d", secs / 60, secs % 60));
    }

    @Override public void onTimerFinish() {
        saveSecondTimer();
        pomodoroBreak = !pomodoroBreak;
        if (pomodoroBreak)
            timer.setTotalSeconds(TimeUnit.MINUTES.toSeconds(5));
        restartSecondTimer();
    }

    @OnClick(R.id.iv_timer_pomodoro) void selectPomodoro() {
        state = POMODORO;
        pomodoroBreak = false;
        timerChooser.setVisibility(View.GONE);

        timer.setup(COUNT_DOWN, TimeUnit.MINUTES.toSeconds(25));
        restartSecondTimer();
    }

    @OnClick(R.id.iv_timer_stopwatch) void selectStopwatch() {
        state = HOUR;
        timerChooser.setVisibility(View.GONE);

        timer.setup(COUNT_UP, TimeUnit.HOURS.toSeconds(1));
        restartSecondTimer();
    }


    @OnClick(R.id.iv_poverlay_pause) void pauseTimer() {
    }

    @OnClick(R.id.iv_poverlay_switch) void switchActivity() {
        bus.post(new SwitchFragmentEvent(ChooseActivityFragment.newInstance(), true, true));
    }
}
