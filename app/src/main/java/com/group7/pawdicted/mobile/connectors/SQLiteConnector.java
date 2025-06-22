package com.group7.pawdicted.mobile.connectors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SQLiteConnector extends SQLiteOpenHelper {

    private static final String DB_NAME = "order_tracking_full_updated.sqlite";
    private static final int DB_VERSION = 1;
    private static String DB_PATH = "";

    private final Context context;

    public SQLiteConnector(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            getReadableDatabase(); // Tạo file trống
            close();
            try {
                InputStream input = context.getAssets().open(DB_NAME);
                OutputStream output = new FileOutputStream(DB_PATH);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không cần tạo bảng vì database đã có sẵn trong assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Không xử lý nâng cấp
    }

    public SQLiteDatabase getDatabase() {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }
}
