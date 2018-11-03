package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * SQLiteOpenHelper extended helper class for database management
 */

public class DbHelper extends SQLiteOpenHelper implements DeviceDataContract {
    private static final String DB_NAME = "habitat.db";
    private static final int DB_VERSION = 4;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create tables
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_AUTOMATION + "("
                + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_AUTOMATION_NAME + " TEXT, "
                + FIELD_AUTOMATION_DESCRIPTION + " TEXT, "
                + FIELD_AUTOMATION_TYPE + " TEXT, "
                + FIELD_AUTOMATION_TRIGGER + " TEXT, "
                + FIELD_AUTOMATION_DEVICE + " TEXT, "
                + FIELD_AUTOMATION_COMMANDS + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOG + "("
                + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_LOG_TAG + " TEXT, "
                + FIELD_LOG_MESSAGE + " TEXT, "
                + FIELD_LOG_TYPE + " INTEGER, "
                + FIELD_LOG_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int previousVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        onCreate(sqLiteDatabase);
    }
}