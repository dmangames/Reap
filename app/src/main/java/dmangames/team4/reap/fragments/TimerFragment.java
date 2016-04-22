package dmangames.team4.reap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.adapters.BreakGridAdapter;
import dmangames.team4.reap.adapters.BreakGridAdapter.BreakGridListener;
import dmangames.team4.reap.annotations.HasBusEvents;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.events.ActivityObjectChangedEvent;
import dmangames.team4.reap.events.ActivityObjectDeletedEvent;
import dmangames.team4.reap.events.ChooseActivityObjectEvent;
import dmangames.team4.reap.events.SwitchFragmentEvent;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.util.AnimationEndListener;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.views.IconView;
import dmangames.team4.reap.views.TimerIndicatorView;
import jp.wasabeef.blurry.Blurry;
import timber.log.Timber;

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
@HasInjections
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

    public class BreakOverlay implements BreakGridListener {
        @Bind(R.id.ol_timer_break) View overlayContainer;
        @Bind(R.id.rv_boverlay_icons) RecyclerView grid;
        @Bind(R.id.iv_timer_blur_container) ImageView blurContainer;

        BreakGridAdapter adapter;

        public BreakOverlay(View view) {
            ButterKnife.bind(this, view);

            adapter = new BreakGridAdapter(context, this);
            grid.setLayoutManager(new GridLayoutManager(context, 3));
            grid.setAdapter(adapter);
        }

        public void fadeIn() {
            Blurry.with(context).capture(container).into(blurContainer);
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            overlayContainer.setVisibility(VISIBLE);
            blurContainer.setVisibility(VISIBLE);
            anim.setAnimationListener(new AnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    animating = false;
                }
            });
            overlayContainer.setAnimation(anim);
            blurContainer.setAnimation(anim);
            anim.start();
        }

        public void fadeOut() {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            anim.setAnimationListener(new AnimationEndListener() {
                @Override public void onAnimationEnd(Animation animation) {
                    setVisibilityGone();
                    animating = false;
                }
            });
            overlayContainer.setAnimation(anim);
            blurContainer.setAnimation(anim);
            anim.start();
        }

        @Override
        public void chooseBreak(ActivityObject activityObject) {
            TimerFragment fragment = TimerFragment
                    .newInstance(HOUR, activityObject.getActivityName(), true);
            bus.post(new SwitchFragmentEvent(fragment, false, true));
        }

        public void setVisibilityGone() {
            overlayContainer.setVisibility(GONE);
            blurContainer.setVisibility(GONE);
            blurContainer.setImageDrawable(null);
        }
    }

    public static final String KEY_TIMER_STATE = "timer.state";
    public static final String KEY_TIMER_ACTIVITY = "timer.activity";
    public static final String KEY_TIMER_BREAK = "timer.break";

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

    @Inject DataObject data;
    @Inject ActivityBlob blob;

    private State state;
    private SecondTimer timer;
    private ActivityObject activityObject;
    private BreakOverlay boverlay;
    private Context context;

    private float numJars;

    private boolean pomodoroBreak = false;
    private boolean animating = false;
    private boolean isPaused = false;
    private boolean isBreak = false;
    private boolean previouslyCreated = false;

    public static TimerFragment newInstance() {
        Bundle args = new Bundle(1);
        args.putInt(KEY_TIMER_STATE, NO_ACTIVITY.id);
        return getFragmentWithArgs(args);
    }

    public static TimerFragment newInstance(State state, String activityName, boolean isBreak) {
        Bundle args = new Bundle(3);
        args.putInt(KEY_TIMER_STATE, state.id);
        args.putString(KEY_TIMER_ACTIVITY, activityName);
        args.putBoolean(KEY_TIMER_BREAK, isBreak);

        return getFragmentWithArgs(args);
    }

    private static TimerFragment getFragmentWithArgs(Bundle args) {
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getActivity();
        boverlay = new BreakOverlay(view);

        Timber.d("Fragment started");

        if (!previouslyCreated) {
            Bundle args = getArguments();
            state = State.fromInt(args.getInt(KEY_TIMER_STATE));
            timer = new SecondTimer(this);

            String name = args.getString(KEY_TIMER_ACTIVITY, null);
            if (name != null) {
                isBreak = args.getBoolean(KEY_TIMER_BREAK);
                if (isBreak)
                    activityObject = data.getBreakByName(name);
                else
                    activityObject = data.getActivityByName(name);

                if (state == HOUR)
                    timer.setup(COUNT_UP, COUNT_UP_SECS);
                else if (state == POMODORO) {
                    timer.setup(COUNT_DOWN, POMODORO_WORK_SECS);
                    pomodoroBreak = false;
                }
                reconstructFromState();
            }

            Timber.d("New Second Timer created");
            previouslyCreated = true;
        }

        iconView.setTimer(timer);
        if (activityObject == null)
            activityObject = new ActivityObject("Null", 0);

        Timber.d("Fragment view loaded");
    }

    public void reconstructFromState() {
        boverlay.setVisibilityGone();
        isPaused = false;
        animating = false;

        if (activityObject != null) {
            int iconRes = activityObject.getIconRes();
            if (iconRes == 0)
                iconRes = R.drawable.no_activity_icon;
            numJars = (float) activityObject.getTimeSpent() / 60;
            jarView.changeIcon(iconRes);
            jarView.setNumIcons(numJars);
            iconView.setImageResource(iconRes);
        }

        if (isBreak)
            pauseButton.setImageResource(R.drawable.ic_arrow_down);

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
                timerView.setText(getString(R.string.no_timer));
                totalTimeView.setText(getString(R.string.no_timer));

                pauseButton.hide();
                jarView.setVisibility(GONE);
                timerChooser.setVisibility(VISIBLE);
                break;
            case POMODORO:
                resumeSecondTimer();

                jarView.setVisibility(VISIBLE);
                timerChooser.setVisibility(GONE);
                break;
            case HOUR:
                resumeSecondTimer();

                jarView.setVisibility(VISIBLE);
                timerChooser.setVisibility(GONE);
                break;
            default:
                Timber.e("Unknown state!");
        }
    }

    /*
    ## SecondTimer is saved onStop and whenever the ActivityObject changes
    */
    private void saveSecondTimer() {
        if (activityObject == null || state == NO_ACTIVITY)
            return;
        Timber.d("Saving Timer");
        blob.updateActivity(activityObject);
        data.getRecentActivities().updateActivity(activityObject);
    }

    @Override public void onStop() {
        super.onStop();
        saveSecondTimer();
        stopSecondTimer();
        Timber.d("Timer Fragment stopped");
    }

    @Override public void onResume() {
        super.onResume();
        reconstructFromState();
    }

    private void restartSecondTimer() {
        timer.reset();
        timer.start();
        pauseButton.show();
    }

    private void resumeSecondTimer() {
        timer.start();
        pauseButton.show();
    }

    private void stopSecondTimer() {
        Timber.d("Stopping timer");
        if (timer != null)
            timer.stop();
    }

    @Subscribe(sticky = true) public void onActivityChosen(ChooseActivityObjectEvent event) {
        String activityName = event.object.getActivityName();
        Timber.d("Chose activity %s", activityName);

        stopSecondTimer();
        timer.reset();

        ActivityBlob recent = data.getRecentActivities();
        if (recent.checkActivity(activityName))
            activityObject = recent.getActivity(activityName);
        else {
            activityObject = new ActivityObject(activityName, event.object.getIconRes());
            recent.addActivity(activityObject);
        }

        bus.removeStickyEvent(event);

        state = event.isBreak ? HOUR : CHOOSE_TIMER;
        reconstructFromState();
    }

    @Subscribe(sticky = true) public void onActivityDeleted(ActivityObjectDeletedEvent event) {
        if (activityObject == null || !activityObject.equals(event.object))
            return;

        bus.removeStickyEvent(event);
        state = NO_ACTIVITY;
        activityObject = null;
        reconstructFromState();
    }

    @Subscribe(sticky = true) public void onActivityChanged(ActivityObjectChangedEvent event) {
        if (activityObject == null || !activityObject.equals(event.object))
            return;

        bus.removeStickyEvent(event);
        activityObject = event.object;
        reconstructFromState();
    }

    @Override public void onTimerTick(long secs) {
        timerView.setText(String.format("%02d:%02d", secs / 60, secs % 60));
        activityObject.addTimeSpent(1);
        long timeSpent = activityObject.getTimeSpent();
        totalTimeView.setText(String.format("%02d:%02d", timeSpent / 60, timeSpent % 60));

        numJars = (float) timeSpent / 60;
        jarView.setNumIcons(numJars);

    }

    @Override public void onTimerFinish() {
        Timber.d("Timer finished");
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

    @Override public boolean onBackPressed() {
        if (isPaused) {
            pauseClicked();
            return true;
        }
        return false;
    }

    private void fadeOutTimerChooser() {
        animating = true;
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
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
        if (isBreak) {
            goBack();
            return;
        }
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
        if (isBreak) {
            goBack();
            return;
        }
        bus.post(new SwitchFragmentEvent(ChooseActivityFragment.newInstance(), false, true));
    }

}
