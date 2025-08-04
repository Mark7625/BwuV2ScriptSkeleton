package botwithus.gui;

import botwithus.CowKiller;
import botwithus.ui.BaseTab;
import net.botwithus.imgui.ImGui;
import net.botwithus.ui.workspace.Workspace;

public class CowKillerGUI extends BaseTab {
    private final CowKiller script;

    public CowKillerGUI(CowKiller script) {
        this.script = script;
    }

    public void render(Workspace workspace) {
    }

    private void renderMainTab() {
        renderSectionHeader("Script Settings");

        // Banking toggle
        boolean currentBankingState = script.isBankingEnabled();
        if (ImGui.checkbox("Enable Banking", currentBankingState)) {
            script.setBankingEnabled(!currentBankingState);
        }

        addSpacing();

        renderSectionHeader("Script Status");

        addSpacing();

        // Display current status
        ImGui.text("Banking Enabled: " + (script.isBankingEnabled() ? "Yes" : "No"));
        ImGui.text("Backpack Full: " + (script.isBackpackFull() ? "Yes" : "No"));


    }
}
