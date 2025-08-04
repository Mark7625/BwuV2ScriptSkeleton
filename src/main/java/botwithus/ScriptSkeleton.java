package botwithus;

import net.botwithus.scripts.Info;
import net.botwithus.scripts.Script;
import net.botwithus.ui.workspace.Workspace;

@Info(name = "ScriptSkeleton", description = "A skeleton script.", version = "1.0.0", author = "YourName")
public class ScriptSkeleton extends Script {

    private final ScriptSkeletonGUI scriptGUI;

    public ScriptSkeleton() {
        this.scriptGUI = new ScriptSkeletonGUI(this);
    }

    @Override
    public void onDraw(Workspace workspace) {
        super.onDraw(workspace);
        scriptGUI.render(workspace);
    }


    @Override
    public void run() {
        println("Running main script logic...");
    }

    @Override
    public void onActivation() {
        super.onActivation();
        println("ScriptSkeleton activated.");
    }

    @Override
    public void onDeactivation() {
        super.onDeactivation();
        println("ScriptSkeleton deactivated.");
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
    }
}
