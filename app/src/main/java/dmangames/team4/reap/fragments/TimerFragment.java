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

    private int numJars;

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
    private boolean pomodoroBreak = false;
    private boolean animating = false;
    private boolean previouslyCreated = false;
    private long currentTotal;
    private boolean isPaused = false;
    private BreakOverlay boverlay;

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

        Log.d("Timer Fragment", "Fragment started");

        if (!previouslyCreated) {
            Bundle args = getArguments();
            state = State.fromInt(args.getInt(KEY_TIMER_STATE));
            Log.d("New Second Timer", "New Second Timer created");
            timer = new SecondTimer(this);
            previouslyCreated = true;
        } else {
            resumeSecondTimer();
        }

        iconView.setTimer(timer);

        //Create activity object if none exists
        if (activityObject == null) {
            activityObject = new ActivityObject("Null", 0);
        } else {
            iconView.setImageResource(activityObject.getIconRes());
            //currentTotal = activityObject.getTimeSpent();
            Log.d("Timer Fragment", "" + activityObject.getTimeSpent());
            pauseButton.show();
        }

        if (state == NO_ACTIVITY) {
            timerView.setText(getString(R.string.no_timer));
            iconView.setImageResource(R.drawable.no_activity_icon);
            totalTimeView.setText(getString(R.string.no_timer));
        }

        Log.d("Timer Fragment", "Fragment view loaded");
    }

    /*
    ## SecondTimer is saved onStop and whenever the ActivityObject changes
    */
    private void saveSecondTimer() {
        Log.d("timer", "Saving Timer");
        if (timer != null)
            activityObject.addTimeSpent(timer.getSecondsElapsed());
        ((MainActivity) getActivity()).blob.updateActivity(activityObject);
        ((MainActivity) getActivity()).data.getRecentActivities().updateActivity(activityObject);
        currentTotal = activityObject.getTimeSpent();
    }

    @Override public void onStop() {
        super.onStop();
        saveSecondTimer();
        stopSecondTimer();
        Log.d("Stop", "Timer Fragment stopped");
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
        Log.d("timer", "Stopping timer");
        if (timer != null)
            timer.stop();
    }

    @Subscribe(sticky = true) public void onActivityChosen(ChooseActivityObjectEvent event) {
        Log.d(tag(), "Chose activity " + event.object.getActivityName());
        stopSecondTimer();
        timer.reset();
        timerView.setText(R.string.no_timer);
        pauseButton.hide();

        MainActivity activity = (MainActivity) getActivity();
        if(activity.data.getRecentActivities().checkActivity(event.object.getActivityName()))
            activityObject = activity.data.getRecentActivities().getActivity(event.object.getActivityName());
        else {
            activityObject = new ActivityObject(event.object.getActivityName(), event.object.getIconRes());
            activity.data.getRecentActivities().addActivity(activityObject);
        }
        bus.removeStickyEvent(event);

        Log.d("blob",activity.blob.size()+"");

        currentTotal = activityObject.getTimeSpent();

        state = CHOOSE_TIMER;
        iconView.setImageResource(activityObject.getIconRes());
        timerChooser.setVisibility(VISIBLE);

        int iconID = activity.data.getActivityByName(event.object.getActivityName()).getIconRes();
        jarView.changeIcon(iconID);
        long seconds = activity.blob.getActivity(event.object.getActivityName()).getTimeSpent();
        numJars = (int) TimeUnit.SECONDS.toMinutes(seconds);

        jarView.setNumIcons((float)seconds/60);
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
                if(!pomodoroBreak) {
                    actualCurrentTime = POMODORO_WORK_SECS - secs + currentTotal;
                    totalTimeView.setText(String.format("%02d:%02d", (POMODORO_WORK_SECS - secs + currentTotal) / 60, (POMODORO_WORK_SECS - secs + currentTotal) % 60));
                }
                break;
            default:
                Log.e(tag(), "Timer mode does not exist");
        }

//        if (numJars != TimeUnit.SECONDS.toMinutes(actualCurrentTime)) {
//            numJars = (int) TimeUnit.SECONDS.toMinutes(actualCurrentTime);
//            Log.d("D", numJars + "");
//            jarView.setNumIcons(numJars);
//        }
//        Log.d("D", (float)actualCurrentTime/60+"");
        jarView.setNumIcons((float)actualCurrentTime/60);

    }

    @Override public void onTimerFinish() {
        Log.d("onTimerFinish", "Timer finished");
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
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
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
        ((MainActivity) getActivity()).postToBus(
                new SwitchFragmentEvent(ChooseActivityFragment.newInstance(), true, true));
    }

    public class BreakOverlay {
        @Bind(R.id.ol_timer_break) View overlayContainer;
        @Bind(R.id.rv_boverlay_icons) RecyclerView grid;
        @Bind(R.id.iv_timer_blur_container) ImageView blurContainer;

        BreakGridAdapter adapter;

        public BreakOverlay(View view) {
            ButterKnife.bind(this, view);

            Activity activity = getActivity();
            adapter = new BreakGridAdapter(activity);
            grid.setLayoutManager(new GridLayoutManager(activity, 3));
            grid.setAdapter(adapter);
        }

        public void fadeIn() {
            Blurry.with(getActivity()).capture(container).into(blurContainer);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
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
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
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
}
