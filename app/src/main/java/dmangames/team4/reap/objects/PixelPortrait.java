package dmangames.team4.reap.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dmangames.team4.reap.dagger.DaggerInjector;
import timber.log.Timber;

/**
 * Created by Brian on 4/28/2016.
 */
public class PixelPortrait {
    private static final long HOUR_SECS = TimeUnit.MINUTES.toSeconds(1);

    private String activityName;

    @Inject
    transient DataObject data;
    // This can be created by the activity name every time.
    private transient File file;
    // We don't want this bitmap serialized by GSON. We'll let Picasso handle it.
    private transient Bitmap portrait;
    private transient boolean updated;

    public PixelPortrait(Context context, String activityName) {
        this.activityName = activityName;
        DaggerInjector.inject(this);
        file = new File(context.getFilesDir(), activityName + ".png");
        updated = false;
    }

    public void loadInto(Context context, ImageView view) {
        if (!updated) {
            createPortrait();
            updated = true;
        }
        int min = Math.min(view.getWidth(), view.getHeight());
        Picasso.with(context).load(file).resize(min, min).centerInside().into(view);
    }

    private void createPortrait() {
        portrait = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        update();
    }

    /**
     * Updates the pixel portrait from its
     */
    private void update() {
        ActivityObject activity = data.getActivityByName(activityName);
        Canvas canvas = new Canvas(portrait);
        Paint paint = new Paint();
        // TODO paint.setColor
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);

        int lines = (int) (activity.getTimeSpent() / (100 * HOUR_SECS));
        int dots = (int) (activity.getTimeSpent() / HOUR_SECS) % 100;

        if (lines == 0)
            canvas.drawLine(0, 0, dots, 0, paint);
        else {
            canvas.drawLine(0, lines + 1, dots, lines + 1, paint);
            canvas.drawRect(new RectF(0, 0, portrait.getWidth(), lines), paint);
        }

        save();
    }

    private void save() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            portrait.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                portrait.recycle();
                portrait = null;
            } catch (IOException e) {
                Timber.e(e, "Exception in save!");
            }
        }
    }

    public void cleanUp() {
        if (file != null && file.exists())
            file.delete();
    }
}
