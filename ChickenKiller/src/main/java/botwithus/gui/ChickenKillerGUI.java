package botwithus.gui;

import botwithus.ChickenKiller;
import botwithus.ui.BaseTab;
import net.botwithus.imgui.ImGui;
import net.botwithus.ui.workspace.Workspace;

public class ChickenKillerGUI extends BaseTab {
    private final ChickenKiller script;

    public ChickenKillerGUI(ChickenKiller script) {
        this.script = script;
    }

    public void render(Workspace workspace) {
        renderMainTab();
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
