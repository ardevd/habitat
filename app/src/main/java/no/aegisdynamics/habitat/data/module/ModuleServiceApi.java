package no.aegisdynamics.habitat.data.module;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Defines an interface to the service API that is used by the app. All
 * module data requests should be piped through this interface
 */
public interface ModuleServiceApi {

    interface ModuleServiceCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    void getAllModules(@NonNull Context context, ModuleServiceCallback<List<Module>> callback);
    void getModule(@NonNull Context context, ModuleServiceCallback<Module> callback);
    void installModule(@NonNull Context context, @NonNull String url,
                       ModuleServiceCallback<Integer> callback);
    void deleteModule(@NonNull Context context, Module module, ModuleServiceCallback callback);
}
