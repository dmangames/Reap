package dmangames.team4.reap.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies whether a
 * {@link dmangames.team4.reap.fragments.ReapFragment ReapFragment} or a
 * {@link dmangames.team4.reap.adapters.ReapAdapter ReapAdapter} has injectable members. This will
 * trigger a call to
 * {@link dmangames.team4.reap.util.DaggerInjector#inject(Object) DaggerInjector.inject(this)},
 * and the object must be listed in the "injects" section of the
 * {@link dmangames.team4.reap.ReapModule}. Failure to do so will cause a crash.
 *
 * This should go before the class declaration.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface HasInjections {
}

