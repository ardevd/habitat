package no.aegisdynamics.habitat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.backup.Backup;
import no.aegisdynamics.habitat.itemListeners.BackupItemListener;
import no.aegisdynamics.habitat.util.FileSizeStringifier;

/**
 * ListView adapter for backups
 */

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.ViewHolder> {

    private List<Backup> mBackups;
    private BackupItemListener mItemListener;
    private static final String BACKUPS_SCHEDULED_IDENTIFIER = "auto";
    private static final int RETENTION_DAYS = 7;

    public BackupAdapter(List<Backup> backups, BackupItemListener itemListener) {
        setList(backups);
        mItemListener = itemListener;
    }

    private void setList(List<Backup> backups) {
        mBackups = backups;
    }

    @Override
    public BackupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View backupView = inflater.inflate(R.layout.item_backup, parent, false);
        return new BackupAdapter.ViewHolder(backupView, mItemListener);
    }

    @Override
    public void onBindViewHolder(BackupAdapter.ViewHolder holder, int position) {
        final Backup backup = mBackups.get(position);

        holder.filename.setText(backup.getFilename());
        holder.filesize.setText(FileSizeStringifier.convertBytesToScaledString(backup.getFileSize()));
        long now = System.currentTimeMillis();
        holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(backup.getTimestamp().getTime(),
                now, DateUtils.MINUTE_IN_MILLIS));

        // Show retention icon for scheduled backups
        if (backup.getFilename().contains(BACKUPS_SCHEDULED_IDENTIFIER)) {
            holder.retentionIcon.setVisibility(View.VISIBLE);
            // If retention is about to expire, set red tint.
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(holder.retentionIcon.getContext());
            int retentionDays = settings.getInt("backup_retention", RETENTION_DAYS);
            Calendar calendar  = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, +2);

            Calendar retentionCalendar = Calendar.getInstance();
            retentionCalendar.setTime(backup.getTimestamp());
            retentionCalendar.add(Calendar.DAY_OF_YEAR, retentionDays);

            if (calendar.getTime().after(retentionCalendar.getTime())) {
                holder.retentionIcon.getDrawable()
                        .setColorFilter(ContextCompat.getColor(holder.retentionIcon.getContext(), R.color.colorAccent),
                                PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.retentionIcon.getDrawable()
                        .setColorFilter(ContextCompat.getColor(holder.retentionIcon.getContext(), R.color.colorNavMenuItemColor),
                                PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            holder.retentionIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mBackups.size();
    }

    private Backup getItem(int position) {
        return mBackups.get(position);
    }

    public void replaceData(List<Backup> backups) {
        setList(backups);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener {

        private TextView filename;
        private TextView filesize;
        private TextView timestamp;
        private ImageView retentionIcon;

        private BackupItemListener mItemListener;

        public ViewHolder(View itemView, BackupItemListener itemListener) {
            super(itemView);

            // Views
            mItemListener = itemListener;
            filename = itemView.findViewById(R.id.item_backup_filename);
            filesize = itemView.findViewById(R.id.item_backup_filesize);
            timestamp = itemView.findViewById(R.id.item_backup_timestamp);
            retentionIcon = itemView.findViewById(R.id.item_backup_retention_icon);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Backup backup = getItem(position);
            mItemListener.onBackupClick(backup);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            Activity mActivity = (Activity) view.getContext();
            MenuInflater inflater = mActivity.getMenuInflater();
            inflater.inflate(R.menu.menu_backup_actions, contextMenu);

            /*
             * Workaround listener in order to pass context menu item selections to
             * our onCustomMenuItem click method so that we can handle
             * menu item selection in the ViewHolder with the ItemListener interface.
             */
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onCustomMenuItemClick(item);
                    return true;
                }
            };

            for (int i = 0, n = contextMenu.size(); i < n; i++)
                contextMenu.getItem(i).setOnMenuItemClickListener(listener);
        }

        public boolean onCustomMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            Backup backup = getItem(position);

            switch (menuItem.getItemId()) {
                case R.id.menu_backup_delete:
                    // Delete backup
                    mItemListener.onBackupDeleteClick(backup);
                    notifyItemRemoved(position);
                    return true;
                default:
                    return false;
            }
        }
    }
}
