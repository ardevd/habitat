package no.aegisdynamics.habitat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import no.aegisdynamics.habitat.util.DbHelper;

public class LogDataProvider extends ContentProvider implements DeviceDataContract {

    private DbHelper db;
    private final UriMatcher matcher = new UriMatcher(0);
    private static final int URI_ALL_LOGS = 1;
    private static final int URI_ONE_LOG = 2;

    public LogDataProvider() {
        matcher.addURI(AUTHORITY_LOGS, ENTITY_LOG + "/#", URI_ONE_LOG);
        matcher.addURI(AUTHORITY_LOGS, ENTITY_LOG, URI_ALL_LOGS);
    }
    @Override
    public boolean onCreate() {
        db = new DbHelper(getContext());
        return true;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortBy) {
        Cursor c;
        SQLiteDatabase database = db.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_LOG);
        switch (matcher.match(uri)){
            case URI_ONE_LOG:
                // Single item query is identical to all-items query so fall through here.
            case URI_ALL_LOGS:
                c = qb.query(database, projection, selection, selectionArgs, null, null, sortBy, null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        sendChangeNotification(uri);
        return c;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)){
            case URI_ALL_LOGS:
                return MULTIPLE_LOGS_MIME_TYPE;
            case URI_ONE_LOG:
                return SINGLE_LOG_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Uri result = null;
        SQLiteDatabase database = db.getWritableDatabase();
        switch (matcher.match(uri)) {
            case URI_ALL_LOGS:
                long logRowId = database.insert(TABLE_LOG, null, contentValues);
                if (logRowId > 0) {
                    result = ContentUris.withAppendedId(CONTENT_URI_LOGS, logRowId);
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (result != null) {
            sendChangeNotification(uri);
        }
        return result;
    }
    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int count;
        switch (matcher.match(uri)){
            case URI_ALL_LOGS:
                count = database.delete(TABLE_LOG, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (count > 0) {
            sendChangeNotification(uri);
        }
        return count;
    }
    private void sendChangeNotification(Uri uri) {
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int count;
        switch (matcher.match(uri)){
            case URI_ALL_LOGS:
                count = database.update(TABLE_LOG, contentValues, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (count > 0) {
            sendChangeNotification(uri);
        }
        return count;
    }
}