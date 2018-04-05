package no.aegisdynamics.habitat.automations;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.AutomationsAdapter;
import no.aegisdynamics.habitat.automationadd.AutomationAddActivity;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.itemListeners.AutomationItemListener;

public class AutomationsFragment extends Fragment implements AutomationsContract.View {

    private AutomationsAdapter mListAdapter;
    private AutomationsContract.UserActionsListener mActionsListener;

    public AutomationsFragment() {
        // Required empty constructor
    }
    
    public static AutomationsFragment newInstance() {
        return new AutomationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new AutomationsPresenter(Injection.provideAutomationsRepository(getContext()), this);
        mListAdapter = new AutomationsAdapter(new ArrayList<Automation>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadAutomations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_automation, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.automations_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_automations_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        registerForContextMenu(recyclerView);
        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout_automations);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadAutomations();
            }
        });

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_new_automation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mActionsListener.addAutomation();
            }
        });
        return root;
    }

    /**
     * Listener for clicks on automations in the RecyclerView.
     */
    private AutomationItemListener mItemListener = new AutomationItemListener() {

        @Override
        public void onAutomationClick(Automation clickedAutomation) {
            // TODO: Implement something useful when clicking a automation.
        }

        @Override
        public void onAutomationDeleteClick(Automation clickedAutomation) {
            // Delete automation.
            mActionsListener.deleteAutomation(clickedAutomation);
        }

    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void showAutomations(List<Automation> automations) {
        mListAdapter.replaceData(automations);
        RelativeLayout emptyAutomationsLayout = getView().findViewById(R.id.automations_empty_layout);
        if (automations.size() > 0) {
            emptyAutomationsLayout.setVisibility(View.GONE);
        } else {
            emptyAutomationsLayout.setVisibility(View.VISIBLE);
            if (getActivity() instanceof AutomationsContract.Activity) {
                ((AutomationsContract.Activity) getActivity()).showFabHint();
            }
        }

    }

    @Override
    public void showAutomationsLoadError(String error) {

    }

    @Override
    public void showDeleteAutomation() {
        if (!isDetached()) {
            Snackbar.make(getView(), getString(R.string.automation_removed), Snackbar.LENGTH_SHORT).show();
            mActionsListener.loadAutomations();
        }
    }

    @Override
    public void showDeleteAutomationError(String error) {
        if (!isDetached()) {
            Snackbar.make(getView(), getString(R.string.automation_not_removed), Snackbar.LENGTH_SHORT).show();
            mActionsListener.loadAutomations();
        }
    }

    @Override
    public void showAddAutomation() {
        Intent intent = new Intent(getContext(), AutomationAddActivity.class);
        startActivity(intent);
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_automations);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showAutomationDetailUI(int automationId) {

    }
}
