package dmangames.team4.reap.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import dmangames.team4.reap.R;
import dmangames.team4.reap.objects.DataObject;
import timber.log.Timber;

/**
 * Created by Andrew on 3/28/2016.
 */
public class GsonWrapper {
    public static void commitData(DataObject data, Context context) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        data.getRecentActivities().removeNulls();
        Timber.d(gson.toJson(data));

        String filename = "data.dat";
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(gson.toJson(data).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkDataObject(Context context) {

        String filename = "data.dat";
        File file = new File(context.getFilesDir(), filename);
        return file.exists();

    }


    public static DataObject getDataObject(Context context) {

        String filename = "data.dat";
        File file = new File(context.getFilesDir(), filename);

        //Read text from file
        StringBuilder json = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                json.append(line);
                json.append('\n');
            }
            br.close();
        } catch (IOException e) {
        }

        Gson gson = new Gson();

        Timber.d(json.toString());

        return gson.fromJson(json.toString(), DataObject.class);

    }

    public static DataObject mockData(Context context) {
        Scanner input = new Scanner(context.getResources().openRawResource(R.raw.mock));

        StringBuilder builder = new StringBuilder();
        while (input.hasNextLine())
            builder.append(input.nextLine());
        Gson gson = new Gson();
        Timber.d("Mock Data: %s", builder);
        return gson.fromJson(builder.toString(), DataObject.class);
    }

}
