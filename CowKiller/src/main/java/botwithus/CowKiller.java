package botwithus;

import java.util.Collection;
import java.util.Comparator;

import botwithus.script.TickBasedScript;
import botwithus.walker.Walker;
import botwithus.areas.GameAreas;
import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.entities.Entity;

import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.World;
import net.botwithus.scripts.Info;

@Info(name = "CoaezCowKiller", description = "CowKiller script", version = "1.0.0", author = "coaez")
public class CowKiller extends TickBasedScript {

    // Inventory interface ID
    private static final int BACKPACK_INVENTORY_ID = 93;
    // Boolean to indicate if we are banking or not
    private boolean bankingEnabled = true;

    @Override
    protected void onTick() {
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
                Walker.bresenhamWalkTo(GameAreas.BURTHORPE_BANK_LOCATION, false, 20);
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
            Walker.bresenhamWalkTo(GameAreas.COW_AREA.getRandomCoordinate(), false, 20);
            delayUntil(player::isMoving, 15);
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
                        npc.interact("Attack"); // Attack the nearest cow
                        delayTicks(5); // Delay for 5 ticks to avoid spamming
                    });
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
}
