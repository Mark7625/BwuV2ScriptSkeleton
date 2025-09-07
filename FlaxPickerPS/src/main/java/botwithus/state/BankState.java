package botwithus.state;

import botwithus.FlaxPickerPS;
import net.botwithus.rs3.world.Distance;
import net.botwithus.util.Rand;
import net.botwithus.xapi.game.inventory.Backpack;
import net.botwithus.xapi.game.inventory.Bank;
import net.botwithus.xapi.game.traversal.Traverse;
import net.botwithus.xapi.query.ComponentQuery;
import net.botwithus.xapi.script.permissive.Interlock;
import net.botwithus.xapi.script.permissive.Permissive;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.node.Branch;
import net.botwithus.xapi.script.permissive.node.LeafNode;

public class BankState extends PermissiveScript.State {
    private FlaxPickerPS script;

    // Define branches and leaf nodes for the BankState
    private Branch isBackpackFull;
    private Branch isBankOpen;
    private Branch isNearBank;
    private LeafNode toPickingStateLeaf;
    private LeafNode depositBackpackLeaf;
    private LeafNode openBankLeaf;
    private LeafNode traverseToBankLeaf;

    private final int INTERACT_DISTANCE = 10, NEARBY_DISTANCE = 20;

    public BankState(FlaxPickerPS script, String name) {
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

        isBankOpen = new Branch(script, "isBankOpen", new Interlock("isBankOpen",
            new Permissive("bankOpen", Bank::isOpen)
        ));

        isNearBank = new Branch(script, "isNearBank", new Interlock("isNearBank",
            new Permissive("bankNearby", () -> Distance.to(script.BANK_AREA) < NEARBY_DISTANCE)
        ));


        // Initialize Leaf Nodes
        toPickingStateLeaf = script.TO_PICKING_STATE_LEAF;

        openBankLeaf = new LeafNode(script, "openBankLeaf", () -> {
            script.setStatus("Opening bank");
            if (Bank.open(script)) {
                script.delayUntil(Bank::isOpen, Rand.nextInt(10, 14));
            } else {
                script.debug("Failed to open bank");
                script.delay(6);
            }
        });

        depositBackpackLeaf = new LeafNode(script, "depositBackpackLeaf", () -> {
            script.setStatus("Depositing items in bank");
            if (Bank.depositAll()) {
                script.delayUntil(Backpack::isEmpty, Rand.nextInt(10, 14));
            } else {
                script.debug("Failed to deposit items in bank");
            }
        });

        traverseToBankLeaf = new LeafNode(script, "traverseToBankLeaf", () -> {
            script.setStatus("Traversing to bank");
            if (Traverse.to(script.BANK_AREA.getRandomCoordinate())) {
                script.delayUntil(() -> Distance.to(script.BANK_AREA) < INTERACT_DISTANCE, Rand.nextInt(10, 14));
            } else {
                script.warn("Failed to traverse to bank");
                script.delay(5);
            }
        });

        // Define Tree Traversal Structure
        isBackpackFull.setChildrenNodes(isBankOpen, toPickingStateLeaf);
        isBankOpen.setChildrenNodes(depositBackpackLeaf, isNearBank);
        isNearBank.setChildrenNodes(openBankLeaf, traverseToBankLeaf);

        // Set your root node after initializing branches and leaf nodes
        setNode(isBackpackFull);
    }
}