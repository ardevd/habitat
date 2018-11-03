package no.aegisdynamics.habitat.notifications;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.NotificationsAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.itemListeners.NotificationItemListener;
import no.aegisdynamics.habitat.util.SnackbarHelper;
import no.aegisdynamics.habitat.util.SwipeUtil;

public class NotificationsFragment extends Fragment implements NotificationsContract.View {

    private NotificationsAdapter mListAdapter;
    private NotificationsContract.UserActionsListener mActionsListener;


    public NotificationsFragment() {
        // Required empty constructor
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new NotificationsPresenter(Injection.provideNotificationsRepository(getContext()), this);
        mListAdapter = new NotificationsAdapter(new ArrayList<Notification>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.notifications_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_notifications_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        registerForContextMenu(recyclerView);
        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout =
                root.findViewById(R.id.refresh_layout_notifications);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadNotifications();
            }
        });

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(getSwipeToDeleteNotification());
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    private SwipeUtil getSwipeToDeleteNotification() {
        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Delete notification.
                int swipedItemPosition = viewHolder.getAdapterPosition();
                mActionsListener.deleteNotification(mListAdapter.getItem(swipedItemPosition));
                mListAdapter.notifyItemRemoved(swipedItemPosition);
            }
        };

        //set swipe background-Color
        swipeHelper.setLeftColorCode(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        return swipeHelper;
    }

    /**
     * Listener for clicks on notifications in the RecyclerView.
     */
    private final NotificationItemListener mItemListener = new NotificationItemListener() {

        @Override
        public void onNotificationClick(Notification clickedNotification) {
            // TODO: Implement something useful when clicking a notification.
        }

        @Override
        public void onNotificationDeleteClick(Notification clickedNotification) {
            // Delete notification.
            mActionsListener.deleteNotification(clickedNotification);
        }

        @Override
        public void onNotificationRedeemClick(Notification clickedNotification) {
            // Redeem notification
            mActionsListener.redeemNotification(clickedNotification);
        }
    };

    @Override
    public void showNotifications(List<Notification> notifications) {
        mListAdapter.replaceData(notifications);
    }

    @Override
    public void showNotificationsLoadError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_notifications);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showNotificationDeleted() {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.notification_deleted), getView());
        }
    }

    @Override
    public void showNotificationDeletedError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
            mActionsListener.loadNotifications();
        }
    }

    @Override
    public void showNotificationRedeemed() {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.notification_redeemed), getView());
        }
    }

    @Override
    public void showNotificationRedeemedError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
            mActionsListener.loadNotifications();
        }
    }
}
