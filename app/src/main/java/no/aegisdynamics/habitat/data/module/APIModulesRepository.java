package no.aegisdynamics.habitat.data.module;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

public class APIModulesRepository implements ModulesRepository {

    private final ModuleServiceApi mModuleServiceApi;
    private final Context mContext;

    APIModulesRepository(@NonNull ModuleServiceApi moduleServiceApi, @NonNull Context context) {
        mModuleServiceApi = moduleServiceApi;
        mContext = context;
    }


    @Override
    public void getModules(@NonNull final LoadModulesCallback callback) {
        mModuleServiceApi.getAllModules(mContext, new ModuleServiceApi.ModuleServiceCallback<List<Module>>() {
            @Override
            public void onSuccess(List<Module> data) {
                callback.onModulesListed(data);
            }

            @Override
            public void onError(String error) {
                callback.onModulesListError(error);
            }
        });
    }

    @Override
    public void getModule(@NonNull LoadSingleModuleCallback callback) {

    }

    @Override
    public void installModule(@NonNull String moduleUrl, @NonNull final InstallModuleCallback callback) {
        mModuleServiceApi.installModule(mContext, moduleUrl, new ModuleServiceApi.ModuleServiceCallback<Integer>() {
            @Override
            public void onSuccess(Integer responseCode) {
                callback.onModuleInstalled(responseCode);
            }

            @Override
            public void onError(String error) {
                callback.onModuleInstallError(error);
            }
        });
    }

    @Override
    public void deleteModule(final Module module, @NonNull final DeleteModuleCallback callback) {
        mModuleServiceApi.deleteModule(mContext, module, new ModuleServiceApi.ModuleServiceCallback() {
            @Override
            public void onSuccess(Object data) {
                callback.onModuleDeleted(module);
            }

            @Override
            public void onError(String error) {
                callback.onModuleDeleteError(module, error);
            }
        });

    }
}
