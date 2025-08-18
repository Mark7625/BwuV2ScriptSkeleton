package botwithus.gui;

import botwithus.ChickenKiller;
import net.botwithus.imgui.ImGui;
import net.botwithus.ui.workspace.Workspace;

public class ChickenKillerGUI {
    private final ChickenKiller script;
    private boolean showDebugWindow = false;

    public ChickenKillerGUI(ChickenKiller script) {
        this.script = script;
    }

    public void render(Workspace workspace) {
        if (ImGui.begin("ChickenKiller", 0)) {
            script.setBankingEnabled(ImGui.checkbox("Enable Banking", script.isBankingEnabled()));
        }
        ImGui.end();
    }

}
