package no.aegisdynamics.habitat.modules;

import java.util.List;

import no.aegisdynamics.habitat.data.module.Module;

public interface ModulesContract {

    interface View {
        void showModules(List<Module> modules);
        void showModulesLoadError(String error);
        void showModuleDeleted(Module module);
        void showModuleDeleteError(String error);
        void showModuleInstalled();
        void showModuleInstallError(String error);
        void setProgressIndicator(boolean active);
    }

    interface UserActionsListener {
        void loadModules();
        void deleteModule(Module module);
        void installModule(String moduleUrl);
    }
}
