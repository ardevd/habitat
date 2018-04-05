package no.aegisdynamics.habitat.util;

import org.junit.Test;

import no.aegisdynamics.habitat.R;

import static junit.framework.Assert.assertEquals;

public class IconHelperTest {

    @Test
    public void getSSLIndicatorIcon_test() {
        assertEquals(IconHelper.getSSLIndicatorIcon(true), R.drawable.ic_lock);
        assertEquals(IconHelper.getSSLIndicatorIcon(false), R.drawable.ic_unlock);
    }

    @Test
    public void getSSLIndicatorTint_test() {
        assertEquals(IconHelper.getSSLIndicatorTint(true), R.color.colorWhite);
        assertEquals(IconHelper.getSSLIndicatorTint(false), R.color.colorAccent);
    }
}
