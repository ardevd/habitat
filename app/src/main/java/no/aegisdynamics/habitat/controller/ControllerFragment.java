package no.aegisdynamics.habitat.controller;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.data.Injection;

public class ControllerFragment extends Fragment implements ControllerContract.View {

    private TextView textViewControllerStatus;
    private TextView textViewControllerSubtitle;
    private ControllerContract.UserActionsListener mActionsListener;
    private CircularProgressButton mProgressButton;
    private SharedPreferences settings;


    public ControllerFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new ControllerPresenter(Injection.provideDevicesRepository(getContext()), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_controller, container, false);
        textViewControllerSubtitle = root.findViewById(R.id.controller_subtitle);
        textViewControllerStatus = root.findViewById(R.id.controller_status);
        mProgressButton = root.findViewById(R.id.controller_button_restart);
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());

        mProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRestartControllerDialog();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewControllerSubtitle.setText(settings.getString("zway_hostname", "10.10.10.100:8083"));
        mActionsListener.getControllerStatus();
    }

    public static ControllerFragment newInstance() {
        return new ControllerFragment();
    }


    @Override
    public void showControllerWillRestartMessage() {

    }

    @Override
    public void showControllerHasRestarted() {

    }

    @Override
    public void showControllerRestartError(String error) {

    }

    @Override
    public void showControllerStatusUp() {
        if (isAdded()) {
            textViewControllerStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
            textViewControllerStatus.setText(getString(R.string.controller_online));
        }
    }

    @Override
    public void showControllerStatusError(String error) {
        if (isAdded()) {
            textViewControllerStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            textViewControllerStatus.setText(error);
        }
    }

    @Override
    public void showRestartControllerDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.devices_restart_controller_dialog_title));
        alertDialog.setMessage(getString(R.string.controller_restart_description));
        /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActionsListener.restartController();
                dialog.dismiss();
            }
        });

        /* When negative (No/cancel) button is clicked*/
        alertDialog.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (isAdded()) {
            if (active) {
                mProgressButton.startAnimation();
            } else {
                mProgressButton.doneLoadingAnimation(ContextCompat.getColor(getActivity(), R.color.colorAccent),
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
                // Revert button animation after three seconds.
                final Runnable r = new Runnable() {
                    public void run() {
                        mProgressButton.revertAnimation();
                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(r, 3000);
            }
        }

    }
}