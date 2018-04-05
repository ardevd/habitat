package no.aegisdynamics.habitat.itemListeners;


import no.aegisdynamics.habitat.data.automation.Automation;

public interface AutomationItemListener {

    void onAutomationClick(Automation clickedAutomation);

    void onAutomationDeleteClick(Automation clickedAutomation);
    
}
