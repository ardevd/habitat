package no.aegisdynamics.habitat.data.device;

import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the ControllerDataParser class.
 */
public class ControllerDataParserTest {

    private JSONObject generateTestControllerData() throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        dataObj.put("first_start_up", "016-07-05T20:58:47.010Z");
        dataObj.put("count_of_reconnects", 134);
        dataObj.put("current_firmware", "2.3.7");
        dataObj.put("current_firmware_majurity", null);
        dataObj.put("remote_id", 12345);
        dataObj.put("firstaccess", false);

        obj.put("code", 200);
        obj.put("message", "200 OK");
        obj.put("error", null);

        obj.put("data", dataObj);

        return obj;
    }

    @Test
    public void ParseControllerDataTest() throws Exception {
        JSONObject obj = generateTestControllerData();
        Controller controller = ControllerDataParser.parseControllerJsonData(obj);
        assertEquals(controller.getFirmwareVersion(), "2.3.7");
        assertEquals(controller.getStatus(), "200 OK");
        assertEquals(controller.getRemoteId(), 12345);
    }
}
