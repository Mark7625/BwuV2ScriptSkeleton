package botwithus;

import java.util.Collection;
import java.util.Comparator;

import botwithus.gui.ChickenKillerGUI;
import botwithus.areas.GameAreas;
import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.rs3.world.World;
import net.botwithus.scripts.Info;
import net.botwithus.scripts.Script;
import net.botwithus.ui.workspace.Workspace;

@Info(name = "CoaezChickenKiller", description = "ChickenKiller script", version = "1.0.0", author = "coaez")
public class ChickenKiller extends Script {

    // Inventory interface ID
    private static final int BACKPACK_INVENTORY_ID = 93;
    // Boolean to indicate if we are banking or not
    private boolean bankingEnabled = true;
    // Timestamp of last chicken interaction to prevent spam attacking
    private long lastChickenInteractionTime = 0;
    // Delay in milliseconds before attacking another chicken (5 seconds)
    private static final long CHICKEN_ATTACK_DELAY = 5000;

    private final ChickenKillerGUI chickenKillerGUI;

    public ChickenKiller() {
        this.chickenKillerGUI = new ChickenKillerGUI(this);
    }

    @Override
    public void run() {
        try {
            LocalPlayer player = LocalPlayer.self();
            if (Client.getClientState() != ClientState.GAME) {
                println("Client is not in game state. Please log in to start the script.");
                return;
            }

            // Bank on full backpack and if banking is enabled
            if (isBackpackFull() && bankingEnabled) {
                println("Backpack is full and banking is enabled. We should go to bank and drop off our loot.");

                // Check if we are already in the bank area or moving
                if (!GameAreas.BURTHORPE_BANK_AREA.contains(LocalPlayer.self()) && !player.isMoving()) {
                    println("Moving to bank...");
                    // Use the Walker from custom api to move to the bank location
                    int result = MiniMenu.doAction(Action.WALK, 0, GameAreas.BURTHORPE_BANK_LOCATION.x(),
                            GameAreas.BURTHORPE_BANK_LOCATION.y());
                    return;
                }

                Collection<PathingEntity> banker = World.getNpcs();
                if (!banker.isEmpty() && !player.isMoving()) {
                    banker.stream()
                            .filter(npc -> npc.getName().equalsIgnoreCase("Banker"))
                            .filter(GameAreas.BURTHORPE_BANK_AREA::contains)
                            .findFirst()
                            .ifPresent(npc -> {
                                println("Found banker: " + npc.getName());
                                npc.interact("Load last preset from");
                            });
                }

                return;
            }

            // If we are here, we are not banking, so we can continue with killing chickens
            println("Continuing with chicken killing...");

            // Check if we are in the chicken area or moving
            if (!GameAreas.CHICKEN_AREA.contains(LocalPlayer.self()) && !player.isMoving()) {
                println("Moving to chicken area...");
                int result = MiniMenu.doAction(Action.WALK, 0, GameAreas.CHICKEN_AREA.getRandomCoordinate().x(), GameAreas.CHICKEN_AREA.getRandomCoordinate().y());
                return;
            }

            // If we are in the chicken area, we can attack chickens unless we are already have a target
            if (LocalPlayer.self().getTargetType() != EntityType.NPC_ENTITY) {
                println("We are in the chicken area, but not targeting a chicken. Let's find a chicken to attack.");
                // Get all NPCs in the world
                Collection<PathingEntity> chickens = World.getNpcs();
                // Filter the NPCs to find the nearest chicken that is valid, has health, and is not
                // already a target
                chickens.stream()
                        .filter(npc -> npc.getName().equalsIgnoreCase("Chicken"))
                        .filter(GameAreas.CHICKEN_AREA::contains) // Filter to only include NPCs in the chicken area
                        .filter(npc -> npc.getHealth() > 0) // Filter to only include NPCs with health greater than 0
                        .filter(Entity::isValid) // Filter to only include valid NPCs
                        .filter(npc -> npc.getFollowingType() != EntityType.NPC_ENTITY)
                        .min(Comparator.comparingDouble(player::distanceTo)) // Find the first (nearest) NPC
                        .ifPresent(npc -> {
                            println("Found nearest chicken: " + npc.getName());
                            long currentTime = System.currentTimeMillis();
                            // Check if the delay has passed since the last interaction
                            if (currentTime - lastChickenInteractionTime >= CHICKEN_ATTACK_DELAY) {
                                int attack = npc.interact("Attack"); // Attack the chicken
                                lastChickenInteractionTime = currentTime; // Update the last interaction time
                                if(attack != 0){
                                    println("Sent attack command to chicken: " + npc.getName() + " with result: " + attack + " distance: " + player.distanceTo(npc));
                                }
                            }
                        });
            }
        } catch (Exception e) {
            println("ERROR in ChickenKiller.run(): " + e.getMessage());
            e.printStackTrace();
            // Optionally stop the script on critical errors
            // this.stop();
        } catch (Throwable t) {
            println("CRITICAL ERROR in ChickenKiller.run(): " + t.getMessage());
            t.printStackTrace();
            // Stop script on critical errors
        }
    }

    /**
     * Checks if the backpack (inventory) is full.
     */
    public boolean isBackpackFull() {
        Inventory backpack = InventoryManager.getInventory(BACKPACK_INVENTORY_ID);
        return backpack != null && backpack.isFull();
    }

    /**
     * Get the current banking setting.
     * 
     * @return true if banking is enabled, false otherwise
     */
    public boolean isBankingEnabled() {
        return bankingEnabled;
    }

    /**
     * Set the banking setting.
     * 
     * @param bankingEnabled true to enable banking, false to disable
     */
    public void setBankingEnabled(boolean bankingEnabled) {
        this.bankingEnabled = bankingEnabled;
    }

    @Override
    public void onDraw(Workspace workspace) {
        super.onDraw(workspace);
        chickenKillerGUI.render(workspace);
    }

    @Override
    public void onActivation() {
        super.onActivation();
        println("ChickenKiller activated.");
    }

    @Override
    public void onDeactivation() {
        super.onDeactivation();
        println("ChickenKiller deactivated.");
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        println("ChickenKiller initialized.");
    }

}
