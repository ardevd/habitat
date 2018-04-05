package no.aegisdynamics.habitat.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Dialog informing the user about the Habitat app module.
 */

public class HabitatAppModuleInfoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        return new AlertDialog.Builder(getActivity())
                .setView(inflater.inflate(R.layout.dialog_habitatappmodule, null))
                // set dialog icon
                .setIcon(android.R.drawable.stat_notify_error)
                // positive button
                .setPositiveButton(getString(R.string.app_module_dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Show zway app url.
                        Intent intent= new Intent(Intent.ACTION_VIEW,
                                Uri.parse(ZWayNetworkHelper.getZwayAppSupportModule(getContext())));
                        startActivity(intent);
                    }
                })
                // negative button
                .setNegativeButton(getString(R.string.app_module_dialog_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
    }
}