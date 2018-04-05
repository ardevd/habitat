package no.aegisdynamics.habitat.data.automation;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Concrete implementation to load devices from Content provider
 */

public class ContentProviderAutomationProviderRepository implements AutomationsRepository, DeviceDataContract {

    private final AutomationsServiceApi mAutomationsServiceApi;
    private final Context mContext;

    public ContentProviderAutomationProviderRepository(@NonNull AutomationsServiceApi automationsServiceApi, @NonNull Context context) {
        mAutomationsServiceApi = automationsServiceApi;
        mContext = context;
    }

    @Override
    public void getAutomations(@NonNull final LoadAutomationsCallback callback) {
        mAutomationsServiceApi.getAllAutomations(mContext, new AutomationsServiceApi.AutomationsServiceCallback<List<Automation>>() {
            @Override
            public void onLoaded(List<Automation> automations) {
                callback.onAutomationsLoaded(automations);
            }

            @Override
            public void onError(String error) {
                callback.onAutomationsLoadError(error);
            }
        });
    }

    @Override
    public void getAutomation(int automationId, @NonNull final GetAutomationCallback callback) {
        mAutomationsServiceApi.getAutomation(mContext, automationId, new AutomationsServiceApi.AutomationsServiceCallback<Automation>() {
            @Override
            public void onLoaded(Automation automation) {
                callback.onAutomationLoaded(automation);
            }

            @Override
            public void onError(String error) {
                callback.onAutomationLoadError(error);
            }
        });

    }

    @Override
    public void createAutomation(@NonNull Automation mAutomation, @NonNull final CreateAutomationCallback callback) {
        mAutomationsServiceApi.createAutomation(mContext, mAutomation, new AutomationsServiceApi.AutomationsServiceCallback<Automation>() {

            @Override
            public void onLoaded(Automation automation) {
                callback.onAutomationCreated();
            }

            @Override
            public void onError(String error) {
                callback.onAutomationCreateError(error);

            }
        });
    }

    @Override
    public void deleteAutomation(int automationId, @NonNull final DeleteAutomationCallback callback) {
        mAutomationsServiceApi.removeAutomation(mContext, automationId, new AutomationsServiceApi.AutomationsServiceCallback<Boolean>() {

            @Override
            public void onLoaded(Boolean automations) {
                callback.onAutomationDeleted();
            }

            @Override
            public void onError(String error) {
                callback.onAutomationDeleteError(error);
            }
        });
    }
}
