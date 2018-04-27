package no.aegisdynamics.habitat.data;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mock;

import no.aegisdynamics.habitat.data.automation.AutomationsRepository;
import no.aegisdynamics.habitat.data.automation.ContentProviderAutomationProviderRepository;
import no.aegisdynamics.habitat.data.log.ContentProviderLogRepository;
import no.aegisdynamics.habitat.data.log.LogRepository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Unit tests for the Injection class
 */
public class InjectionTest {

    @Mock
    private Context context;

    @Test
    public void provideLogRepositoryTest() {
        LogRepository repository = Injection.provideLogRepository(context);
        assertNotNull(repository);
        assertEquals(repository instanceof ContentProviderLogRepository, true);
    }

    @Test
    public void provideAutomationRepositoryTest() {
        AutomationsRepository repository = Injection.provideAutomationsRepository(context);
        assertNotNull(repository);
        assertEquals(repository instanceof ContentProviderAutomationProviderRepository, true);
    }
}
