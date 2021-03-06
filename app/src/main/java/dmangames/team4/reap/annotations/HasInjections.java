package dmangames.team4.reap.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dmangames.team4.reap.dagger.DaggerInjector;
import dmangames.team4.reap.dagger.ReapModule;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies whether a
 * {@link dmangames.team4.reap.adapters.ReapAdapter ReapAdapter} or a
 * {@link dmangames.team4.reap.dialogs.ReapDialog} has injectable members. This will
 * trigger a call to
 * {@link DaggerInjector#inject(Object) DaggerInjector.inject(this)},
 * and the object must be listed in the "injects" section of the
 * {@link ReapModule}. Failure to do so will cause a crash.
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

