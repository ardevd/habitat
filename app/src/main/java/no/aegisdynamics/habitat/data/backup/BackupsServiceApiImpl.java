package no.aegisdynamics.habitat.data.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.util.InputStreamVolleyRequest;
import no.aegisdynamics.habitat.util.LogHelper;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Backup service API implementation.
 */

public class BackupsServiceApiImpl implements BackupsServiceApi {
    private static final String BACKUPS_DIR = "backups";
    private static final String BACKUPS_SCHEDULED_IDENTIFIER = "auto";
    private static final int RETENTION_DAYS = 7;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void getBackups(Context context, BackupsServiceCallback<List<Backup>> callback) {
        List<Backup> backups = new ArrayList<>();
        if (verifyBackupDirectory(context)) {
            File backupDirectory = new File(context.getFilesDir(), BACKUPS_DIR);
            File[] backupFiles = backupDirectory.listFiles();
            for (File backupFile : backupFiles) {
                Backup backup = new Backup(backupFile.getName(),
                        backupFile.length(), new Date(backupFile.lastModified()));
                backups.add(backup);
            }
        }

        callback.onLoaded(backups);
    }

    @Override
    public void createBackup(final Context context, final boolean manualBackup, final BackupsServiceCallback<Boolean> callback) {
        if (verifyBackupDirectory(context)) {
            String backup_url = ZWayNetworkHelper.getZwayBackupURL(context);
            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, backup_url,
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            try {
                                if (response != null) {

                                    CreateBackupFileTask task = new CreateBackupFileTask(context,
                                            manualBackup, callback);
                                    task.execute(response);
                                }
                            } catch (Exception e) {
                                callback.onError(e.getMessage());
                                LogHelper.logError(context, TAG, e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    LogHelper.logError(context, TAG, error.getMessage());
                    if (error instanceof AuthFailureError) {
                        callback.onError(context.getString(R.string.devices_authentication_error));
                    } else {
                        callback.onError(error.getMessage());
                    }
                }
            }, null) {
                @Override
                public Map<String, String> getHeaders() {
                    return ZWayNetworkHelper.getAuthenticationHeaders(context);
                }
            };

            // Define a retry policy with a 30 second timeout. It takes a while to create backups.
            RetryPolicy policy = new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);

            // Access the RequestQueue through the singleton class.
            RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(request);
        }
    }

    @Override
    public void deleteBackup(Context context, Backup backup, BackupsServiceCallback<Boolean> callback) {

        File backupFile = new File(String.format("%s/%s",
                context.getFilesDir(),
                BACKUPS_DIR), backup.getFilename());
        if (backupFile.delete()) {
            callback.onLoaded(true);
        } else {
            callback.onError("Error");
        }

    }

    private boolean verifyBackupDirectory(Context context) {
        File backupDirectory = new File(context.getFilesDir(), BACKUPS_DIR);
        return backupDirectory.exists() || backupDirectory.mkdir();
    }

    private static class CreateBackupFileTask extends AsyncTask<byte[], Void, String> {

        private final WeakReference<Context> mContext;
        private final BackupsServiceCallback<Boolean> mCallback;
        private final boolean isManualBackup;

        private CreateBackupFileTask(Context context, boolean manualBackup, BackupsServiceCallback<Boolean> callback) {
            mContext = new WeakReference<>(context);
            mCallback = callback;
            isManualBackup = manualBackup;
        }

        @Override
        protected String doInBackground(byte[]... responses) {

            String baseFileName = "zway.zab";
            if (!isManualBackup) {
                baseFileName = String.format("zway-%s.zab", BACKUPS_SCHEDULED_IDENTIFIER);
            }
            String name = String.format("%s-%s",
                    new SimpleDateFormat("yyyymmdd_HHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()),
                    baseFileName);
            try (FileOutputStream outputStream = new FileOutputStream(new File(String.format("%s/%s",
                    mContext.get().getFilesDir(),
                    BACKUPS_DIR), name))) {
                outputStream.write(responses[0]);
                outputStream.flush();
            } catch (IOException ex) {
                LogHelper.logError(mContext.get(), "Backup Service", ex.getMessage());
                return ex.getMessage();
            }

            // If we are doing a scheduled backup, delete backups that are older than
            // the retention period.
            if (!isManualBackup) {
                File backupDirectory = new File(mContext.get().getFilesDir(), BACKUPS_DIR);
                File[] backupFiles = backupDirectory.listFiles();
                for (File backupFile : backupFiles) {
                    if (backupFile.getName().contains(BACKUPS_SCHEDULED_IDENTIFIER)) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext.get());
                        int retentionDays = settings.getInt("backup_retention", RETENTION_DAYS);
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, -retentionDays);
                        if (new Date(backupFile.lastModified()).before(calendar.getTime())
                                && !backupFile.delete()) {
                            return mContext.get().getString(R.string.backup_delete_error);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String error) {
            super.onPostExecute(error);
            if (error != null) {
                mCallback.onError(error);
            } else {
                mCallback.onLoaded(true);
            }
        }
    }
}
