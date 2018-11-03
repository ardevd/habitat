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


public class AutomationDataProvider extends ContentProvider implements DeviceDataContract {

    private DbHelper db;
    private final UriMatcher matcher = new UriMatcher(0);
    private static final int URI_ALL_AUTOMATIONS = 1;
    private static final int URI_ONE_AUTOMATION = 2;
    private static final String UNSUPPORTED_URI = "Unsupported URI: ";
    
    public AutomationDataProvider() {
        matcher.addURI(AUTHORITY_AUTOMATIONS, ENTITY_AUTOMATION + "/#", URI_ONE_AUTOMATION);
        matcher.addURI(AUTHORITY_AUTOMATIONS, ENTITY_AUTOMATION, URI_ALL_AUTOMATIONS);
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
        qb.setTables(TABLE_AUTOMATION);
        switch (matcher.match(uri)){
            case URI_ONE_AUTOMATION:
                // Single item query is identical to all-items query so fall through here.
            case URI_ALL_AUTOMATIONS:
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
            case URI_ALL_AUTOMATIONS:
                return MULTIPLE_AUTOMATIONS_MIME_TYPE;
            case URI_ONE_AUTOMATION:
                return SINGLE_AUTOMATION_MIME_TYPE;
            default:
                throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Uri result = null;
        SQLiteDatabase database = db.getWritableDatabase();
        switch (matcher.match(uri)) {
            case URI_ALL_AUTOMATIONS:
                long automationRowId = database.insert(TABLE_AUTOMATION, null, contentValues);
                if (automationRowId > 0) {
                    result = ContentUris.withAppendedId(CONTENT_URI_AUTOMATIONS, automationRowId);
                }
                break;

            default:
                throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
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
            case URI_ALL_AUTOMATIONS:
                count = database.delete(TABLE_AUTOMATION, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
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
            case URI_ALL_AUTOMATIONS:
                count = database.update(TABLE_AUTOMATION, contentValues, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }
        if (count > 0) {
            sendChangeNotification(uri);
        }
        return count;
    }
}
