package botwithus;

import java.util.Collection;

import botwithus.walker.Walker;
import net.botwithus.rs3.client.Client;
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
import net.botwithus.scripts.Script;

@Info(name = "CoaezCowKiller", description = "CowKiller script", version = "1.0.0", author = "coaez")
public class CowKiller extends Script {

    // Inventory interface ID
    private static final int BACKPACK_INVENTORY_ID = 93;

    private final Coordinate bankLocation = new Coordinate(2889, 3537, 0); // Example coordinates close to the bank in Burthorpe
    private final Area bankArea = new Area.Circular(bankLocation, 8); // Define a bank area with a radius of 8 tiles around the bank coordinates
    
    // Define polygonal area for cows for better precision
    public static final Area COW_AREA = new Area.Polygonal(
            new Coordinate(2882, 3492, 0),
            new Coordinate(2889, 3492, 0),
            new Coordinate(2889, 3482, 0),
            new Coordinate(2881, 3482, 0),
            new Coordinate(2881, 3491, 0)
    );
    
    private boolean bankingEnabled = true; // Flag to enable or disable banking


    @Override
    public void run() {
        LocalPlayer player = LocalPlayer.self();
        if(Client.getClientState() != ClientState.GAME) {
            println("Client is not in game state. Please log in to start the script.");
            return;
        }

        //Bank on full backpack and if banking is enabled
        if(isBackpackFull() && bankingEnabled) {
            println("Backpack is full and banking is enabled. We should go to bank and drop off our loot.");

            // Check if we are already in the bank area or moving
            if (!bankArea.contains(LocalPlayer.self()) && !player.isMoving()) {
                println("Moving to bank...");
                // Use the Walker from custom api to move to the bank location
                Walker.bresenhamWalkTo(bankLocation, false, 20);
            }

            Collection<PathingEntity> banker = World.getNpcs();
            if (!banker.isEmpty() && !player.isMoving()) {
                banker.stream()
                    .filter(npc -> npc.getName().equalsIgnoreCase("Banker"))
                    .filter(npc -> bankArea.contains(npc))
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
        if (!COW_AREA.contains(LocalPlayer.self()) && !player.isMoving()) {
            println("Moving to cow area...");
            Walker.bresenhamWalkTo(COW_AREA.getRandomCoordinate(), false, 20);
            return;
        } 

        // If we are in the cow area, we can attack cows unless we are already attacking one
        if (LocalPlayer.self().getTargetType() != EntityType.NPC_ENTITY && COW_AREA.contains(LocalPlayer.self())) {
            println("We are in the cow area, but not targeting a cow. Let's find a cow to attack.");
            // Get all NPCs in the world
            Collection<PathingEntity> cows = World.getNpcs();
            // Stream the NPCs and filter them to find the nearest cow
            cows.stream()
                .filter(npc -> npc.getName().equalsIgnoreCase("Cow")) 
                .filter(npc -> COW_AREA.contains(npc)) // Filter to only include NPCs in the cow area
                .filter(npc -> npc.getHealth() > 0) // Filter to only include NPCs with health greater than 0
                .filter(npc -> npc.isValid()) // Filter to only include valid NPCs
                .filter(npc -> npc.getFollowingType() != EntityType.NPC_ENTITY) // Filter to only include NPCs that are not following another NPC
                .sorted((npc1, npc2) -> Double.compare(player.distanceTo(npc1), player.distanceTo(npc2))) // Sort the NPCs by distance to the player
                .findFirst() // Find the first (nearest) NPC
                .ifPresent(npc -> {
                    println("Found nearest cow: " + npc.getName());
                    npc.interact("Attack"); // Attack the nearest cow
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
     * @return true if banking is enabled, false otherwise
     */
    public boolean isBankingEnabled() {
        return bankingEnabled;
    }
    
    /**
     * Set the banking setting.
     * @param bankingEnabled true to enable banking, false to disable
     */
    public void setBankingEnabled(boolean bankingEnabled) {
        this.bankingEnabled = bankingEnabled;
    }

}