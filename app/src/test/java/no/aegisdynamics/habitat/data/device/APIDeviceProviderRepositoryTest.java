package no.aegisdynamics.habitat.data.device;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the APIDeviceProviderRepositoryTest
 */
public class APIDeviceProviderRepositoryTest {
    private static final String DEVICE_ID = "device_1";
    private static final String DEVICE_TITLE = "Device 1";
    private static final String DEVICE_TYPE = "device";
    private static final String DEVICE_LOCATION = "room";
    private static final int DEVICE_LOCATION_ID = 1;
    private static final int DEVICE_CREATOR_ID = 2;
    private static final String[] DEVICE_TAGS = new String[]{"Tag 1", "Tag2"};
    private static final String DEVICE_STATUS = "on";
    private static final int DEVICE_MIN_VALUE = 0;
    private static final int DEVICE_MAX_VALUE = 100;
    private static final String DEVICE_STATUS_NOTATION = "celsius";
    private static final String DEVICE_PROBE_TITLE = "Temperature";
    private static final String DEVICE_ICON_NAME = "deviceicon";

    private static final Device DEVICE = new Device(DEVICE_ID, DEVICE_TITLE, DEVICE_TYPE, DEVICE_LOCATION,
            DEVICE_LOCATION_ID, DEVICE_CREATOR_ID, DEVICE_TAGS, DEVICE_STATUS, DEVICE_MIN_VALUE,
            DEVICE_MAX_VALUE, DEVICE_STATUS_NOTATION, DEVICE_PROBE_TITLE, DEVICE_ICON_NAME);

    private static List<Device> DEVICES;

    @Mock
    Context context;

    @Mock
    DevicesServiceApi serviceApi;

    @Mock
    DevicesRepository.LoadDevicesCallback mockedLoadDeviceCallback;

    @Mock
    DevicesRepository.GetDeviceCallback mockedGetDeviceCallback;

    @Captor
    private ArgumentCaptor<DevicesServiceApi.DevicesServiceCallback> devicesServiceCallbackCaptor;

    private DevicesRepository devicesRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        devicesRepository = new APIDeviceProviderRepository(serviceApi, context);
        DEVICES = new ArrayList<>();
        DEVICES.add(DEVICE);
    }

    @Test
    public void getDevices_showSuccess() {
        devicesRepository.getDevices(mockedLoadDeviceCallback);
        verify(serviceApi).getAllDevices(eq(context), devicesServiceCallbackCaptor.capture());
        devicesServiceCallbackCaptor.getValue().onLoaded(DEVICES);

        verify(mockedLoadDeviceCallback).onDevicesLoaded(eq(DEVICES));
    }

    @Test
    public void getDevices_showError() {
        devicesRepository.getDevices(mockedLoadDeviceCallback);
        verify(serviceApi).getAllDevices(eq(context), devicesServiceCallbackCaptor.capture());
        devicesServiceCallbackCaptor.getValue().onError("error");

        verify(mockedLoadDeviceCallback).onDevicesLoadError(eq("error"));
    }

    @Test
    public void getDevice_showSuccess() {
        devicesRepository.getDevice(DEVICE.getId(), mockedGetDeviceCallback);
        verify(serviceApi).getDevice(eq(context), eq(DEVICE.getId()), devicesServiceCallbackCaptor.capture());
        devicesServiceCallbackCaptor.getValue().onLoaded(DEVICE);

        verify(mockedGetDeviceCallback).onDeviceLoaded(eq(DEVICE));
    }

    @Test
    public void getDevice_showError() {
        devicesRepository.getDevice(DEVICE.getId(), mockedGetDeviceCallback);
        verify(serviceApi).getDevice(eq(context), eq(DEVICE.getId()), devicesServiceCallbackCaptor.capture());
        devicesServiceCallbackCaptor.getValue().onError("error");

        verify(mockedGetDeviceCallback).onDeviceLoadError(eq("error"));
    }
}
