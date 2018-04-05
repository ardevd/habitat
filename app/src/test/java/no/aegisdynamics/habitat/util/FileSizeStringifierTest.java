package no.aegisdynamics.habitat.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the FileSizeStringifier class
 */

public class FileSizeStringifierTest {

    @Test
    public void testByteToStringConversion() throws Exception {
        // Test MB
        int mbBytes = 202400;
        assertEquals(FileSizeStringifier.convertBytesToScaledString(mbBytes), String.format("%d MB", mbBytes/1000000));
        // Test Kb
    }
}
