package no.aegisdynamics.habitat.base;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.devices.DevicesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppNavigationTest {

    @Rule
    public ActivityTestRule<DevicesActivity> mActivityTestRule =
            new ActivityTestRule<>(DevicesActivity.class);

    @Test
    public void clickOnLocationNavigationItem_ShowsLocationScreen() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start locations screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.menu_rooms));

        // Check that locations view was opened.
        onView(withId(R.id.locations_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnNotificationsNavigationItem_ShowsNotificationsScreen() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start notifications screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.menu_events));

        // Check that notifications view was opened.
        onView(withId(R.id.notifications_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnAutomationsNavigationItem_ShowsAutomationsScreen() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start automations screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.menu_automation));

        // Check that automations view was opened.
        onView(withId(R.id.automations_title)).check(matches(isDisplayed()));
    }


}
