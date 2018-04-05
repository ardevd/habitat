package no.aegisdynamics.habitat.automations;


import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class AutomationsPresenterTest {

    private static List<Automation> AUTOMATIONS = Lists.newArrayList(new Automation(1, "Test", "Test automation","time", "23:45:00", "on", "device"),
            new Automation(2, "Test 2", "Test automation 2", "time", "23:11:00", "open", "device"));

    @Mock
    private AutomationsRepository mAutomationsRepository;

    @Mock
    private AutomationsContract.View mAutomationsView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<AutomationsRepository.LoadAutomationsCallback> mLoadAutomationsCallbackCaptor;

    @Captor
    private ArgumentCaptor<AutomationsRepository.DeleteAutomationCallback> mDeleteAutomationCallbackCaptor;

    private AutomationsPresenter mAutomationsPresenter;

    @Before
    public void setupAutomationPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mAutomationsPresenter = new AutomationsPresenter(mAutomationsRepository, mAutomationsView);
    }

    @Test
    public void loadAutomationsFromRepositoryAndLoadIntoView() {
        // Given an initialized AutomationsPresenter with initialized automations
        // When loading of automations is requested
        mAutomationsPresenter.loadAutomations();

        // Callback is captured and invoked with stubbed automations
        verify(mAutomationsView).setProgressIndicator(true);
        verify(mAutomationsRepository).getAutomations(mLoadAutomationsCallbackCaptor.capture());
        mLoadAutomationsCallbackCaptor.getValue().onAutomationsLoaded(AUTOMATIONS);

        // Progress indicator is hidden and automations are shown in the UI
        verify(mAutomationsView).setProgressIndicator(false);
        verify(mAutomationsView).showAutomations(AUTOMATIONS);
    }

    @Test
    public void loadAutomationsFromRepositoryFailureAndErrorMessageShown() {
        // Given an initialized AutomationsPresenter with initialized automations
        // When loading of automations is requested
        mAutomationsPresenter.loadAutomations();

        // Callback is captured and invoked with stubbed automations
        verify(mAutomationsView).setProgressIndicator(true);
        verify(mAutomationsRepository).getAutomations(mLoadAutomationsCallbackCaptor.capture());
        mLoadAutomationsCallbackCaptor.getValue().onAutomationsLoadError("Automation load error!");

        // Progress indicator is hidden and error message is shown in the UI
        verify(mAutomationsView).setProgressIndicator(false);
        verify(mAutomationsView).showAutomationsLoadError("Automation load error!");
    }

    @Test
    public void clickOnAutomation_ShowsDetailUi() {
        // Given a stubbed Automation
        Automation requestedAutomation = AUTOMATIONS.get(0);

        // When open automation is requested
        mAutomationsPresenter.openAutomationDetails(requestedAutomation);

        // Then automation detail UI is shown
        verify(mAutomationsView).showAutomationDetailUI(any(Integer.class));
    }

    @Test
    public void clickOnFab_ShowsAddAutomationUI() {
        // When adding a new automation
        mAutomationsPresenter.addAutomation();
        // Then add Automations UI is shown
        verify(mAutomationsView).showAddAutomation();
    }

    @Test
    public void deleteAutomation() {
        mAutomationsPresenter.deleteAutomation(AUTOMATIONS.get(1));

        verify(mAutomationsView).setProgressIndicator(true);
        verify(mAutomationsRepository).deleteAutomation(eq(AUTOMATIONS.get(1).getId()), mDeleteAutomationCallbackCaptor.capture());
        mDeleteAutomationCallbackCaptor.getValue().onAutomationDeleted();

        // Message is shown in the UI
        verify(mAutomationsView).setProgressIndicator(false);
        verify(mAutomationsView).showDeleteAutomation();
    }

    @Test
    public void deleteAutomationError() {
        mAutomationsPresenter.deleteAutomation(AUTOMATIONS.get(1));

        verify(mAutomationsView).setProgressIndicator(true);
        verify(mAutomationsRepository).deleteAutomation(eq(AUTOMATIONS.get(1).getId()), mDeleteAutomationCallbackCaptor.capture());
        mDeleteAutomationCallbackCaptor.getValue().onAutomationDeleteError("Error");

        // Error message is shown in the UI
        verify(mAutomationsView).setProgressIndicator(false);
        verify(mAutomationsView).showDeleteAutomationError("Error");
    }

}
