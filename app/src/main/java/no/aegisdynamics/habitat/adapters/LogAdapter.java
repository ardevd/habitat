package no.aegisdynamics.habitat.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.log.HabitatLog;
import no.aegisdynamics.habitat.data.log.LogConstants;
import no.aegisdynamics.habitat.itemListeners.LogItemListener;

/**
 * HabitatLogs adapter used for display log items in a recycler view.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private List<HabitatLog> mHabitatLogs;
    private final LogItemListener mItemListener;

    public LogAdapter(List<HabitatLog> notifications, LogItemListener itemListener) {
        setList(notifications);
        mItemListener = itemListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.item_log, parent, false);
        return new ViewHolder(notificationView, mItemListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HabitatLog log = mHabitatLogs.get(position);

        holder.logTag.setText(log.getTag());
        holder.logMessage.setText(log.getMessage());
        holder.logTimestamp.setText(log.getTimestamp().toString());

        if (log.getType() == LogConstants.LOG_TYPE_ERROR) {
            holder.colorBarLayout.setBackgroundColor(ContextCompat.getColor(
                    holder.colorBarLayout.getContext(), R.color.colorAccent));
        } else if (log.getType() == LogConstants.LOG_TYPE_DEBUG){
            holder.colorBarLayout.setBackgroundColor(ContextCompat.getColor(
                    holder.colorBarLayout.getContext(), R.color.colorYellow));
        } else {
            holder.colorBarLayout.setBackgroundColor(ContextCompat.getColor(
                    holder.colorBarLayout.getContext(), R.color.colorPrimary));
        }
    }

    public void replaceData(List<HabitatLog> logs) {
        setList(logs);
        notifyDataSetChanged();
    }

    private void setList(List<HabitatLog> logs) {
        mHabitatLogs = logs;
    }

    @Override
    public int getItemCount() {
        return mHabitatLogs.size();
    }

    private HabitatLog getItem(int position) {
        return mHabitatLogs.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        final TextView logMessage;

        final TextView logTag;

        final TextView logTimestamp;

        final RelativeLayout colorBarLayout;

        private final LogItemListener mItemListener;

        ViewHolder(View itemView, LogItemListener itemListener) {
            super(itemView);
            mItemListener = itemListener;
            logMessage = itemView.findViewById(R.id.item_log_message);
            logTimestamp = itemView.findViewById(R.id.item_log_timestamp);
            logTag = itemView.findViewById(R.id.item_log_tag);
            colorBarLayout = itemView.findViewById(R.id.item_log_colorbar);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            HabitatLog logItem = getItem(position);
            mItemListener.onLogItemClicked(logItem);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(view.getContext().getString(R.string.logs_item_menu_title));
            Activity mActivity = (Activity) view.getContext();
            MenuInflater inflater = mActivity.getMenuInflater();
            inflater.inflate(R.menu.menu_log_actions, menu);

            /*
             * Workaround listener in order to pass context menu item selections to
             * our onCustomMenuItem click method so that we can handle
             * menu item selection in the ViewHolder with the ItemListener interface.
             */
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onCustomMenuItemClick(item);
                }
            };

            for (int i = 0, n = menu.size(); i < n; i++)
                menu.getItem(i).setOnMenuItemClickListener(listener);
        }

        boolean onCustomMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            HabitatLog logItem = getItem(position);

            switch (menuItem.getItemId()) {
                case R.id.menu_log_item_delete:
                    // Delete log entry
                    mItemListener.onLogDeleteClicked(logItem);
                    notifyItemRemoved(position);
                    return true;
                default:
                    return false;
            }
        }
    }
}
