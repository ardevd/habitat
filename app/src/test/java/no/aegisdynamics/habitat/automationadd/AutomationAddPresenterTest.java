package no.aegisdynamics.habitat.automationadd;


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
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.util.DateHelper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class AutomationAddPresenterTest {

    private static Automation AUTOMATION = new Automation(0, "Test Automation", "test description",
            "time", "20:00:00", "on", "device");

    private static Automation AUTOMATION_EMPTY_TITLE = new Automation(0, "", "test description",
            "time", "20:00:00", "on", "device");

    private static Automation AUTOMATION_EMPTY_DEVICE = new Automation(0, "Test Automation", "Test",
            "time", "20:00:00", "on", "");

    private static Automation AUTOMATION_EMPTY_COMMAND = new Automation(0, "Test Automation", "Test",
            "time", "20:00:00", "", "device");

    private static List<Device> DEVICES = Lists.newArrayList(new Device("test-device-01", "Test Device", "switchBinary", "Office", 1, 1, null, "on", 0, 0, null, null, "icon"),
            new Device("test-device-02", "Test Device 02", "doorlock", "Living Room", 2, 2, null, "close",0 , 0, null, null, "icon"));

    @Mock
    private AutomationsRepository mAutomationsRepository;

    @Mock
    private DevicesRepository mDevicesRepository;

    @Mock
    private AutomationAddContract.View mAutomationAddView;

    private AutomationAddPresenter mAutomationAddPresenter;

    @Captor
    private ArgumentCaptor<AutomationsRepository.CreateAutomationCallback> mCreateAutomationCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.LoadDevicesCallback> mGetDeviceForAutomationCallbackCaptor;

    @Before
    public void setupAutomationAddPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mAutomationAddPresenter = new AutomationAddPresenter(mDevicesRepository, mAutomationsRepository, mAutomationAddView);
    }

    @Test
    public void saveAutomationToRepository_showsSuccessMessageUi() {
        // When the presenter is asked to save a new Automation
        mAutomationAddPresenter.saveAutomation(AUTOMATION);

        // Then a Automation is,
        verify(mAutomationsRepository).createAutomation(any(Automation.class), mCreateAutomationCallbackCaptor.capture());
        mCreateAutomationCallbackCaptor.getValue().onAutomationCreated();
        verify(mAutomationAddView).showAutomationAdded(); // shown in the UI.
    }

    @Test
    public void saveAutomationToRepository_showsErrorMessageUi() {
        // When the presenter is asked to save a new Automation
        mAutomationAddPresenter.saveAutomation(AUTOMATION);

        // The UI shows an error message if the automation could not be saved.
        verify(mAutomationsRepository).createAutomation(any(Automation.class), mCreateAutomationCallbackCaptor.capture());
        mCreateAutomationCallbackCaptor.getValue().onAutomationCreateError("Error");
        verify(mAutomationAddView).showAutomationAddError("Error");
    }

    @Test
    public void saveAutomationToRepository_showsEmptyTitleMessageUi() {
        // When the presenter is asked to save a new Automation with empty title.
        mAutomationAddPresenter.saveAutomation(AUTOMATION_EMPTY_TITLE);
        // Automation is not added and error is shown in UI.
        verify(mAutomationAddView).showEmptyAutomationError();
    }


    @Test
    public void saveAutomationToRepository_showsEmptyDeviceMessageUi() {
        // When the presenter is asked to save a new Automation with empty description.
        mAutomationAddPresenter.saveAutomation(AUTOMATION_EMPTY_DEVICE);
        // Automation is not added and error is shown in UI.
        verify(mAutomationAddView).showEmptyAutomationError();
    }

    @Test
    public void saveAutomationToRepository_showsEmptyCommandMessageUi() {
        // When the presenter is asked to save a new Automation with empty description.
        mAutomationAddPresenter.saveAutomation(AUTOMATION_EMPTY_COMMAND);
        // Automation is not added and error is shown in UI.
        verify(mAutomationAddView).showEmptyAutomationError();
    }

    @Test
    public void getDevicesFromRepositoryAndLoadIntoView() {
        mAutomationAddPresenter.loadDevices();
        verify(mDevicesRepository).getDevices(mGetDeviceForAutomationCallbackCaptor.capture());
        mGetDeviceForAutomationCallbackCaptor.getValue().onDevicesLoaded(DEVICES);
        verify(mAutomationAddView).showDevices(DEVICES);
    }

    @Test
    public void getDevicesFromRepositoryAndLoadIntoViewError() {
        mAutomationAddPresenter.loadDevices();
        verify(mDevicesRepository).getDevices(mGetDeviceForAutomationCallbackCaptor.capture());
        mGetDeviceForAutomationCallbackCaptor.getValue().onDevicesLoadError("Error");
        verify(mAutomationAddView).showDevicesLoadError("Error");
    }

    @Test
    public void generateDefaultTimestamp_showInView() {
        mAutomationAddPresenter.generateDefaultTime();
        verify(mAutomationAddView).showDefaultTime(eq(DateHelper.getDefaultTimestampString()));
    }
}
