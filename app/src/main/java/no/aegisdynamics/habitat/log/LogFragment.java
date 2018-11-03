package no.aegisdynamics.habitat.log;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.LogAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.log.HabitatLog;
import no.aegisdynamics.habitat.itemListeners.LogItemListener;
import no.aegisdynamics.habitat.util.SnackbarHelper;

public class LogFragment extends Fragment implements LogContract.View {

    private LogAdapter mListAdapter;
    private LogContract.UserActionListener mActionsListener;

    public LogFragment() {
        // Required empty constructor
    }

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_logs, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.logs_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_notifications_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        registerForContextMenu(recyclerView);
        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout =
                root.findViewById(R.id.refresh_layout_logs);
        Activity thisActivity = getActivity();
        if (thisActivity != null) {
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(getActivity(), R.color.colorAccent),
                    ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadLogs();
            }
        });

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new LogPresenter(Injection.provideLogRepository(getContext()),
                this);
        mListAdapter = new LogAdapter(new ArrayList<HabitatLog>(), mItemListener);

        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadLogs();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_log_delete:
                mActionsListener.deleteAllLogs();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLogs(List<HabitatLog> logs) {
        mListAdapter.replaceData(logs);
    }

    @Override
    public void showLogsLoadError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_logs);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showLogDeleted() {
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.logs_log_deleted), getView());
        mActionsListener.loadLogs();
    }

    @Override
    public void showLogDeleteError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }

    @Override
    public void showLogsDeleted(int count) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.logs_logs_deleted, count), getView());
            mActionsListener.loadLogs();
        }
    }

    @Override
    public void showLogsDeletedError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }

    /**
     * Listener for log entry clicks in the RecyclerView
     */
    private final LogItemListener mItemListener = new LogItemListener() {

        @Override
        public void onLogItemClicked(HabitatLog clickedLogEntry) {

        }

        @Override
        public void onLogDeleteClicked(HabitatLog clickedLogEntry) {
            mActionsListener.deleteLog(clickedLogEntry);
        }
    };
}
