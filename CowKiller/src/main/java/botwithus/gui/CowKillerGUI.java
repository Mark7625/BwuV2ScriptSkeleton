package botwithus.gui;

import botwithus.CowKiller;
import botwithus.ui.BaseTab;
import net.botwithus.imgui.ImGui;

/**
 * Simple GUI for the CowKiller script.
 * Extends BaseTab to provide a clean UI interface.
 */
public class CowKillerGUI extends BaseTab {
    
    private final CowKiller cowKiller;
    
    /**
     * Constructor for the CowKiller GUI.
     * @param cowKiller Reference to the main CowKiller script
     */
    public CowKillerGUI(CowKiller cowKiller) {
        super();
        this.cowKiller = cowKiller;
    }
    
    @Override
    public void render() {
        renderSectionHeader("CowKiller Settings");
        
        // Banking checkbox
        renderCheckbox("Enable Banking", cowKiller.isBankingEnabled(), () -> {
            cowKiller.setBankingEnabled(!cowKiller.isBankingEnabled());
        });
        
        addSpacing();
        
        // Help section
        renderCollapsibleSection("Help", () -> {
            ImGui.text("This script automatically kills cows and banks when inventory is full using load last preset.");
            ImGui.text("Enable/disable banking using the checkbox above.");
            ImGui.text("The script will walk to the cow area and attack the nearest cow.");
        }, true);
        
        addSpacing();
        
        // Status section
        renderSectionHeader("Status");
        ImGui.text("Banking: " + (cowKiller.isBankingEnabled() ? "Enabled" : "Disabled"));
    }
    

}
