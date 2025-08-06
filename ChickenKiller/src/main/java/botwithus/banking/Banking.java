package botwithus.banking;

import botwithus.areas.GameAreas;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.world.World;

import java.util.Collection;
import java.util.function.Consumer;

public class Banking {

    private static final int BACKPACK_INVENTORY_ID = 93;
    private final Consumer<String> logger;

    public Banking(Consumer<String> logger) {
        this.logger = logger;
    }

    /**
     * Checks if the backpack (inventory) is full
     */
    public boolean isBackpackFull() {
        Inventory backpack = InventoryManager.getInventory(BACKPACK_INVENTORY_ID);
        return backpack != null && backpack.isFull();
    }

    /**
     * Handles banking when backpack is full
     * @return true if banking action was taken or player should stay at bank
     */
    public boolean handleBanking() {
        LocalPlayer player = LocalPlayer.self();

        if (!isBackpackFull()) {
            return false;
        }

        logger.accept("Backpack is full. Going to bank.");

        // Move to bank if not already there
        if (!GameAreas.BURTHORPE_BANK_AREA.contains(player) && !player.isMoving()) {
            logger.accept("Moving to bank...");
            MiniMenu.doAction(Action.WALK, 0, GameAreas.BURTHORPE_BANK_LOCATION.x(),
                    GameAreas.BURTHORPE_BANK_LOCATION.y());
            return true;
        }

        // Use banker if at bank and not moving
        if (!player.isMoving()) {
            useBanker();
        }

        return true;
    }

    /**
     * Handles health checking and healing at bank
     * @return true if player should stay at bank for healing
     */
    public boolean handleHealthAtBank() {
        LocalPlayer player = LocalPlayer.self();

        if (!GameAreas.BURTHORPE_BANK_AREA.contains(player)) {
            return false;
        }

        // Check if health is full
        if (player.getHealth() < player.getMaxHealth()) {
            logger.accept("At bank but health not full (" + player.getHealth() + "/" +
                             player.getMaxHealth() + "). Waiting for health to restore...");
            return true;
        }

        logger.accept("Health is full (" + player.getHealth() + "/" +
                         player.getMaxHealth() + "). Ready to continue.");
        return false;
    }

    /**
     * Handles low health scenarios
     * @return true if player needs to go to bank for healing
     */
    public boolean handleLowHealth() {
        LocalPlayer player = LocalPlayer.self();

        if (player.getHealth() >= 100) {
            return false;
        }

        logger.accept("Player health is low (" + player.getHealth() + "). Going to bank to heal.");

        if (!GameAreas.BURTHORPE_BANK_AREA.contains(player)) {
            logger.accept("Moving to bank for healing...");
            MiniMenu.doAction(Action.WALK, 0, GameAreas.BURTHORPE_BANK_LOCATION.x(),
                    GameAreas.BURTHORPE_BANK_LOCATION.y());
        }

        return true;
    }

    /**
     * Attempts to use the banker NPC
     */
    private void useBanker() {
        Collection<PathingEntity> npcs = World.getNpcs();

        if (npcs == null || npcs.isEmpty()) {
            logger.accept("No NPCs found!");
            return;
        }

        npcs.stream()
                .filter(GameAreas.BURTHORPE_BANK_AREA::contains)
                .filter(npc -> npc.getName().equalsIgnoreCase("Gnome Banker"))
                .findFirst()
                .ifPresentOrElse(npc -> {
                    logger.accept("Found banker: " + npc.getName());
                    int result = npc.interact("Load Last Preset from");
                    logger.accept("Banker interaction result: " + result);
                }, () -> {
                    logger.accept("No banker found!");
                    // Debug log all NPCs in bank area
                    npcs.stream()
                            .filter(GameAreas.BURTHORPE_BANK_AREA::contains)
                            .forEach(npc -> logger.accept("NPC in bank area: " + npc.getName()));
                });
    }
}
