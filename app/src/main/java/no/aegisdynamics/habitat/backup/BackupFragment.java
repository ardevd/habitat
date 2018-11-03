package no.aegisdynamics.habitat.backup;

import android.animation.ObjectAnimator;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.BackupAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.backup.Backup;
import no.aegisdynamics.habitat.dialogs.ScheduledBackupDialog;
import no.aegisdynamics.habitat.itemListeners.BackupItemListener;
import no.aegisdynamics.habitat.jobservices.BackupJobService;
import no.aegisdynamics.habitat.util.SnackbarHelper;

/**
 * Fragment for the Backup screen
 */

public class BackupFragment extends Fragment implements BackupContract.View,
        ScheduledBackupDialog.BackupDialogListener {

    private static final int BACKUP_JOB_ID = 1;
    private static final int DIALOG_BACKUP_CONFIRM = 1;
    private BackupContract.UserActionListener mUserActionsListener;
    private BackupAdapter mListAdapter;

    public BackupFragment() {
        // Required empty constructor
    }

    public static BackupFragment newInstance() {
        return new BackupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the BackupPresenter
        mUserActionsListener = new BackupPresenter(Injection.providerBackupRepository(getContext().getApplicationContext()),
                this);
        // Prepare the list adapter
        mListAdapter = new BackupAdapter(new ArrayList<Backup>(), mItemListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Load backups on resume.
        mUserActionsListener.loadBackups();
        mUserActionsListener.getAvailableStorage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_backup, container, false);

        // RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.backups_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getResources().getInteger(R.integer.num_backups_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        registerForContextMenu(recyclerView);

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout_backups);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserActionsListener.loadBackups();
            }
        });

        // Set up fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_new_backup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserActionsListener.createBackup();
            }
        });

        // Periodic backup switch
        SwitchCompat scheduledBackupSwitch = root.findViewById(R.id.backups_scheduled_switch);
        scheduledBackupSwitch.setChecked(checkForScheduledPeriodicBackups());
        scheduledBackupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Show the info dialog
                    ScheduledBackupDialog dialog = new ScheduledBackupDialog();
                    FragmentManager manager = getFragmentManager();
                    dialog.setTargetFragment(BackupFragment.this, DIALOG_BACKUP_CONFIRM);
                    dialog.show(manager, "habitat_scheduled_backup_dialog");
                } else {
                    cancelScheduledPeriodicBackups();
                }
            }
        });
        return root;
    }

    @Override
    public void showBackupStarted() {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.backup_started), getView());
        }
    }

    @Override
    public void showBackupCreated() {
        if(isAdded()) {
            mUserActionsListener.loadBackups();
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.backup_created), getView());
            mUserActionsListener.getAvailableStorage();
        }
    }

    @Override
    public void showBackupCreatedError(String error) {
        if(isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }

    @Override
    public void showBackupsLoaded(List<Backup> backups) {
        if(isAdded()) {
            mListAdapter.replaceData(backups);
            View view = getView();
            if (view != null) {
                RelativeLayout emptyBackupLayout = view.findViewById(R.id.backups_empty_layout);
                if (!backups.isEmpty()) {
                    emptyBackupLayout.setVisibility(View.GONE);
                } else {
                    emptyBackupLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void showBackupDeleted() {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.backup_deleted), getView());
            mUserActionsListener.loadBackups();
            mUserActionsListener.getAvailableStorage();
        }
    }

    @Override
    public void showBackupDeleteError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_backups);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showAvailableStorage(long free, long total) {
        if (isAdded()) {
            View view = getView();
            if (view != null) {

                float freePercentage = (float) free / total;

                // Show percentage text
                TextView storagePercentage = view.findViewById(R.id.backup_available_storage_percentage);
                Locale loc = Locale.getDefault();
                storagePercentage.setText(String.format(loc, "%d%%", (int)(freePercentage * 100)));

                // Set progressBar value.
                ProgressBar storageProgressBar = view.findViewById(R.id.backup_storage_progress_bar);

                // Animate progressBar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(storageProgressBar, "progress", 0, (int) (freePercentage * 100));
                progressAnimator.setDuration(400);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();
            }
        }
    }

    @Override
    public void schedulePeriodicBackups() {
        JobScheduler jobScheduler =
                (JobScheduler) getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(new JobInfo.Builder(BACKUP_JOB_ID,
                    new ComponentName(getActivity(), BackupJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .setRequiresCharging(true)
                    .setRequiresDeviceIdle(true)
                    .setPeriodic(TimeUnit.DAYS.toMillis(1))
                    .build());
        }
    }

    @Override
    public void cancelScheduledPeriodicBackups() {
        JobScheduler jobScheduler =
                (JobScheduler) getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancel(BACKUP_JOB_ID);
        }
    }

    @Override
    public boolean checkForScheduledPeriodicBackups() {
        JobScheduler jobScheduler =
                (JobScheduler) getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for ( JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == BACKUP_JOB_ID) {
                return true;
            }
        }

        return false;
    }

    /**
     * Listener for clicks on backup items in the RecyclerView.
     */
    private final BackupItemListener mItemListener = new BackupItemListener() {
        @Override
        public void onBackupClick(Backup clickedBackup) {
            File backupPath = new File(getContext().getFilesDir(), "backups");
            File backupFile = new File(backupPath, clickedBackup.getFilename());
            if (backupFile.exists()) {
                Uri contentUri = FileProvider.getUriForFile(getContext().getApplicationContext(), "no.aegisdynamics.habitat.fileprovider", backupFile);

                // create new Intent
                Intent intent = new Intent(Intent.ACTION_SEND);
                // set flag to give temporary permission to external app to use your FileProvider
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                intent.setDataAndType(
                        contentUri,
                        getContext().getContentResolver().getType(contentUri));
                // validate that the device can open your File!
                PackageManager pm = getActivity().getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    startActivity(intent);
                } else {
                    SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.backup_intent_no_supported_apps), getView());
                }
            } else {
                SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.backup_intent_file_does_not_exist), getView());
            }
        }

        @Override
        public void onBackupDeleteClick(Backup clickedBackup) {
            mUserActionsListener.deleteBackup(clickedBackup);
        }
    };


    @Override
    public void onScheduledBackupsConfirmed(int retentionTime) {
        // Save retention time
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("backup_retention", retentionTime).apply();

        // Schedule periodic backups
        schedulePeriodicBackups();
    }

    @Override
    public void onScheduledBackupsCancelled() {
        // User cancelled the backup dialog.
        SwitchCompat scheduledBackupSwitch = getView().findViewById(R.id.backups_scheduled_switch);
        scheduledBackupSwitch.setChecked(false);
    }
}
