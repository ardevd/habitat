package no.aegisdynamics.habitat.automationadd;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.aegisdynamics.habitat.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AutomationAddActivityTest {

    @Rule
    public ActivityTestRule<AutomationAddActivity> mAutomationAddActivityTestRule =
            new ActivityTestRule<>(AutomationAddActivity.class);

    @Test
    public void TestAutomationFormFill() {
        // Verify visibility
        onView(withId(R.id.automationadd_name_edittext)).check(matches(isDisplayed()));
        // Select the name edit text
        onView(withId(R.id.automationadd_name_edittext));
        // Enter data
        onView(withId(R.id.automationadd_name_edittext)).perform(clearText(),typeText("Test Automation"));
    }
}
