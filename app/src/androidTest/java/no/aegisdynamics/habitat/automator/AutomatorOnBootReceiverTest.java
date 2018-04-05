package no.aegisdynamics.habitat.automator;


import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import no.aegisdynamics.habitat.data.automation.AutomationsRepository;

import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class AutomatorOnBootReceiverTest {

    private Context context;
    private AutomatorOnBootReceiver automatorOnBootReceiver;
    @Mock
    private AutomatorScheduler as;
    @Mock
    private AutomationsRepository mDevicesRepository;

    @Before
    public void setupSetupPresenter() {
        context = InstrumentationRegistry.getTargetContext();
        // Get a reference to the class under test
        automatorOnBootReceiver = new AutomatorOnBootReceiver();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnBootCompleteReceive() {
        // prepare data for onReceive and call it
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        automatorOnBootReceiver.onReceive(context, intent);
        assertNull(automatorOnBootReceiver.getResultData());
    }
}
