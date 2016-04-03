package dmangames.team4.reap.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies whether a
 * {@link dmangames.team4.reap.fragments.ReapFragment ReapFragment} has bus events, and will
 * require bus registration.
 * This should go before the class declaration.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface HasBusEvents {
}
