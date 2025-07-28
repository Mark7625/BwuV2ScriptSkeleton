package botwithus;

import botwithus.state.BankState;
import botwithus.state.BotState;
import botwithus.state.FlaxPickState;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.scripts.Info;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.node.LeafNode;

@Info(name = "FlaxPicker", description = "FlaxPicker script", version = "1.0.0", author = "BotWithUs")
public class FlaxPicker extends PermissiveScript {
    public Area bankArea = new Area.Rectangular(new Coordinate(2872, 3420, 0), new Coordinate(2878, 3414, 0));
    public Area flaxArea = new Area.Rectangular(new Coordinate(2881, 3474, 0), new Coordinate(2889, 3470, 0));

    public LocalPlayer player;

    public final LeafNode TO_PICKING_STATE_LEAF = new LeafNode(this, "toPickingStateLeaf", () -> {
        setStatus("Switching to Picking State");
        setCurrentState(BotState.FLAX_PICKING.getDescription());
    });

    public final LeafNode TO_BANK_STATE_LEAF = new LeafNode(this, "toBankStateLeaf", () -> {
        setStatus("Switching to Bank State");
        setCurrentState(BotState.BANKING.getDescription());
    });

    @Override
    public boolean onPreTick() {
        player = LocalPlayer.self();
        return super.onPreTick() && player.isValid();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        initStates(
            new BankState(this, BotState.BANKING.getDescription()),
            new FlaxPickState(this, BotState.FLAX_PICKING.getDescription())
        );
    }
}