package dmangames.team4.reap.annotations;

import android.support.annotation.LayoutRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies the layout file to use for a
 * {@link dmangames.team4.reap.fragments.ReapFragment ReapFragment}.
 * This should go before the class declaration.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Layout {

    /**
     * @return The layout id.
     */
    @LayoutRes int value();
}
