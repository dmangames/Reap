package dmangames.team4.reap.annotations;

import android.support.annotation.LayoutRes;

/**
 * Specifies the layout file to use for a
 * {@link dmangames.team4.reap.fragments.ReapFragment ReapFragment}.
 * This should go before the class declaration.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
public @interface Layout {

    /**
     * @return The layout id.
     */
    @LayoutRes int value();
}
