package no.aegisdynamics.habitat.util;

import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the VolleyResponse helper class
 */
public class VolleyResponseHelperTest {

    @Test
    public void isResponseOk_true() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        assertEquals(VolleyResponseHelper.hasResponseReturnedOK(jsonObject), true);

        JSONObject errorJsonObject = new JSONObject();
        errorJsonObject.put("code", 404);
        assertEquals(VolleyResponseHelper.hasResponseReturnedOK(errorJsonObject), false);

        JSONObject badJsonObject = new JSONObject();
        badJsonObject.put("noCode", 200);
        assertEquals(VolleyResponseHelper.hasResponseReturnedOK(badJsonObject), false);
    }
}
