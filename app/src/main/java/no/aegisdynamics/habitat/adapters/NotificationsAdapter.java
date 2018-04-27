package no.aegisdynamics.habitat.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.itemListeners.NotificationItemListener;

/**
 * Notifications adapter used for display notification items in a recycler view.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> mNotifications;
    private final NotificationItemListener mItemListener;

    public NotificationsAdapter(List<Notification> notifications, NotificationItemListener itemListener) {
        setList(notifications);
        mItemListener = itemListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(notificationView, mItemListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Notification notification = mNotifications.get(position);

        holder.deviceName.setText(notification.getDeviceName());
        holder.notificationMessage.setText(notification.getMessage());
        holder.notificationTimestamp.setText(DateUtils.getRelativeTimeSpanString(notification.getTimestamp().getTime()).toString());

        if (notification.isRedeemed()) {
            holder.colorBarLayout.setBackgroundColor(ContextCompat.getColor(
                    holder.colorBarLayout.getContext(), R.color.colorPrimary));
        } else {
            holder.colorBarLayout.setBackgroundColor(ContextCompat.getColor(
                    holder.colorBarLayout.getContext(), R.color.colorAccent));
        }
    }

    public void replaceData(List<Notification> notifications) {
        setList(notifications);
        notifyDataSetChanged();
    }

    private void setList(List<Notification> notifications) {
        mNotifications = notifications;
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public Notification getItem(int position) {
        return mNotifications.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        final TextView deviceName;

        final TextView notificationMessage;

        final TextView notificationTimestamp;

        final RelativeLayout colorBarLayout;

        private final NotificationItemListener mItemListener;

        ViewHolder(View itemView, NotificationItemListener itemListener) {
            super(itemView);
            mItemListener = itemListener;
            deviceName = itemView.findViewById(R.id.item_notification_device);
            notificationTimestamp = itemView.findViewById(R.id.item_notification_timestamp);
            notificationMessage = itemView.findViewById(R.id.item_notification_message);
            colorBarLayout = itemView.findViewById(R.id.item_notification_colorbar);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Notification notification = getItem(position);
            mItemListener.onNotificationClick(notification);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(view.getContext().getString(R.string.notification_action_menu_title));
            Activity mActivity = (Activity) view.getContext();
            MenuInflater inflater = mActivity.getMenuInflater();
            inflater.inflate(R.menu.menu_notification_actions, menu);

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

            for (int i = 0, n = menu.size(); i < n; i++)
                menu.getItem(i).setOnMenuItemClickListener(listener);
        }

        boolean onCustomMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            Notification notification = getItem(position);

            switch (menuItem.getItemId()) {
                case R.id.menu_notification_delete:
                    // Delete notification
                    mItemListener.onNotificationDeleteClick(notification);
                    notifyItemRemoved(position);
                    return true;
                case R.id.menu_notification_redeem:
                    // Redeem notification
                    mItemListener.onNotificationRedeemClick(notification);
                    notification.setRedeemed(true);
                    notifyDataSetChanged();
                    return true;
                default:
                    return false;
            }
        }
    }
}
