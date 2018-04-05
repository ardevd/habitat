package no.aegisdynamics.habitat.data.automation;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.automations.AutomationsActivity;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit test for the ContentProviderAutomationRepository
 */

public class ContentProviderAutomationRepositoryTest {

    private final static int AUTOMATION_ID = 0;
    private final static String AUTOMATION_NAME = "Test automation";
    private final static String AUTOMATION_DESCRIPTION ="Test description";
    private final static String AUTOMATION_TYPE = "single";
    private final static String AUTOMATION_TRIGGER = "10:45";
    private final static String AUTOMATION_COMMANDS = "on";
    private final static String AUTOMATION_DEVICE_ID = "switch_01";

    private final static Automation AUTOMATION = new Automation(AUTOMATION_ID, AUTOMATION_NAME,
            AUTOMATION_DESCRIPTION, AUTOMATION_TYPE, AUTOMATION_TRIGGER, AUTOMATION_COMMANDS,
            AUTOMATION_DEVICE_ID);

    private List<Automation> AUTOMATIONS;

    @Mock
    Context context;

    @Mock
    AutomationsServiceApi automationsServiceApi;

    @Mock
    AutomationsRepository.CreateAutomationCallback mockedCreateAutomationCallback;

    @Mock
    AutomationsRepository.DeleteAutomationCallback mockedDeleteAutomationCallback;

    @Mock
    AutomationsRepository.GetAutomationCallback mockedGetAutomationCallback;

    @Mock
    AutomationsRepository.LoadAutomationsCallback mockedLoadAutomationsCallback;

    @Captor
    private ArgumentCaptor<AutomationsServiceApi.AutomationsServiceCallback> automationsServiceCallback;

    private AutomationsRepository automationsRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        automationsRepository = new ContentProviderAutomationProviderRepository(automationsServiceApi,
                context);
        AUTOMATIONS = new ArrayList<>();
        AUTOMATIONS.add(AUTOMATION);
    }

    @Test
    public void getAutomation_success() {
        automationsRepository.getAutomation(AUTOMATION.getId(), mockedGetAutomationCallback);

        verify(automationsServiceApi).getAutomation(eq(context), eq(AUTOMATION.getId()),
                automationsServiceCallback.capture());

        automationsServiceCallback.getValue().onLoaded(AUTOMATION);

        verify(mockedGetAutomationCallback).onAutomationLoaded(eq(AUTOMATION));
    }

    @Test
    public void getAutomation_error() {
        automationsRepository.getAutomation(AUTOMATION.getId(), mockedGetAutomationCallback);

        verify(automationsServiceApi).getAutomation(eq(context), eq(AUTOMATION.getId()),
                automationsServiceCallback.capture());

        automationsServiceCallback.getValue().onError("Error");

        verify(mockedGetAutomationCallback).onAutomationLoadError(eq("Error"));
    }

    @Test
    public void deleteAutomation_success() {
        automationsRepository.deleteAutomation(AUTOMATION.getId(), mockedDeleteAutomationCallback);

        verify(automationsServiceApi).removeAutomation(eq(context), eq(AUTOMATION.getId()),
                automationsServiceCallback.capture());

        automationsServiceCallback.getValue().onLoaded(true);

        verify(mockedDeleteAutomationCallback).onAutomationDeleted();
    }

    @Test
    public void deleteAutomation_error() {
        automationsRepository.deleteAutomation(AUTOMATION.getId(), mockedDeleteAutomationCallback);

        verify(automationsServiceApi).removeAutomation(eq(context), eq(AUTOMATION.getId()),
                automationsServiceCallback.capture());

        automationsServiceCallback.getValue().onError("error");

        verify(mockedDeleteAutomationCallback).onAutomationDeleteError("error");
    }

    @Test
    public void getAutomations_success() {
        automationsRepository.getAutomations(mockedLoadAutomationsCallback);
        verify(automationsServiceApi).getAllAutomations(eq(context),
                automationsServiceCallback.capture());
        automationsServiceCallback.getValue().onLoaded(AUTOMATIONS);
        verify(mockedLoadAutomationsCallback).onAutomationsLoaded(eq(AUTOMATIONS));
    }

    @Test
    public void getAutomations_error() {
        automationsRepository.getAutomations(mockedLoadAutomationsCallback);
        verify(automationsServiceApi).getAllAutomations(eq(context),
                automationsServiceCallback.capture());
        automationsServiceCallback.getValue().onError("Error");
        verify(mockedLoadAutomationsCallback).onAutomationsLoadError(eq("Error"));
    }

    @Test
    public void createAutomation_success() {
        automationsRepository.createAutomation(AUTOMATION, mockedCreateAutomationCallback);
        verify(automationsServiceApi).createAutomation(eq(context), eq(AUTOMATION),
                automationsServiceCallback.capture());
        automationsServiceCallback.getValue().onLoaded(AUTOMATION);
        verify(mockedCreateAutomationCallback).onAutomationCreated();
    }

    @Test
    public void createAutomation_error() {
        automationsRepository.createAutomation(AUTOMATION, mockedCreateAutomationCallback);
        verify(automationsServiceApi).createAutomation(eq(context), eq(AUTOMATION),
                automationsServiceCallback.capture());
        automationsServiceCallback.getValue().onError("error");
        verify(mockedCreateAutomationCallback).onAutomationCreateError(eq("error"));
    }
}
