package no.aegisdynamics.habitat.locationadd;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.util.SnackbarHelper;

public class LocationAddFragment extends Fragment implements LocationAddContract.View {

    private LocationAddContract.UserActionsListener mActionsListener;
    private EditText locationTitleEditText;

    public LocationAddFragment() {
        // Required empty constructor
    }

    public static LocationAddFragment newInstance() {
        return new LocationAddFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new LocationAddPresenter(Injection.provideLocationsRepository(getContext()), this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_locationadd, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_locationadd_save:
                Location mLocation = new Location(0, locationTitleEditText.getText().toString(),
                        null, 0);
                mActionsListener.saveLocation(mLocation);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locationadd, container, false);
        locationTitleEditText = root.findViewById(R.id.locationadd_title_edittext);
        return root;
    }
    
    @Override
    public void showEmptyLocationError() {

        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.locationadd_empty_location), getView());
    }

    @Override
    public void showLocationAdded() {
        getActivity().finish();
    }

    @Override
    public void locationAddError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }
}
