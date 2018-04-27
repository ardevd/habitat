package no.aegisdynamics.habitat.data.device;

import java.util.Date;

/**
 * Immutable model class for a Z-way controller
 */
public class Controller {

    private final int mRemoteId;
    private final String mFirmwareVersion;
    private final Date mFirstStart;
    private final String mStatus;

    public Controller(int remoteId,
                      String firmwareVersion,
                      Date firstStart,
                      String status) {
        mRemoteId = remoteId;
        mFirmwareVersion = firmwareVersion;
        mFirstStart = firstStart;
        mStatus = status;
    }

    public int getRemoteId() {
        return mRemoteId;
    }

    public String getFirmwareVersion() {
        return mFirmwareVersion;
    }

    public Date getFirstStartDate() {
        return mFirstStart;
    }

    public String getStatus() {
        return mStatus;
    }
}
