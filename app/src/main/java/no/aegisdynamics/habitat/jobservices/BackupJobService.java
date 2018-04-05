package no.aegisdynamics.habitat.jobservices;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.backup.BackupsRepository;
import no.aegisdynamics.habitat.util.BackupNotifications;

/**
 * JobService responsible for requesting and downloading periodic Z-way backups.
 */

public class BackupJobService extends JobService {

    /**
     * Kick off the Z-way backup routine. Return true here
     * since we need a bit of time to complete
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        // Download backup.
        BackupsRepository mBackupsRepository = Injection.providerBackupRepository(getApplicationContext());
        mBackupsRepository.createBackup(false, new BackupsRepository.CreateBackupCallback() {

            @Override
            public void onBackupCreated() {
                // Show notification unless disabled in settings
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (settings.getBoolean("scheduled_backup_notifications", true)) {
                    BackupNotifications bn = new BackupNotifications(getApplicationContext());
                    bn.showNotification(getString(R.string.backup_notification_title),
                            getString(R.string.backup_notification_text));
                }
                // Finish job
                jobFinished(jobParameters, false);
            }

            @Override
            public void onBackupCreatedError(String error) {
                jobFinished(jobParameters, true);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // Job was cancelled as job conditions were no longer met.
        // Return true so that the system reschedules the job.
        return true;
    }

}
