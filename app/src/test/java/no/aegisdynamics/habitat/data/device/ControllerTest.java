package no.aegisdynamics.habitat.data.device;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class ControllerTest {

    Controller controller;
    Date mDate = new Date();

    @Before
    public void setUpController() {
        controller = new Controller(123, "2.3.7",
                mDate, "200 OK");
    }

    @Test
    public void getRemoteId() {
        assertEquals(controller.getRemoteId(), 123);
    }

    @Test
    public void getFirmwareVersion() {
        assertEquals(controller.getFirmwareVersion(), "2.3.7");
    }

    @Test
    public void getStartupDate() {
        assertEquals(controller.getFirstStartDate(), mDate);
    }

    @Test
    public void getStatus() {
        assertEquals(controller.getStatus(), "200 OK");
    }
}
