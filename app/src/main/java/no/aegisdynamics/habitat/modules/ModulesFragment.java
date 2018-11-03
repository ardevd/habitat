package no.aegisdynamics.habitat.modules;

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
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.ModulesAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.module.Module;
import no.aegisdynamics.habitat.dialogs.ModuleInstallDialog;
import no.aegisdynamics.habitat.itemListeners.ModuleItemListener;
import no.aegisdynamics.habitat.util.SnackbarHelper;
public class ModulesFragment extends Fragment implements ModulesContract.View,
        SearchView.OnQueryTextListener, ModuleInstallDialog.ModuleInstallDialogListener {

    private ModulesContract.UserActionsListener mActionsListener;
    private ModulesAdapter mListAdapter;

    public ModulesFragment() {
        // Required empty constructor
    }

    public static ModulesFragment newInstance() {
        return new ModulesFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadModules();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_modules, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_modules_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.modules_menu_search_hint));
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new ModulesPresenter(Injection.provideModulesRepository(getContext()), this);
        mListAdapter = new ModulesAdapter(new ArrayList<Module>(), mItemListener);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_modules, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.list_modules);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setHasFixedSize(true);
        int numColums = getResources().getInteger(R.integer.num_modules_columns);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColums));
        registerForContextMenu(recyclerView);
        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout =
                root.findViewById(R.id.refresh_layout_modules);
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
                mActionsListener.loadModules();
            }
        });
        return root;
    }

    @Override
    public void showModules(List<Module> modules) {
        mListAdapter.replaceData(modules);
    }

    @Override
    public void showModulesLoadError(String error) {
        if (isAdded()) {
            SnackbarHelper.showFlashbarErrorMessage(getString(R.string.modules_failed_to_load),
                    error, getActivity());
        }
    }

    @Override
    public void showModuleDeleted(Module module) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.modules_module_deleted,
                    module.getModuleName()), getView());
        }
    }

    @Override
    public void showModuleDeleteError(String error) {
        if (isAdded()) {
            SnackbarHelper.showFlashbarErrorMessage(getString(R.string.modules_error_deleting_module),
                    error, getActivity());
        }
    }

    @Override
    public void showModuleInstalled() {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.modules_install_success),
                    getView());
        }
    }

    @Override
    public void showModuleInstallError(String error) {

    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_modules);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    private final ModuleItemListener mItemListener = new ModuleItemListener() {
        @Override
        public void onModuleClicked(Module clickedModule) {
            // TODO: Handle module clicks. Show module detail UI
        }

        @Override
        public void onModuleDeleteClicked(Module clickedModule) {
            // TODO: Handle module delete clicks
        }
    };

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mListAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public void onModuleInstallUrlSubmitted(String url) {
        // Install module.
        mActionsListener.installModule(url);
    }
}
