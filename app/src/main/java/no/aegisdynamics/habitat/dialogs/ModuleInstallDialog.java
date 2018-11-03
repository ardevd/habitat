package no.aegisdynamics.habitat.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import no.aegisdynamics.habitat.R;

public class ModuleInstallDialog extends DialogFragment {

    private ModuleInstallDialogListener callback;

    public interface ModuleInstallDialogListener {
        void onModuleInstallUrlSubmitted(String url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (ModuleInstallDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement ModuleInstallDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_module_install, null);
        return new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                // positive button
                .setPositiveButton(getString(R.string.app_module_dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Trigger callback with Module URL string.
                        EditText moduleUrlEditText = dialogView.findViewById(R.id.dialog_install_url);
                        callback.onModuleInstallUrlSubmitted(moduleUrlEditText.getText().toString());
                    }
                })
                // negative button
                .setNegativeButton(getString(R.string.app_module_dialog_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }
}
