package no.aegisdynamics.habitat.automationadd;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

public class AutomationAddFragment extends Fragment implements AutomationAddContract.View, DeviceDataContract {

    private AutomationAddContract.UserActionsListener mActionsListener;
    private EditText timeEditText;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText commandsEditText;
    private Spinner deviceSpinner;
    private RadioButton rbRepeating;

    public AutomationAddFragment() {
        // Required empty constructor
    }

    public static AutomationAddFragment newInstance() {
        return new AutomationAddFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new AutomationAddPresenter(Injection.provideDevicesRepository(getContext()),
                Injection.provideAutomationsRepository(getContext()), this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_automationadd, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_automationadd_save:
                submitAutomation();

        }
        return super.onOptionsItemSelected(item);
    }

    private void submitAutomation() {
        Device selectedDevice = (Device) deviceSpinner.getSelectedItem();
        String automationType = AUTOMATION_TYPE_SINGLE;
        if (rbRepeating.isChecked()) {
            automationType = AUTOMATION_TYPE_RECURRING;
        }
        Automation mAutomation = new Automation(0,
                nameEditText.getText().toString(),
                descriptionEditText.getText().toString(),
                automationType,
                timeEditText.getText().toString(),
                commandsEditText.getText().toString(),
                selectedDevice.getId());
        mActionsListener.saveAutomation(mAutomation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_automationadd, container, false);
        deviceSpinner = root.findViewById(R.id.automationadd_device_spinner);
        nameEditText = root.findViewById(R.id.automationadd_name_edittext);
        commandsEditText = root.findViewById(R.id.automationadd_command_edittext);
        descriptionEditText = root.findViewById(R.id.automationadd_description_edittext);
        timeEditText = root.findViewById(R.id.automationadd_time_edittext);
        rbRepeating = root.findViewById(R.id.automationadd_repeating);
        mActionsListener.generateDefaultTime();

        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    /* Show time picker dialog and return result */
                    setTimeStringFromDialog();
                }
            }
        });
        return root;
    }

    private void setTimeStringFromDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadDevices();
    }

    @Override
    public void showEmptyAutomationError() {
        if (!isDetached() && getView() != null) {
            Snackbar.make(getView(), getString(R.string.automationadd_empty), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showInvalidAutomationTrigger() {
        if (!isDetached() && getView() != null) {
            Snackbar.make(getView(), getString(R.string.automationadd_invalid_trigger), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showAutomationAdded() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void showAutomationAddError(String error) {

    }


    @Override
    public void showDefaultTime(String timestring) {
        timeEditText.setText(timestring);
    }

    @Override
    public void showDevices(List<Device> devices) {
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.item_device_spinner, devices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(adapter);
    }

    @Override
    public void showDevicesLoadError(String error) {

    }
}
