package botwithus;

import botwithus.state.BankState;
import botwithus.state.BotState;
import botwithus.state.FlaxPickState;
import net.botwithus.events.EventInfo;
import net.botwithus.imgui.ImGui;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.inventories.events.InventoryEvent;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.scripts.Info;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.node.LeafNode;
import net.botwithus.xapi.util.BwuMath;
import net.botwithus.xapi.util.time.DurationStringFormat;
import net.botwithus.xapi.util.time.Stopwatch;
import net.botwithus.xapi.util.time.Timer;

@Info(name = "FlaxPickerPS", description = "FlaxPickerPS script", version = "1.0.0", author = "BotWithUs")
public class FlaxPickerPS extends PermissiveScript {
    public final Area BANK_AREA = new Area.Rectangular(new Coordinate(2873, 3419, 0), new Coordinate(2878, 3415, 0));
    public final Area FLAX_AREA = new Area.Rectangular(new Coordinate(2881, 3474, 0), new Coordinate(2889, 3470, 0));

    public Stopwatch stopwatch;
    public LocalPlayer player;
    private int flaxPicked = 0;

    public final LeafNode TO_PICKING_STATE_LEAF = new LeafNode(this, "toPickingStateLeaf", () -> {
        setStatus("Switching to Picking State");
        setCurrentState(BotState.FLAX_PICKING.getDescription());
    });

    public final LeafNode TO_BANK_STATE_LEAF = new LeafNode(this, "toBankStateLeaf", () -> {
        setStatus("Switching to Bank State");
        setCurrentState(BotState.BANKING.getDescription());
    });

    public FlaxPickerPS() {

    }

    @Override
    public void onDraw(Workspace workspace) {
        super.onDraw(workspace);

        ImGui.begin(getInfo().name(), 0);
        ImGui.separatorText("Script Stats");
        ImGui.text("Flax picked: " + flaxPicked + " (" + BwuMath.getUnitsPerHour(stopwatch, flaxPicked) + " / hr)");
        ImGui.text("Runtime: " + Timer.secondsToFormattedString(stopwatch.elapsed() / 1000, DurationStringFormat.CLOCK));
        ImGui.end();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        try {
            // Create the states
            BankState bankState = new BankState(this, BotState.BANKING.getDescription());
            FlaxPickState flaxPickState = new FlaxPickState(this, BotState.FLAX_PICKING.getDescription());

            // Add them to the script using initStates - this sets the first state as current
            initStates(bankState, flaxPickState);

            // Force set status to confirm state is working
            setStatus("Script initialized with state: " + getCurrentState().getName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean onPreTick() {
        player = LocalPlayer.self();
        return super.onPreTick() && player != null && player.isValid();
    }

    @EventInfo(type = InventoryEvent.class)
    public void onInventoryEvent(InventoryEvent event) {

        // New Item Acquired
        if (event.oldItem().getId() <= -1 && event.newItem().getId() > -1) {
            if (event.newItem().getName().contains("Flax")) {
                flaxPicked += event.newItem().getQuantity();
            }
        }
    }

}