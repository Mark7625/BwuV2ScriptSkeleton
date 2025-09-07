package botwithus.state;

import botwithus.FlaxPickerBWU;
import net.botwithus.rs3.entities.SceneObject;
import net.botwithus.rs3.world.Distance;
import net.botwithus.util.Rand;
import net.botwithus.xapi.game.inventory.Backpack;
import net.botwithus.xapi.game.traversal.Traverse;
import net.botwithus.xapi.query.SceneObjectQuery;
import net.botwithus.xapi.script.permissive.Interlock;
import net.botwithus.xapi.script.permissive.Permissive;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.node.Branch;
import net.botwithus.xapi.script.permissive.node.LeafNode;

public class FlaxPickState extends PermissiveScript.State {
    private FlaxPickerBWU script;

    // Define branches and leaf nodes for the FlaxPickState
    private Branch isBackpackFull;
    private Branch shouldPickFlax;
    private LeafNode toBankStateLeaf;
    private LeafNode pickFlaxLeaf;
    private LeafNode traverseToFlaxAreaLeaf;

    private SceneObject flaxObj;

    private final int INTERACT_DISTANCE = 10, NEARBY_DISTANCE = 20;

    public FlaxPickState(FlaxPickerBWU script, String name) {
        super(name);
        this.script = script;
        initializeNodes();
    }

    // Initialize the nodes for the BankState
    @Override
    public void initializeNodes() {

        // Initialize Branches
        isBackpackFull = new Branch(script, "isBackpackFull", new Interlock("isBackpackFull",
            new Permissive("backpackFull", Backpack::isFull)
        ));

        shouldPickFlax = new Branch(script, "shouldPickFlax", new Interlock("isFlaxObjNearby",
            new Permissive("flaxObjExists", () -> {
                flaxObj = SceneObjectQuery.newQuery().name("Flax").inside(script.FLAX_AREA).results().nearest();
                return flaxObj != null;
            }),
            new Permissive("flaxNearby", () -> Distance.to(script.FLAX_AREA) < INTERACT_DISTANCE),
            new Permissive("playerIdleAnim", () -> script.player.getAnimationId() == -1)
        ));


        // Initialize Leaf Nodes
        toBankStateLeaf = script.TO_BANK_STATE_LEAF;

        pickFlaxLeaf = new LeafNode(script, "pickFlaxLeaf", () -> {
            script.setStatus("Picking flax");
            final var currentBackpackCount = Backpack.getItems().size();
            if (currentBackpackCount < 28 && flaxObj.interact("Pick") > 0) {
                script.delayUntil(() -> {
                    var newCount = Backpack.getItems().size();
                    return !script.player.isMoving() && script.player.getAnimationId() == -1 && currentBackpackCount != newCount;
                }, Rand.nextInt(6, 9));
            } else {
                script.warn("Failed to interact with flax object");
                script.delay(5);
            }
        });

        traverseToFlaxAreaLeaf = new LeafNode(script, "traverseToFlaxAreaLeaf", () -> {
            if (script.FLAX_AREA.contains(script.player)) {
                script.setStatus("Already in flax area, waiting for pick process to complete.");
            } else {
                script.setStatus("Traversing to flax area");
                if (Traverse.to(script.FLAX_AREA.getRandomCoordinate())) {
                    script.delayUntil(() -> Distance.to(script.FLAX_AREA) < NEARBY_DISTANCE, Rand.nextInt(10, 14));
                } else {
                    script.warn("Failed to traverse to flax area");
                    script.delay(5);
                }
            }
        });


        // Define Tree Traversal Structure
        isBackpackFull.setChildrenNodes(toBankStateLeaf, shouldPickFlax);
        shouldPickFlax.setChildrenNodes(pickFlaxLeaf, traverseToFlaxAreaLeaf);

        // Set your root node after initializing branches and leaf nodes
        setNode(isBackpackFull);

        script.println("FlaxPickState Initialized");
    }


}