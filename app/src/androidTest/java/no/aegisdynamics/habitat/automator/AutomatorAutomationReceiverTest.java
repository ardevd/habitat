package no.aegisdynamics.habitat.automator;


import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;


import static junit.framework.Assert.assertNull;
@RunWith(AndroidJUnit4.class)
public class AutomatorAutomationReceiverTest {

    private Context context;
    private AutomatorAutomationReceiver automatorAutomationReceiver;

    @Before
    public void setupSetupPresenter() {
        context = InstrumentationRegistry.getTargetContext();
        // Get a reference to the class under test
        automatorAutomationReceiver = new AutomatorAutomationReceiver();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnBootCompleteReceive() {
        // prepare data for onReceive and call it
        Intent intent = new Intent("automatorAutomationReceiver");
        automatorAutomationReceiver.onReceive(context, intent);
        assertNull(automatorAutomationReceiver.getResultData());
    }
}
