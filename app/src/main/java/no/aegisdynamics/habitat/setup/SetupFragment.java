package no.aegisdynamics.habitat.setup;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Charsets;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.dashboard.DashboardActivity;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.util.HostnameHelper;
import no.aegisdynamics.habitat.util.KeyStoreHelper;
import no.aegisdynamics.habitat.util.SnackbarHelper;

import static no.aegisdynamics.habitat.util.PrefsConstants.PREF_ZWAY_ANONYMOUS;
import static no.aegisdynamics.habitat.util.PrefsConstants.PREF_ZWAY_ANONYMOUS_DEFAULT;

public class SetupFragment extends Fragment implements SetupContract.View {

    private SetupContract.UserActionsListener mActionsListener;

    private EditText etZwayHostname;
    private EditText etZwayUsername;
    private EditText etZwayPassword;
    private TextView tvSubtitle;
    private LinearLayout cardContainerLayout;
    private Button buttonRestartSetup;

    private View inflatedCardViewHostname;
    private View inflatedCardViewCredentials;
    private View inflatedCardViewCustomName;
    private View inflatedProgressView;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private CheckBox sslCheckbox;

    public SetupFragment() {
        // Required empty constructor
    }

    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = settings.edit();

        mActionsListener = new SetupPresenter(Injection.provideDevicesRepository(getContext()), this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showHostnameView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup, container, false);
        tvSubtitle = root.findViewById(R.id.setup_subtitle);

        cardContainerLayout = root.findViewById(R.id.setup_card_container_layout);
        LayoutInflater inflaterCardViews = LayoutInflater.from(getActivity());
        inflatedCardViewHostname = inflaterCardViews.inflate(R.layout.setup_hostname,
                container, false);
        inflatedCardViewCredentials = inflaterCardViews.inflate(R.layout.setup_credentials,
                container, false);
        inflatedCardViewCustomName = inflaterCardViews.inflate(R.layout.setup_custom_name,
                container, false);
        inflatedProgressView = inflaterCardViews.inflate(R.layout.setup_progress,
                container, false);

        buttonRestartSetup = root.findViewById(R.id.setup_restart_button);
        buttonRestartSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to the first view.
                showHostnameView();
            }
        });

        return root;
    }

    @Override
    public void showValidSetup() {
        // Parameters are valid.
        editor.putBoolean("first_run", false);
        editor.apply();

        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showInvalidSetup(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        showCustomNameView();
    }

    @Override
    public void showEmptyCustomName() {
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.setup_empty_name), getView());
    }

    @Override
    public void setProgressIndicator(boolean active) {
        cardContainerLayout.removeAllViews();
        if (active) {
            cardContainerLayout.addView(inflatedProgressView);
            ProgressBar progressBar = getView().findViewById(R.id.setup_progress);
            progressBar.setIndeterminate(active);
        }
    }

    @Override
    public void showEmptyHostname() {
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.setup_empty_hostname), getView());
    }

    @Override
    public void showInvalidHostname(String error) {
        if (error.equals(getString(R.string.devices_authentication_error))) {
            // Credentials required
            showHostnameValidCredentialsRequired();
        } else {
            showHostnameView();
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }

    @Override
    public void showHostnameValidCredentialsRequired() {
        showCredentialsView();
    }

    @Override
    public void showHostnameAnonymousAccessApproved() {
        if (getView() != null) {
            if (settings.getBoolean(PREF_ZWAY_ANONYMOUS, PREF_ZWAY_ANONYMOUS_DEFAULT)) {
                showCustomNameView();
            } else {
                showCredentialsView();
            }
        }
    }

    private void showHostnameView() {
        cardContainerLayout.removeAllViews();
        buttonRestartSetup.setVisibility(View.INVISIBLE);
        cardContainerLayout.addView(inflatedCardViewHostname);
        tvSubtitle.setText(getString(R.string.setup_welcome_subtitle));
        etZwayHostname = getView().findViewById(R.id.setup_hostname);
        Button buttonHostnameSubmit = getView().findViewById(R.id.setup_hostname_submit_button);
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade);
        CardView hostnameCardView = getView().findViewById(R.id.setup_cardview_hostname);
        hostnameCardView.startAnimation(animation);
        buttonHostnameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitHostname();
            }
        });

        etZwayHostname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    submitHostname();
                    handled = true;
                }
                return handled;
            }
        });

        sslCheckbox = getView().findViewById(R.id.setup_use_https);

        sslCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    etZwayHostname.setText(HostnameHelper
                            .updateHostnameWhenSSLIsEnabled(etZwayHostname.getText().toString()));
                } else {
                    etZwayHostname.setText(HostnameHelper
                            .updateHostnameWhenSSLIsDisabled(etZwayHostname.getText().toString()));
                }
            }
        });

    }

    private void submitHostname() {
        // Store hostname (strip whitespaces) and attempt connection with anonymous credentials
        String hostname = etZwayHostname.getText().toString().replaceAll("\\s+","");
        // Use port 8083 by default unless a port is specifically specified.
        if (!hostname.contains(":")) {
            // TODO: Use regex for this...
            hostname += ":8083";
        }

        // Save parameters to SharedPrefs first
        editor.putString("zway_hostname", hostname);
        // Determine SSL access
        editor.putBoolean("zway_ssl", sslCheckbox.isChecked());

        // Determine anonymous access
        CheckBox anonymousAccessCheckbox = getView().findViewById(R.id.setup_disable_anonymous_access);
        editor.putBoolean(PREF_ZWAY_ANONYMOUS, anonymousAccessCheckbox.isChecked());

        editor.apply();
        mActionsListener.verifyHostname(hostname);
    }

    private void showCredentialsView() {
        cardContainerLayout.removeAllViews();
        buttonRestartSetup.setVisibility(View.VISIBLE);
        cardContainerLayout.addView(inflatedCardViewCredentials);
        tvSubtitle.setText(getString(R.string.setup_credentials_required));
        etZwayUsername = getView().findViewById(R.id.setup_username);
        etZwayPassword = getView().findViewById(R.id.setup_password);
        Button buttonCredentialsSubmit = getView().findViewById(R.id.setup_credentials_submit_button);
        buttonCredentialsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCredentials();
            }
        });

        etZwayPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    submitCredentials();
                    handled = true;
                }
                return handled;

            }
        });
    }

    private void submitCredentials() {
        // Store credentials and attempt to connect.
        String username = etZwayUsername.getText().toString();
        String password = etZwayPassword.getText().toString();

        // TEST - Use keystore to store password
        KeyStoreHelper ksh = new KeyStoreHelper();
        try {
            // Encrypt username and password
            String encryptedUsername = new String(ksh.encryptStringWithKeyStoreKey(username),
                    Charsets.ISO_8859_1);
            String encryptionIVUsername = new String(ksh.getIv(), Charsets.ISO_8859_1);
            String encryptedPassword = new String(ksh.encryptStringWithKeyStoreKey(password),
                    Charsets.ISO_8859_1);
            String encryptionIVPassword = new String(ksh.getIv(), Charsets.ISO_8859_1);

            // Store encrypted values in shared preferences.
            editor.putString("zway_username", encryptedUsername);
            editor.putString("zway_password", encryptedPassword);
            editor.putString("zway_username_iv", encryptionIVUsername);
            editor.putString("zway_password_iv", encryptionIVPassword);
        } catch (Exception e) {
            // Encryption failed
            SnackbarHelper
                    .showSimpleSnackbarMessage(getString(R.string.setup_credentials_encryption_failed), getView());
        }

        // Save parameters to SharedPrefs first
        editor.putBoolean(PREF_ZWAY_ANONYMOUS, false);
        editor.apply();
        mActionsListener.verifyCredentials();
    }

    private void showCustomNameView() {
        cardContainerLayout.removeAllViews();
        cardContainerLayout.addView(inflatedCardViewCustomName);
        buttonRestartSetup.setVisibility(View.VISIBLE);
        tvSubtitle.setText(getString(R.string.setup_custom_name));
        final EditText etZwayCustomName = getView().findViewById(R.id.setup_custom_name_text);
        Button buttonCustomNameSubmit = getView().findViewById(R.id.setup_custom_name_submit_button);
        buttonCustomNameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCustomName(etZwayCustomName.getText().toString());
            }
        });

        etZwayCustomName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitCustomName(etZwayCustomName.getText().toString());
                    handled = true;
                }
                return handled;

            }
        });

    }

    private void submitCustomName(String name) {
        // Save parameters to SharedPrefs
        editor.putString("zway_name", name);
        editor.apply();
        mActionsListener.saveSetupData(name);
    }

    @Override
    public void showInvalidCredentials(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        showCredentialsView();
    }

    @Override
    public void showValidCredentials() {
        showCustomNameView();
    }


}
