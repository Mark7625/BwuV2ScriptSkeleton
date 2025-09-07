package botwithus.ui;

import botwithus.FlaxPickerBWU;
import net.botwithus.imgui.ImGui;
import net.botwithus.xapi.script.ui.interfaces.BuildableUI;

public class FlaxPickerUI implements BuildableUI {
    private FlaxPickerBWU script;

    public FlaxPickerUI(FlaxPickerBWU script) {
        this.script = script;
    }

    @Override
    public void buildUI() {
        ImGui.text("This script has no settings.");
    }
}
