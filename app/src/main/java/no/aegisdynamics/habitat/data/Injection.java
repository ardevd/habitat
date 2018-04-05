package no.aegisdynamics.habitat.data;

import android.content.Context;

import no.aegisdynamics.habitat.data.automation.AutomationRepositories;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;
import no.aegisdynamics.habitat.data.automation.AutomationsServiceApiImpl;
import no.aegisdynamics.habitat.data.backup.BackupRepositories;
import no.aegisdynamics.habitat.data.backup.BackupsServiceApiImpl;
import no.aegisdynamics.habitat.data.backup.BackupsRepository;
import no.aegisdynamics.habitat.data.device.DeviceRepositories;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.device.DevicesServiceApiImpl;
import no.aegisdynamics.habitat.data.location.LocationRepositories;
import no.aegisdynamics.habitat.data.location.LocationsRepository;
import no.aegisdynamics.habitat.data.location.LocationsServiceApiImpl;
import no.aegisdynamics.habitat.data.notifications.NotificationRepositories;
import no.aegisdynamics.habitat.data.notifications.NotificationsRepository;
import no.aegisdynamics.habitat.data.notifications.NotificationsServiceApiImpl;
import no.aegisdynamics.habitat.data.profile.ProfileRepositories;
import no.aegisdynamics.habitat.data.profile.ProfileRepository;
import no.aegisdynamics.habitat.data.profile.ProfileServiceApiImpl;
import no.aegisdynamics.habitat.data.weather.WeatherRepositories;
import no.aegisdynamics.habitat.data.weather.WeatherRepository;
import no.aegisdynamics.habitat.data.weather.WeatherServiceApiImpl;


public class Injection {

    public static DevicesRepository provideDevicesRepository(Context context) {
        return DeviceRepositories.getRepository(context, new DevicesServiceApiImpl());
    }

    public static LocationsRepository provideLocationsRepository(Context context) {
        return LocationRepositories.getRepository(context, new LocationsServiceApiImpl());
    }

    public static NotificationsRepository provideNotificationsRepository(Context context) {
        return NotificationRepositories.getRepository(context, new NotificationsServiceApiImpl());
    }

    public static AutomationsRepository provideAutomationsRepository(Context context) {
        return AutomationRepositories.getRepository(context, new AutomationsServiceApiImpl());
    }

    public static WeatherRepository provideWeatherRepository(Context context) {
        return WeatherRepositories.getRepository(context, new WeatherServiceApiImpl());
    }

    public static BackupsRepository providerBackupRepository(Context context) {
        return BackupRepositories.getRepository(context, new BackupsServiceApiImpl());
    }

    public static ProfileRepository provideProfileRepository(Context context) {
        return ProfileRepositories.getRepository(context, new ProfileServiceApiImpl());
    }

}
