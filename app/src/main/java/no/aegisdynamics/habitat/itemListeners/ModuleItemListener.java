package no.aegisdynamics.habitat.itemListeners;

import no.aegisdynamics.habitat.data.module.Module;

public interface ModuleItemListener {

    void onModuleClicked(Module clickedModule);

    void onModuleDeleteClicked(Module clickedModule);
}
