package dmangames.team4.reap.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;

import java.net.URI;

import static android.content.ContentResolver.SCHEME_ANDROID_RESOURCE;

/**
 * Created by brian on 5/2/16.
 */
public class IconURIs {
    private static final String URI_SCHEME = SCHEME_ANDROID_RESOURCE + "://";

    private static IconURIs instance;

    private final Context context;
    private final String fullUrl;

    private IconURIs(Context context) {
        this.context = context;
        fullUrl = URI_SCHEME + context.getPackageName() + "/drawable/";
    }

    public static void newInstance(Context context) {
        instance = new IconURIs(context);
    }

    public static String get(@DrawableRes int iconRes) {
        return instance.fullUrl + instance.context.getResources().getResourceEntryName(iconRes);
    }
}
