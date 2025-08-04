package botwithus;

import net.botwithus.imgui.ImGui;
import net.botwithus.ui.workspace.Workspace;

public class ScriptSkeletonGUI {

    private final ScriptSkeleton script;

    public ScriptSkeletonGUI(ScriptSkeleton script) {
        this.script = script;
    }

    /**
     * Renders the script's GUI.
     */
    public void render(Workspace workspace) {
        ImGui.begin("Script Skeleton", 0);

        ImGui.text("This is a sample GUI for the ScriptSkeleton.");
        ImGui.separator();

        if (ImGui.button("Click Me!", 0.0f, 0.0f)) {
            script.println("Button clicked!");
        }

        ImGui.end();
    }
}
