package dmangames.team4.reap.util;

import android.content.Context;

import dagger.ObjectGraph;
import dmangames.team4.reap.ReapModule;

/**
 * Created by brian on 4/20/16.
 */
public class DaggerInjector {
    private static DaggerInjector instance;
    private ObjectGraph graph;

    private DaggerInjector(Context context) {
        graph = ObjectGraph.create(new ReapModule(context));
    }

    public static void inject(Object obj) {
        instance.graph.inject(obj);
    }

    public static void newInstance(Context context) {
        instance = new DaggerInjector(context);
    }
}
