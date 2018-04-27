package no.aegisdynamics.habitat.log;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.log.HabitatLog;
import no.aegisdynamics.habitat.data.log.LogRepository;

public class LogPresenter implements LogContract.UserActionListener {

    private final LogRepository mLogsRepository;
    private final LogContract.View mLogsView;

    LogPresenter(@NonNull LogRepository logsRepository, @NonNull LogContract.View logsView) {
        mLogsRepository = logsRepository;
        mLogsView = logsView;
    }

    @Override
    public void loadLogs() {
        mLogsView.setProgressIndicator(true);
        mLogsRepository.getLogs(new LogRepository.LoadLogsCallback() {
            @Override
            public void onLogsLoaded(List<HabitatLog> logs) {
                mLogsView.setProgressIndicator(false);
                mLogsView.showLogs(logs);
            }

            @Override
            public void onLogsLoadError(String error) {
                mLogsView.setProgressIndicator(false);
                mLogsView.showLogsLoadError(error);
            }
        });
    }

    @Override
    public void deleteLog(HabitatLog log) {
        mLogsView.setProgressIndicator(true);
        mLogsRepository.deleteLog(log.getId(), new LogRepository.DeleteLogCallback() {
            @Override
            public void onLogDeleted() {
                mLogsView.setProgressIndicator(false);
                mLogsView.showLogDeleted();
            }

            @Override
            public void onLogDeleteError(String error) {
                mLogsView.setProgressIndicator(false);
                mLogsView.showLogDeleteError(error);
            }
        });

    }

    @Override
    public void deleteAllLogs() {

        mLogsView.setProgressIndicator(true);
        mLogsRepository.deleteAllLogs(new LogRepository.DeleteAllLogsCallback() {
            @Override
            public void onLogsDeleted(int count) {
                mLogsView.setProgressIndicator(false);
                mLogsView.showLogsDeleted(count);            }

            @Override
            public void onLogsDeleteError(String error) {
                mLogsView.setProgressIndicator(false);
                mLogsView.showLogsDeletedError(error);
            }
        });

    }
}
