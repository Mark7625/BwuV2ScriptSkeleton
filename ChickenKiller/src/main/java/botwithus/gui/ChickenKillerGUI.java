package botwithus.gui;

import botwithus.ChickenKiller;
import net.botwithus.imgui.ImGui;
import net.botwithus.ui.workspace.Workspace;

public class ChickenKillerGUI {
    private final ChickenKiller script;

    public ChickenKillerGUI(ChickenKiller script) {
        this.script = script;
    }

    public void render(Workspace workspace) {
        if (ImGui.begin("ChickenKiller", 0)) {
            // Banking toggle
            boolean currentBankingState = script.isBankingEnabled();
            if (ImGui.checkbox("Enable Banking", currentBankingState)) {
                script.setBankingEnabled(!currentBankingState);
            }
        }
        ImGui.end();
    }

}
