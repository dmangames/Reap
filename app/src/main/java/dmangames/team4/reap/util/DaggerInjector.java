package dmangames.team4.reap.util;

import android.content.Context;

import dagger.ObjectGraph;
import dmangames.team4.reap.ReapApplication;
import dmangames.team4.reap.ReapModule;

/**
 * Injector class to manage the <a href="https://square.github.io/dagger/">Dagger</a>
 * {@link ObjectGraph ObjectGraph}. A new instance of this is created in
 * {@link ReapApplication#onCreate()}, and should NOT be modified for the runtime of the app.
 * For use in classes extending {@link dmangames.team4.reap.adapters.ReapAdapter ReapAdapter} or
 * {@link dmangames.team4.reap.fragments.ReapFragment ReapFragment}, see
 * {@link dmangames.team4.reap.annotations.HasInjections HasInjections}.
 * For other cases, see {@link #inject(Object)}.
 *
 * @author Brian Wang
 * @version 04/20/2016
 */
public class DaggerInjector {
    private static DaggerInjector instance;
    private ObjectGraph graph;

    private DaggerInjector(Context context) {
        graph = ObjectGraph.create(new ReapModule(context));
    }

    /**
     * Calls the shared instance's {@link ObjectGraph#inject(Object)} to inject members
     * configurable with <a href="https://square.github.io/dagger/">Dagger</a>. The object being
     * injected MUST be enumerated in {@link ReapModule ReapModule's} injects list.
     *
     * @param obj Object to be injected.
     */
    public static void inject(Object obj) {
        instance.graph.inject(obj);
    }

    public static void newInstance(Context context) {
        instance = new DaggerInjector(context);
    }
}
