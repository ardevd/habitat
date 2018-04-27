package no.aegisdynamics.habitat.data.log;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertNotNull;

/**
 * Unit tests for the LogRepositories class
 */
public class LogRepositoriesTest {

    @Mock
    Context context;

    @Mock
    LogServiceApi serviceApi;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getRepositoryTest() {
        LogRepository repository;
        repository = LogRepositories.getRepository(context, serviceApi);
        assertNotNull(repository);
    }
}
