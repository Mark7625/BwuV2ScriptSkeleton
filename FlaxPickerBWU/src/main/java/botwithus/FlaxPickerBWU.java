package botwithus;

import botwithus.state.BankState;
import botwithus.state.BotState;
import botwithus.state.FlaxPickState;
import botwithus.ui.FlaxPickerUI;
import com.google.gson.JsonObject;
import net.botwithus.events.EventInfo;
import net.botwithus.imgui.ImGui;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.inventories.events.InventoryEvent;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.scripts.Info;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.xapi.script.BwuScript;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.node.LeafNode;
import net.botwithus.xapi.script.ui.interfaces.BuildableUI;
import net.botwithus.xapi.util.BwuMath;
import net.botwithus.xapi.util.time.DurationStringFormat;
import net.botwithus.xapi.util.time.Timer;

@Info(name = "FlaxPickerBWU", description = "FlaxPickerBWU script", version = "1.0.0", author = "BotWithUs")
public class FlaxPickerBWU extends BwuScript {
    private FlaxPickerUI ui;

    public final Area BANK_AREA = new Area.Rectangular(new Coordinate(2873, 3419, 0), new Coordinate(2878, 3415, 0));
    public final Area FLAX_AREA = new Area.Rectangular(new Coordinate(2881, 3474, 0), new Coordinate(2889, 3470, 0));

    private int flaxPicked = 0, lifetimePicked = 0;

    public final LeafNode TO_PICKING_STATE_LEAF = new LeafNode(this, "toPickingStateLeaf", () -> {
        setStatus("Switching to Picking State");
        setCurrentState(BotState.FLAX_PICKING.getDescription());
    });

    public final LeafNode TO_BANK_STATE_LEAF = new LeafNode(this, "toBankStateLeaf", () -> {
        setStatus("Switching to Bank State");
        setCurrentState(BotState.BANKING.getDescription());
    });

    public FlaxPickerBWU() {
        setDebugMode(true);
    }

    @Override
    public void onDrawConfig(Workspace workspace) {
        if (ui == null)
            ui = new FlaxPickerUI(this);

        ui.buildUI();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        try {
            botStatInfo.displayInfoMap.put("Flax Picked", flaxPicked + " (" + BwuMath.getUnitsPerHour(STOPWATCH, flaxPicked) + " / hr");

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
    public BuildableUI getBuildableUI() {
        return null;
    }

    @Override
    public void loadPersistentData(JsonObject jsonObject) {
        lifetimePicked = jsonObject.has("lifetimePicked") ? jsonObject.get("lifetimePicked").getAsInt() : 0;
    }

    @Override
    public void savePersistentData(JsonObject jsonObject) {
        jsonObject.addProperty("lifetimePicked", lifetimePicked);
    }

    @Override
    protected void onItemAcquired(InventoryEvent event) {
        if (event.newItem().getName().contains("Flax")) {
            flaxPicked += event.newItem().getQuantity();
            lifetimePicked += event.newItem().getQuantity();
        }
    }
}