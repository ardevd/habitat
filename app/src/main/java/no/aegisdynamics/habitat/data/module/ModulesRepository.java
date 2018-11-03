package no.aegisdynamics.habitat.data.module;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing module data
 */
public interface ModulesRepository {
    interface LoadModulesCallback {
        void onModulesListed(List<Module> modules);
        void onModulesListError(String error);
    }

    interface LoadSingleModuleCallback {
        void onModuleLoaded(Module module);
        void onModuleLoadError(String error);
    }

    interface InstallModuleCallback {
        void onModuleInstalled(int responseCode);
        void onModuleInstallError(String error);
    }

    interface DeleteModuleCallback {
        void onModuleDeleted(Module module);
        void onModuleDeleteError(Module module, String error);
    }

    void getModules(@NonNull LoadModulesCallback callback);
    void getModule(@NonNull LoadSingleModuleCallback callback);
    void installModule(@NonNull String moduleUrl, @NonNull InstallModuleCallback callback);
    void deleteModule(Module module, @NonNull DeleteModuleCallback callback);
}
