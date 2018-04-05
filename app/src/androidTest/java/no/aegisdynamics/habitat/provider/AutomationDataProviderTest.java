package no.aegisdynamics.habitat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AutomationDataProviderTest extends ProviderTestCase2<AutomationDataProvider> implements DeviceDataContract {

    final private static ContentValues values = new ContentValues();

    public AutomationDataProviderTest() {
        super(AutomationDataProvider.class, AUTHORITY);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    private void generateTestAutomationData() {
        values.clear();
        values.put(FIELD_AUTOMATION_NAME, "Name");
        values.put(FIELD_AUTOMATION_DESCRIPTION, "Description of automation");
        values.put(FIELD_AUTOMATION_TYPE, "single");
        values.put(FIELD_AUTOMATION_TRIGGER, "13:37");
        values.put(FIELD_AUTOMATION_COMMANDS, "open");
        values.put(FIELD_AUTOMATION_DEVICE, "lock-1");
    }

    @Test
    public void testInsertAndDeleteSingleAutomation(){
        generateTestAutomationData();
        // Create automation
        long id = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        assertTrue("Automation created successfully", id > -1);

        // Delete Automation
        int count = getMockContentResolver().delete(CONTENT_URI_AUTOMATIONS, "_ID=?", new String[] {String.valueOf(id)});
        assertEquals(1, count);
    }


    @Test
    public void testInsertAutomationAndQuery() {
        generateTestAutomationData();

        // Insert Value
        long id = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        // Verify insertion
        assertTrue("created successfully", id > -1);
        // Insert Another Value
        long secondId = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        // Verify that returned Id is greater than previous entry.
        assertTrue("Content Provider Id's increment correctly", secondId > id);
        // Query Content Provider again
        final Cursor queryResults = getMockContentResolver().query(CONTENT_URI_AUTOMATIONS, null, null, null, null);
        // Assert that we have two new entries in the content providers.
        assertEquals(2, queryResults.getCount());
        queryResults.close();
    }

    @Test
    public void testInsertAndQuerySingleItem() {
        generateTestAutomationData();

        // Insert Value
        long id = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        // Verify insertion
        assertTrue("created successfully", id > -1);
        // Insert Another Value
        long secondId = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        // Verify that returned Id is greater than previous entry.
        assertTrue("Content Provider Id's increment correctly", secondId > id);
        // Query Content Provider again
        final Cursor queryResults = getMockContentResolver().query(CONTENT_URI_AUTOMATIONS, null,
                FIELD_ID + "= ?", new String[]{String.valueOf(secondId)}, null);
        // Assert that we now have a single record
        assertEquals(1, queryResults.getCount());
        queryResults.close();
    }

    @Test
    public void testInsertAndUpdateItem() {
        generateTestAutomationData();

        // Insert Value
        long id = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        // Verify insertion
        assertTrue("created successfully", id > -1);
        // Insert Another Value
        long secondId = ContentUris.parseId(getMockContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        // Verify that returned Id is greater than previous entry.
        assertTrue("Content Provider Id's increment correctly", secondId > id);

        // Update second item.
        values.put(FIELD_AUTOMATION_NAME, "New Name");
        int updatedItems = getMockContentResolver().update(CONTENT_URI_AUTOMATIONS, values,
                FIELD_ID + "= ?", new String[]{String.valueOf(secondId)});

        // Assert a single item was updated
        assertTrue("One item was updated", updatedItems == 1);
        // Query Content Provider again for all items
        final Cursor queryResults = getMockContentResolver().query(CONTENT_URI_AUTOMATIONS, null, null, null, null);
        // Assert that we still have two records
        assertEquals(2, queryResults.getCount());
        queryResults.close();
    }

    @Test
    public void testGetTypeInvalid() {
        Uri einval = Uri.parse("invalid_uri");
        ContentProvider cp = getProvider();
        try {
            cp.getType(einval);
            fail("Did not get expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // empty
        }
    }

}