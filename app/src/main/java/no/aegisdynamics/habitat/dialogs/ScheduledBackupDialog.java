package no.aegisdynamics.habitat.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import com.xw.repo.BubbleSeekBar;

import no.aegisdynamics.habitat.R;

/**
 * Dialog informing the user of the scheduled Z-way backup feature
 */

public class ScheduledBackupDialog extends DialogFragment {

    private BackupDialogListener callback;

    private static final int RETENTION_DAYS = 7;


    public interface BackupDialogListener {
        void onScheduledBackupsConfirmed(int retentionDays);
        void onScheduledBackupsCancelled();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (BackupDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement BackupDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scheduled_backups, null);
        final BubbleSeekBar retentionSeekBar = view.findViewById(R.id.dialog_backup_retention_progress);

        // set retention seekbar to previous retention value.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        int retentionDays = settings.getInt("backup_retention", RETENTION_DAYS);
        retentionSeekBar.setProgress(retentionDays);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                // set dialog icon
                .setIcon(R.drawable.ic_backup)
                // negative button
                .setNegativeButton(getString(R.string.backup_dialog_negative_button), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User cancelled. Do nothing.
                        callback.onScheduledBackupsCancelled();
                    }
                })
                // positive button
                .setPositiveButton(getString(R.string.backup_dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onScheduledBackupsConfirmed(retentionSeekBar.getProgress());
                    }
                }).create();
    }
}
