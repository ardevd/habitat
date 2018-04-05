package no.aegisdynamics.habitat.automator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import no.aegisdynamics.habitat.data.device.DevicesRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the AutomatorPresenter class.
 */

public class AutomatorPresenterTest {

    @Mock
    private AutomatorContract.Notifier mNotifier;

    @Mock
    private DevicesRepository mDeviceRepository;

    @Captor
    private ArgumentCaptor<DevicesRepository.SendAutomatedCommandCallback> sendAutomatedCommandCallback;

    private AutomatorPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mPresenter = new AutomatorPresenter(mDeviceRepository, mNotifier);
    }

    @Test
    public void sendAutomatedCommand_showSuccess() {
        String deviceId = "ZWayVDev_zway_4-0-128";
        String automationTitle = "Automation 1";
        String command = "on";
        mPresenter.sendAutomatedCommand(deviceId, automationTitle, command);
        verify(mDeviceRepository).sendAutomatedCommand(eq(deviceId), eq(command),
                eq(automationTitle), sendAutomatedCommandCallback.capture());
        sendAutomatedCommandCallback.getValue().onAutomatedCommandSent(automationTitle);
        verify(mNotifier).showCommandSuccess(eq(automationTitle));
    }

    @Test
    public void sendAutomatedCommand_showError() {
        String deviceId = "ZWayVDev_zway_4-0-128";
        String automationTitle = "Automation 1";
        String command = "on";
        mPresenter.sendAutomatedCommand(deviceId, automationTitle, command);
        verify(mDeviceRepository).sendAutomatedCommand(eq(deviceId), eq(command),
                eq(automationTitle), sendAutomatedCommandCallback.capture());
        sendAutomatedCommandCallback.getValue().onAutomatedCommandFailed("error", automationTitle);
        verify(mNotifier).showCommandFailed(eq("error"), eq(automationTitle));
    }
}
