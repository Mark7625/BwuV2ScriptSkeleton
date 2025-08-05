package botwithus;

import botwithus.gui.ChickenKillerGUI;
import botwithus.areas.GameAreas;
import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.entities.*;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.rs3.world.World;
import net.botwithus.scripts.Info;
import net.botwithus.scripts.Script;
import net.botwithus.ui.workspace.Workspace;

import java.util.Collection;
import java.util.Comparator;

@Info(name = "CoaezChickenKiller", description = "ChickenKiller script", version = "1.0.0", author = "coaez")
public class ChickenKiller extends Script {

    // Inventory interface ID
    private static final int BACKPACK_INVENTORY_ID = 93;
    // Boolean to indicate if we are banking or not
    private boolean bankingEnabled = true;
    // Timestamp of last chicken interaction to prevent spam attacking
    private long lastChickenInteractionTime = 0;
    // Timestamp of last ground item pickup to prevent spam
    private long lastGroundItemPickupTime = 0;
    // Delay in milliseconds before attacking another chicken (5 seconds)
    private static final long CHICKEN_ATTACK_DELAY = 5000;
    // Delay in milliseconds before picking up another ground item (1.2 seconds)
    private static final long GROUND_ITEM_PICKUP_DELAY = 1200;

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

            //Check player health and go to bank if health is low
            if (player.getHealth() < 100) {
                println("Player health is low. Going to bank to heal.");
                // Check if we are already in the bank area or moving
                if (!GameAreas.BURTHORPE_BANK_AREA.contains(LocalPlayer.self())) {
                    println("Moving to bank...");
                    // Use the Walker from custom api to move to the bank location
                    int result = MiniMenu.doAction(Action.WALK, 0, GameAreas.BURTHORPE_BANK_LOCATION.x(),
                            GameAreas.BURTHORPE_BANK_LOCATION.y());
                    return;
                }
                return;
            }

            // Bank on full backpack and if banking is enabled
            if (isBackpackFull() && bankingEnabled) {
                println("Backpack is full and banking is enabled. We should go to bank and drop off our loot.");

                // Check if we are already in the bank area or moving
                if (!GameAreas.BURTHORPE_BANK_AREA.contains(LocalPlayer.self())) {
                    println("Moving to bank...");
                    int result = MiniMenu.doAction(Action.WALK, 0, GameAreas.BURTHORPE_BANK_LOCATION.x(),
                            GameAreas.BURTHORPE_BANK_LOCATION.y());
                    return;
                } else {
                    println("Already at bank area. Proceeding to bank.");
                    Collection<PathingEntity> banker = World.getNpcs();
                    println("Total NPCs found: " + (banker != null ? banker.size() : "null"));

                    if (banker != null && !banker.isEmpty()) {
                        // Log all NPCs in the area for debugging
                        banker.stream()
                                .filter(GameAreas.BURTHORPE_BANK_AREA::contains)
                                .forEach(npc -> println("NPC in bank area: " + npc.getName() + " at " + npc.getCoordinate()));

                        if (!player.isMoving()) {
                            println("Player is not moving, looking for banker...");
                            banker.stream()
                                    .filter(npc -> {
                                        boolean nameMatch = npc.getName().equalsIgnoreCase("Gnome Banker");
                                        println("Checking NPC: " + npc.getName() + " - Name matches: " + nameMatch);
                                        return nameMatch;
                                    })
                                    .filter(npc -> {
                                        boolean inArea = GameAreas.BURTHORPE_BANK_AREA.contains(npc);
                                        println("NPC " + npc.getName() + " in bank area: " + inArea + " at " + npc.getCoordinate());
                                        return inArea;
                                    })
                                    .findFirst()
                                    .ifPresentOrElse(npc -> {
                                        println("Found banker: " + npc.getName());
                                        int result = npc.interact("Load Last Preset from");
                                        println("Banker interaction result: " + result);
                                    }, () -> {
                                        println("No banker found matching criteria!");
                                    });
                        } else {
                            println("Player is moving, waiting...");
                        }
                    } else {
                        println("No NPCs found or collection is empty!");
                    }
                }

                return;
            }

            if (GameAreas.BURTHORPE_BANK_AREA.contains(LocalPlayer.self())) {
                // Check if health is full
                if (player.getHealth() < player.getMaxHealth()) {
                    println("At bank but health not full (" + player.getHealth() + "/" + player.getMaxHealth() + "). Waiting for health to restore...");
                    return; // Stay at bank until health is full
                }
                println("Health is full (" + player.getHealth() + "/" + player.getMaxHealth() + "). Ready to continue.");
            }

            // Check if we are in the chicken area or moving
            if (!GameAreas.CHICKEN_AREA.contains(LocalPlayer.self()) && !player.isMoving()) {
                println("Moving to chicken area...");
                int result = MiniMenu.doAction(Action.WALK, 0, GameAreas.CHICKEN_AREA.getRandomCoordinate().x(), GameAreas.CHICKEN_AREA.getRandomCoordinate().y());
                return;
            }

            // If we are in the chicken area, first priority is picking up ground items
            if (GameAreas.CHICKEN_AREA.contains(LocalPlayer.self()) && !player.isMoving()) {
                // Pick up feathers and bones
                Collection<ItemStack> groundItems = World.getGroundItems();
                groundItems.stream()
                        .filter(GameAreas.CHICKEN_AREA::contains) // Filter to only include items in the chicken area
                        .filter(ItemStack::isValid) // Filter to only include valid ItemStacks
                        .filter(itemStack -> {
                            // Check if the ItemStack contains feathers or bones
                            return itemStack.getItems().stream()
                                    .anyMatch(item -> item.getName().equalsIgnoreCase("Feather") ||
                                                    item.getName().equalsIgnoreCase("Bones"));
                        })
                        .min(Comparator.comparingDouble(player::distanceTo)) // Find the nearest item
                        .ifPresent(itemStack -> {
                            // Find the specific item to pick up
                            itemStack.getItems().stream()
                                    .filter(item -> item.getName().equalsIgnoreCase("Feather") ||
                                                  item.getName().equalsIgnoreCase("Bones"))
                                    .findFirst()
                                    .ifPresent(item -> {
                                        if (System.currentTimeMillis() - lastGroundItemPickupTime >= GROUND_ITEM_PICKUP_DELAY) {
                                            println("Found ground item: " + item.getName() + " (quantity: " + item.getQuantity() + ")");
                                            int pickup = item.interact(2); // Pick up the item
                                            lastGroundItemPickupTime = System.currentTimeMillis(); // Update the last pickup time
                                            if (pickup != 0) {
                                                println("Sent pickup command for: " + item.getName() + " with result: " + pickup);
                                            }
                                        }
                                    });
                        });
            }

            // If we are in the chicken area, we can attack chickens unless we are already have a target
            if (LocalPlayer.self().getTargetType() != EntityType.NPC_ENTITY && GameAreas.CHICKEN_AREA.contains(LocalPlayer.self())) {
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
