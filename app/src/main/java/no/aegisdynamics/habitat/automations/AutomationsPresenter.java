package no.aegisdynamics.habitat.automations;


import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Listens to user actions from the UI ({@link AutomationsFragment}), retrieves the data and updates the
 * UI as required.
 */
public class AutomationsPresenter implements AutomationsContract.UserActionsListener, DeviceDataContract {

    private final AutomationsRepository mAutomationsRepository;
    private final AutomationsContract.View mAutomationsView;

    public AutomationsPresenter(@NonNull AutomationsRepository automationsRepository, @NonNull AutomationsContract.View automationsView) {
        mAutomationsRepository = automationsRepository;
        mAutomationsView = automationsView;
    }

    /**
     * Get list of automations
     */
    @Override
    public void loadAutomations() {
        mAutomationsView.setProgressIndicator(true);
        mAutomationsRepository.getAutomations(new AutomationsRepository.LoadAutomationsCallback() {

            @Override
            public void onAutomationsLoaded(List<Automation> automations) {
                mAutomationsView.showAutomations(automations);
                mAutomationsView.setProgressIndicator(false);
            }

            @Override
            public void onAutomationsLoadError(String error) {
                mAutomationsView.showAutomationsLoadError(error);
                mAutomationsView.setProgressIndicator(false);
            }
        });
    }

    @Override
    public void deleteAutomation(Automation automation) {
        mAutomationsView.setProgressIndicator(true);
        mAutomationsRepository.deleteAutomation(automation.getId(), new AutomationsRepository.DeleteAutomationCallback(){

            @Override
            public void onAutomationDeleted() {
                mAutomationsView.setProgressIndicator(false);
                mAutomationsView.showDeleteAutomation();
            }

            @Override
            public void onAutomationDeleteError(String error) {
                mAutomationsView.setProgressIndicator(false);
                mAutomationsView.showDeleteAutomationError(error);
            }
        });
    }

    @Override
    public void addAutomation() {
        mAutomationsView.showAddAutomation();
    }

    @Override
    public void openAutomationDetails(@NonNull Automation requestedAutomation) {
        mAutomationsView.showAutomationDetailUI(requestedAutomation.getId());
    }
}
