package dmangames.team4.reap.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.adapters.BreakGridAdapter;
import dmangames.team4.reap.annotations.HasBusEvents;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.events.ChooseActivityObjectEvent;
import dmangames.team4.reap.events.SwitchFragmentEvent;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.util.AnimationEndListener;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.views.IconView;
import dmangames.team4.reap.views.TimerIndicatorView;
import jp.wasabeef.blurry.Blurry;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static dmangames.team4.reap.fragments.TimerFragment.State.CHOOSE_TIMER;
import static dmangames.team4.reap.fragments.TimerFragment.State.HOUR;
import static dmangames.team4.reap.fragments.TimerFragment.State.NO_ACTIVITY;
import static dmangames.team4.reap.fragments.TimerFragment.State.POMODORO;
import static dmangames.team4.reap.util.SecondTimer.SecondListener;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_UP;
import static java.util.concurrent.TimeUnit.MINUTES;

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

    public class BreakOverlay {
        @Bind(R.id.ol_timer_break) View overlayContainer;
        @Bind(R.id.rv_boverlay_icons) RecyclerView grid;
        @Bind(R.id.iv_timer_blur_container) ImageView blurContainer;

        BreakGridAdapter adapter;

        public BreakOverlay(View view) {
            ButterKnife.bind(this, view);

            adapter = new BreakGridAdapter(activity);
            grid.setLayoutManager(new GridLayoutManager(activity, 3));
            grid.setAdapter(adapter);
        }

        public void fadeIn() {
            Blurry.with(activity).capture(container).into(blurContainer);
            Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
            anim.setAnimationListener(new AnimationEndListener() {
                @Override public void onAnimationEnd(Animation animation) {
                    overlayContainer.setVisibility(VISIBLE);
                    blurContainer.setVisibility(VISIBLE);
                    animating = false;
                }
            });
            overlayContainer.setAnimation(anim);
            blurContainer.setAnimation(anim);
            anim.start();
        }

        public void fadeOut() {
            Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
            anim.setAnimationListener(new AnimationEndListener() {
                @Override public void onAnimationEnd(Animation animation) {
                    overlayContainer.setVisibility(GONE);
                    blurContainer.setVisibility(GONE);
                    blurContainer.setImageDrawable(null);
                    animating = false;
                }
            });
            overlayContainer.setAnimation(anim);
            blurContainer.setAnimation(anim);
            anim.start();
        }
    }

    public static final String KEY_TIMER_STATE = "timer.state";
    public static final String KEY_TIMER_ACTIVITY = "timer.activity";

    private static final long POMODORO_WORK_SECS = MINUTES.toSeconds(1);
    private static final long POMODORO_BREAK_SECS = MINUTES.toSeconds(1);
    private static final long COUNT_UP_SECS = MINUTES.toSeconds(1);

    @Bind(R.id.tv_timer_timer) TextView timerView;
    @Bind(R.id.iv_timer_icon) TimerIndicatorView iconView;
    @Bind(R.id.fl_timer_container) FrameLayout container;
    @Bind(R.id.fab_timer_pause) FloatingActionButton pauseButton;
    @Bind(R.id.icv_timer_icons) IconView jarView;
    @Bind(R.id.tv_total_time) TextView totalTimeView;
    @Bind(R.id.ll_timer_chooser) View timerChooser;

    private State state;
    private SecondTimer timer;
    private ActivityObject activityObject;
    private BreakOverlay boverlay;
    private MainActivity activity;

    private int numJars;
    private long currentTotal;

    private boolean pomodoroBreak = false;
    private boolean animating = false;
    private boolean isPaused = false;
    private boolean previouslyCreated = false;

    public static TimerFragment newInstance() {
        Bundle args = new Bundle(1);
        args.putInt(KEY_TIMER_STATE, NO_ACTIVITY.id);
        return getFragmentWithArgs(args);
    }

    public static TimerFragment newInstance(State state, String activityName) {
        Bundle args = new Bundle(2);
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
        boverlay = new BreakOverlay(view);
        activity = (MainActivity) getActivity();

        Log.d(tag(), "Fragment started");

        if (!previouslyCreated) {
            Bundle args = getArguments();
            state = State.fromInt(args.getInt(KEY_TIMER_STATE));
            Log.d(tag(), "New Second Timer created");
            timer = new SecondTimer(this);
            previouslyCreated = true;
        }

        iconView.setTimer(timer);
        if (activityObject == null)
            activityObject = new ActivityObject("Null", 0);
        reconstructFromState();

        Log.d(tag(), "Fragment view loaded");
    }

    public void reconstructFromState() {
        switch (state) {
            case NO_ACTIVITY:
                timerView.setText(getString(R.string.no_timer));
                totalTimeView.setText(getString(R.string.no_timer));
                iconView.setImageResource(R.drawable.no_activity_icon);

                pauseButton.hide();
                jarView.setVisibility(GONE);
                timerChooser.setVisibility(GONE);
                break;
            case CHOOSE_TIMER:
                int iconRes = activityObject.getIconRes();
                timerView.setText(getString(R.string.no_timer));
                totalTimeView.setText(getString(R.string.no_timer));
                iconView.setImageResource(iconRes);

                currentTotal = activityObject.getTimeSpent();
                numJars = (int) TimeUnit.SECONDS.toMinutes(currentTotal);
                jarView.changeIcon(iconRes);
                jarView.setNumIcons(numJars);

                pauseButton.hide();
                jarView.setVisibility(GONE);
                timerChooser.setVisibility(VISIBLE);
                break;
            case POMODORO:
                iconView.setImageResource(activityObject.getIconRes());

                resumeSecondTimer();

                pauseButton.show();
                jarView.setVisibility(VISIBLE);
                timerChooser.setVisibility(GONE);
                break;
            case HOUR:
                iconView.setImageResource(activityObject.getIconRes());
                resumeSecondTimer();

                pauseButton.show();
                jarView.setVisibility(VISIBLE);
                timerChooser.setVisibility(GONE);
                break;
            default:
                Log.e(tag(), "Unknown state!");
        }
    }

    /*
    ## SecondTimer is saved onStop and whenever the ActivityObject changes
    */
    private void saveSecondTimer() {
        Log.d(tag(), "Saving Timer");
        if (timer != null)
            activityObject.addTimeSpent(timer.getSecondsElapsed());
        activity.blob.updateActivity(activityObject);
        activity.data.getRecentActivities().updateActivity(activityObject);
        currentTotal = activityObject.getTimeSpent();
    }

    @Override public void onStop() {
        super.onStop();
        saveSecondTimer();
        stopSecondTimer();
        Log.d(tag(), "Timer Fragment stopped");
    }

    private void restartSecondTimer() {
        timer.reset();
        timer.start();
        pauseButton.show();
    }

    private void resumeSecondTimer() {
        timer.start();
    }

    private void stopSecondTimer() {
        Log.d(tag(), "Stopping timer");
        if (timer != null)
            timer.stop();
    }

    @Subscribe(sticky = true) public void onActivityChosen(ChooseActivityObjectEvent event) {
        String activityName = event.object.getActivityName();
        Log.d(tag(), "Chose activity " + activityName);

        stopSecondTimer();
        timer.reset();

        ActivityBlob recent = activity.data.getRecentActivities();
        if (recent.checkActivity(activityName))
            activityObject = recent.getActivity(activityName);
        else {
            activityObject = new ActivityObject(activityName, event.object.getIconRes());
            recent.addActivity(activityObject);
        }
        bus.removeStickyEvent(event);

        state = CHOOSE_TIMER;
        reconstructFromState();
    }

    @Override public void onTimerTick(long secs) {
        timerView.setText(String.format("%02d:%02d", secs / 60, secs % 60));
        long actualCurrentTime = POMODORO_WORK_SECS - secs + currentTotal;
        switch (state) {
            case HOUR:
                actualCurrentTime = secs + currentTotal;
                totalTimeView.setText(String.format("%02d:%02d", (secs + currentTotal) / 60, (secs + currentTotal) % 60));
                break;
            case POMODORO:
                if (!pomodoroBreak) {
                    actualCurrentTime = POMODORO_WORK_SECS - secs + currentTotal;
                    totalTimeView.setText(String.format("%02d:%02d", (POMODORO_WORK_SECS - secs + currentTotal) / 60, (POMODORO_WORK_SECS - secs + currentTotal) % 60));
                }
                break;
            default:
                Log.e(tag(), "Timer mode does not exist");
        }

        if (numJars != TimeUnit.SECONDS.toMinutes(actualCurrentTime)) {
            numJars = (int) TimeUnit.SECONDS.toMinutes(actualCurrentTime);
            Log.d(tag(), numJars + "");
            jarView.setNumIcons(numJars);
        }
    }

    @Override public void onTimerFinish() {
        Log.d(tag(), "Timer finished");
        if (state == POMODORO) {
            if (!pomodoroBreak)
                saveSecondTimer();

            pomodoroBreak = !pomodoroBreak;
            if (pomodoroBreak)
                timer.setTotalSeconds(POMODORO_BREAK_SECS);
            else
                timer.setTotalSeconds(POMODORO_WORK_SECS);
        } else
            saveSecondTimer();

        restartSecondTimer();
    }

    private void fadeOutTimerChooser() {
        animating = true;
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        anim.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                timerChooser.setVisibility(GONE);
                jarView.setVisibility(VISIBLE);
                animating = false;
            }
        });
        timerChooser.startAnimation(anim);
    }

    @OnClick(R.id.iv_timer_pomodoro) void selectPomodoro() {
        if (animating)
            return;
        state = POMODORO;
        pomodoroBreak = false;
        fadeOutTimerChooser();

        timer.setup(COUNT_DOWN, POMODORO_WORK_SECS);
        restartSecondTimer();
    }

    @OnClick(R.id.iv_timer_stopwatch) void selectStopwatch() {
        if (animating)
            return;
        state = HOUR;
        fadeOutTimerChooser();

        timer.setup(COUNT_UP, COUNT_UP_SECS);
        restartSecondTimer();
    }

    @OnClick(R.id.fab_timer_pause) void pauseClicked() {
        if (animating)
            return;
        animating = true;
        if (!isPaused) {
            stopSecondTimer();
            boverlay.fadeIn();
        } else {
            resumeSecondTimer();
            boverlay.fadeOut();
        }
        isPaused = !isPaused;
    }

    @OnClick(R.id.iv_timer_icon) void chooseActivity() {
        activity.postToBus(new SwitchFragmentEvent(ChooseActivityFragment.newInstance(), true, true));
    }
}
