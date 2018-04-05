package no.aegisdynamics.habitat.automations;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.aegisdynamics.habitat.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for the automations screen, the main screen which contains a list of all automations.
 */
@RunWith(AndroidJUnit4.class)
public class AutomationsScreenTest {
    @Rule
    public ActivityTestRule<AutomationsActivity> mAutomationsActivityTestRule =
            new ActivityTestRule<>(AutomationsActivity.class);

    @Test
    public void clickAddAutomationButton_opensAddAutomationUI() throws Exception {
        // Click on the add automation button
        onView(withId(R.id.fab_new_automation)).perform(click());

        // Check if the add automation screen is displayed
        onView(withId(R.id.automationadd_title)).check(matches(isDisplayed()));
    }

}
