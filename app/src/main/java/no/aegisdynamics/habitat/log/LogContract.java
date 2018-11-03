package no.aegisdynamics.habitat.log;

import java.util.List;

import no.aegisdynamics.habitat.data.log.HabitatLog;

interface LogContract {

    interface View {
        void showLogs(List<HabitatLog> logs);
        void showLogsLoadError(String error);
        void setProgressIndicator(boolean active);
        void showLogDeleted();
        void showLogDeleteError(String error);
        void showLogsDeleted(int count);
        void showLogsDeletedError(String error);
    }

    interface UserActionListener {
        void loadLogs();
        void deleteLog(HabitatLog log);
        void deleteAllLogs();
    }

}
