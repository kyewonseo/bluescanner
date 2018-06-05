package net.bluehack.bluescanner.firebase;


import android.content.Context;
import android.util.JsonReader;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.bluehack.bluescanner.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseDatabaseHelper {

    private static final String FB_DB_NAME_BLOCKS           = "bluelens-browser-blocks";

    private String APP_ID;
    private String API_KEY;

    private static final FirebaseDatabaseHelper INSTANCE = new FirebaseDatabaseHelper();
    private DatabaseReference databaseDefault;
    private DatabaseReference databaseBlocks;

    public static FirebaseDatabaseHelper getInstance() {
        return INSTANCE;
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private FirebaseDatabaseHelper() {
    }

    public void init(Context context) {
        loadApiKey(context);

        this.databaseDefault = FirebaseDatabase.getInstance().getReference();

        initBlocks(context);
//        initDatabases(context);
    }

    private void initDatabases(final Context context) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                initBlocks(context);
            }
        });
    }

    private void loadApiKey(Context context) {
        try (final JsonReader reader =
                     new JsonReader(new InputStreamReader(context.getResources().openRawResource(R.raw.api_key), StandardCharsets.UTF_8))) {

            reader.beginObject();

            while (reader.hasNext()) {
                final String name = reader.nextName();

                String pkgName = context.getPackageName();
                if (name.equals(pkgName)) {
                    extractKey(reader);
                } else {
                    reader.skipValue();
                }
            }

            reader.endObject();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse api_key.json");
        }
    }

    private void extractKey(JsonReader reader) {

        try {
            reader.beginObject();

            while (reader.hasNext()) {
                final String name = reader.nextName();

                if (name.equals("app_id")) {
                    this.APP_ID = reader.nextString();
                } else if (name.equals("api_key")) {
                    this.API_KEY = reader.nextString();
                }
            }

            reader.endObject();
        } catch (IOException e) {
            throw  new IllegalStateException("Unable to parse API key");
        }
    }

    private void initBlocks(Context context) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(APP_ID)
                .setApiKey(API_KEY)
                .setDatabaseUrl("https://" + FB_DB_NAME_BLOCKS + ".firebaseio.com/")
                .build();
        FirebaseApp databaseApp = FirebaseApp.initializeApp(context, options, FB_DB_NAME_BLOCKS);
        databaseBlocks = FirebaseDatabase.getInstance(databaseApp).getReference();
    }

    public DatabaseReference getDatabaseDefault() {
        return databaseDefault;
    }
    public DatabaseReference getDatabaseBlocks() {
        return databaseBlocks;
    }
}
