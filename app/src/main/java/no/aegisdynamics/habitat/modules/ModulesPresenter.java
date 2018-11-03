package no.aegisdynamics.habitat.modules;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.module.Module;
import no.aegisdynamics.habitat.data.module.ModulesRepository;

public class ModulesPresenter implements ModulesContract.UserActionsListener {

    private final ModulesRepository mModulesRepository;
    private final ModulesContract.View mModulesView;

    ModulesPresenter(@NonNull ModulesRepository modulesRepository,
                     @NonNull ModulesContract.View modulesView) {
        mModulesRepository = modulesRepository;
        mModulesView = modulesView;
    }

    @Override
    public void loadModules() {
        mModulesView.setProgressIndicator(true);
        mModulesRepository.getModules(new ModulesRepository.LoadModulesCallback() {
            @Override
            public void onModulesListed(List<Module> modules) {
                mModulesView.setProgressIndicator(false);
                mModulesView.showModules(modules);
            }

            @Override
            public void onModulesListError(String error) {
                mModulesView.setProgressIndicator(false);
                mModulesView.showModulesLoadError(error);
            }
        });
    }

    @Override
    public void deleteModule(final Module module) {
       mModulesRepository.deleteModule(module, new ModulesRepository.DeleteModuleCallback() {
           @Override
           public void onModuleDeleted(Module module) {
               mModulesView.showModuleDeleted(module);
           }

           @Override
           public void onModuleDeleteError(Module module, String error) {
               mModulesView.showModuleDeleteError(error);
           }
       });
    }

    @Override
    public void installModule(String moduleUrl) {
        mModulesView.setProgressIndicator(true);
        mModulesRepository.installModule(moduleUrl, new ModulesRepository.InstallModuleCallback() {
            @Override
            public void onModuleInstalled(int responseCode) {
                mModulesView.setProgressIndicator(false);
                mModulesView.showModuleInstalled();
            }

            @Override
            public void onModuleInstallError(String error) {
                mModulesView.setProgressIndicator(false);
                mModulesView.showModuleInstallError(error);
            }
        });
    }
}
