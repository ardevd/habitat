package no.aegisdynamics.habitat.util;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
/**
 * Unit tests for the Date helper class.
 */

public class DateHelperTest {

    @Test
    public void isValidAutomationTimestamp_ValidString() throws Exception {
        String validTimestampString = "20:21";

        assertThat(DateHelper.isValidAutomationTimestamp(validTimestampString), is(true));
    }

    @Test
    public void isValidAutomationTimestamp_InvalidString() throws Exception {
        String invalidTimestampString = "2021";

        assertThat(DateHelper.isValidAutomationTimestamp(invalidTimestampString), is(false));
    }

    @Test
    public void isGeneratedTimestampStringValidTest() throws Exception {
        String generatedTimestamp = DateHelper.getDefaultTimestampString();
        assertThat(DateHelper.isValidAutomationTimestamp(generatedTimestamp), is(true));
    }

}
