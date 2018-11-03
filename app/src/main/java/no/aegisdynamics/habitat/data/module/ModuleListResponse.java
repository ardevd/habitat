package no.aegisdynamics.habitat.data.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable model class for a list of modules
 * as returned by the Z-Way JSON API.
 */
public class ModuleListResponse {

    @SerializedName("data")
    List<Module> modules;

    // Public constructor is necessary for collections
    public ModuleListResponse() {
        modules = new ArrayList<>();
    }

    public static ModuleListResponse parseJSON(String response) {
        Gson gsonObj = new GsonBuilder().create();
        return gsonObj
                .fromJson(response, ModuleListResponse.class);
    }
}
