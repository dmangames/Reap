package dmangames.team4.reap.events;

import dmangames.team4.reap.util.SecondTimer;

/**
 * Created by brian on 4/15/16.
 */
public class AddTimerEvent {
    public final long timeSpent;
    public final long currentSecs;
    public final SecondTimer.Type timerType;
    public final boolean pomodoroBreak;

    public AddTimerEvent(long timeSpent, long currentSecs, SecondTimer.Type timerType, boolean pomodoroBreak) {
        this.timeSpent = timeSpent;
        this.currentSecs = currentSecs;
        this.timerType = timerType;
        this.pomodoroBreak = pomodoroBreak;
    }
}
