package no.aegisdynamics.habitat.data.profile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Unit tests for the ProfileDataHelper class.
 */
public class ProfileDataHelperTest {

    @Test
    public void parseProfileData_returnProfile() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONObject profileData = new JSONObject();
        JSONArray dashboardArray = new JSONArray();

        profileData.put("id", 1);
        profileData.put("login", "user");
        profileData.put("name", "User");
        profileData.put("email", "user@domain.com");

        dashboardArray.put("device-1");
        dashboardArray.put("device-2");

        profileData.put("dashboard", dashboardArray);

        jsonObject.put("data", profileData);

        Profile userProfile = ProfileDataHelper.getProfileFromJsonData(profileData);

        assertNotNull(userProfile);
        assertEquals(userProfile.getId(), 1);
        assertEquals(userProfile.getUserName(), "user");
        assertEquals(userProfile.getEmail(), "user@domain.com");
        assertEquals(userProfile.getName(), "User");

        Profile badUserProfile = ProfileDataHelper.getProfileFromJsonData(jsonObject);
        assertNull(badUserProfile);
    }
}
