package no.aegisdynamics.habitat.setup;


import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import no.aegisdynamics.habitat.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SetupScreenTest {

    @Rule
    public ActivityTestRule<SetupActivity> mSetupActivityTestRule =
            new ActivityTestRule<>(SetupActivity.class);

    @Test
    public void submitHostname_showsCredentialsView() throws Exception {
        String hostname = "zway.olympus.com";

        // Fill out the hostname and submit.
        onView(withId(R.id.setup_hostname)).perform(typeText(hostname), closeSoftKeyboard());
        onView(withId(R.id.setup_hostname_submit_button)).perform(click());

        // Check if the credentials screen is displayed
        onView(withId(R.id.setup_credentials_submit_button)).check(matches(isDisplayed()));
    }
}
