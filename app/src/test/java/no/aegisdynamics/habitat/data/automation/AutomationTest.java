package no.aegisdynamics.habitat.data.automation;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the Automation class.
 */

public class AutomationTest {

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

    @Test
    public void testGetAutomationId() throws Exception {
        assertEquals(AUTOMATION_ID, AUTOMATION.getId());
    }

    @Test
    public void testGetAutomationName() throws Exception {
        assertEquals(AUTOMATION_NAME, AUTOMATION.getName());
    }

    @Test
    public void testGetAutomationDescription() throws Exception {
        assertEquals(AUTOMATION_DESCRIPTION, AUTOMATION.getDescription());
    }

    @Test
    public void testGetAutomationType() throws Exception {
        assertEquals(AUTOMATION_TYPE, AUTOMATION.getType());
    }

    @Test
    public void testGetAutomationTrigger() throws Exception {
        assertEquals(AUTOMATION_TRIGGER, AUTOMATION.getTrigger());
    }

    @Test
    public void testGetAutomationCommands() throws Exception {
        assertEquals(AUTOMATION_COMMANDS, AUTOMATION.getCommands());
    }

    @Test
    public void testGetAutomationDeviceId() throws Exception {
        assertEquals(AUTOMATION_DEVICE_ID, AUTOMATION.getDeviceId());
    }

    @Test
    public void testSetAndQueryAutomationId() throws Exception {
        // Assert that initial value is correct.
        Automation mAutomation = new Automation(AUTOMATION_ID, AUTOMATION_NAME,
                AUTOMATION_DESCRIPTION, AUTOMATION_TYPE, AUTOMATION_TRIGGER, AUTOMATION_COMMANDS,
                AUTOMATION_DEVICE_ID);
        assertEquals(AUTOMATION_ID, mAutomation.getId());
        // Update ID
        mAutomation.setId(1);
        // Assert that new value is used.
        assertEquals(1, mAutomation.getId());


    }
}
