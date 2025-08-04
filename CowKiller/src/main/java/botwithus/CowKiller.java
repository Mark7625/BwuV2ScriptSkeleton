package botwithus;

import java.util.Collection;
import java.util.Comparator;

import botwithus.gui.CowKillerGUI;
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

@Info(name = "CoaezCowKiller", description = "CowKiller script", version = "1.0.0", author = "coaez")
public class CowKiller extends Script {

    // Inventory interface ID
    private static final int BACKPACK_INVENTORY_ID = 93;
    // Boolean to indicate if we are banking or not
    private boolean bankingEnabled = true;
    // Timestamp of last cow interaction to prevent spam attacking
    private long lastCowInteractionTime = 0;
    // Delay in milliseconds before attacking another cow (5 seconds)
    private static final long COW_ATTACK_DELAY = 5000;

    private final CowKillerGUI cowKillerGUI;

    public CowKiller() {
        this.cowKillerGUI = new CowKillerGUI(this);
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

            // If we are here, we are not banking, so we can continue with killing cows
            println("Continuing with cow killing...");

            // Check if we are in the cow area or moving
            if (!GameAreas.COW_AREA.contains(LocalPlayer.self()) && !player.isMoving()) {
                println("Moving to cow area...");
                int result = MiniMenu.doAction(Action.WALK, 0, GameAreas.COW_AREA.getRandomCoordinate().x(), GameAreas.COW_AREA.getRandomCoordinate().y());
                return;
            }

            // If we are in the cow area, we can attack cows unless we are already have a target
            if (LocalPlayer.self().getTargetType() != EntityType.NPC_ENTITY) {
                println("We are in the cow area, but not targeting a cow. Let's find a cow to attack.");
                // Get all NPCs in the world
                Collection<PathingEntity> cows = World.getNpcs();
                // Filter the NPCs to find the nearest cow that is valid, has health, and is not
                // already a target
                cows.stream()
                        .filter(npc -> npc.getName().equalsIgnoreCase("Cow"))
                        .filter(GameAreas.COW_AREA::contains) // Filter to only include NPCs in the cow area
                        .filter(npc -> npc.getHealth() > 0) // Filter to only include NPCs with health greater than 0
                        .filter(Entity::isValid) // Filter to only include valid NPCs
                        .filter(npc -> npc.getFollowingType() != EntityType.NPC_ENTITY)
                        .min(Comparator.comparingDouble(player::distanceTo)) // Find the first (nearest) NPC
                        .ifPresent(npc -> {
                            println("Found nearest cow: " + npc.getName());
                            long currentTime = System.currentTimeMillis();
                            // Check if the delay has passed since the last interaction
                            if (currentTime - lastCowInteractionTime >= COW_ATTACK_DELAY) {
                                int attack = npc.interact(2); // Attack the cow
                                lastCowInteractionTime = currentTime; // Update the last interaction time
                                if(attack != 0){
                                    println("Sent attack command to cow: " + npc.getName() + " with result: " + attack + " distance: " + player.distanceTo(npc));
                                }
                            } else {
                                println("Attack on cow: " + npc.getName() + " is on cooldown. Remaining time: " + ((COW_ATTACK_DELAY - (currentTime - lastCowInteractionTime)) / 1000) + " seconds.");
                            }
                        });
            }
        } catch (Exception e) {
            println("ERROR in CowKiller.run(): " + e.getMessage());
            e.printStackTrace();
            // Optionally stop the script on critical errors
            // this.stop();
        } catch (Throwable t) {
            println("CRITICAL ERROR in CowKiller.run(): " + t.getMessage());
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
        cowKillerGUI.render(workspace);
    }

    @Override
    public void onActivation() {
        super.onActivation();
        println("CowKiller activated.");
    }

    @Override
    public void onDeactivation() {
        super.onDeactivation();
        println("CowKiller deactivated.");
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        println("CowKiller initialized.");
    }

}
