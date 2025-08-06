package botwithus.loot;

import botwithus.areas.GameAreas;
import net.botwithus.rs3.entities.ItemStack;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.world.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public class Looting {

    private long lastGroundItemPickupTime = 0;
    private static final long GROUND_ITEM_PICKUP_DELAY = 1200;
    private final Consumer<String> logger;

    public Looting(Consumer<String> logger) {
        this.logger = logger;
    }

    /**
     * Attempts to pick up feathers and bones in the chicken area
     * @return true if an item was found and pickup was attempted
     */
    public boolean pickupGroundItems() {
        LocalPlayer player = LocalPlayer.self();

        // Only pick up items if player is in chicken area and not moving
        if (!GameAreas.CHICKEN_AREA.contains(player) || player.isMoving()) {
            return false;
        }

        // Check if enough time has passed since last pickup
        if (System.currentTimeMillis() - lastGroundItemPickupTime < GROUND_ITEM_PICKUP_DELAY) {
            return false;
        }

        Collection<ItemStack> groundItems = World.getGroundItems();
        return groundItems.stream()
                .filter(GameAreas.CHICKEN_AREA::contains)
                .filter(ItemStack::isValid)
                .filter(this::containsTargetItems)
                .min(Comparator.comparingDouble(player::distanceTo))
                .map(this::attemptPickup)
                .orElse(false);
    }

    /**
     * Checks if an ItemStack contains feathers or bones
     */
    private boolean containsTargetItems(ItemStack itemStack) {
        return itemStack.getItems().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase("Feather") ||
                                item.getName().equalsIgnoreCase("Bones"));
    }

    /**
     * Attempts to pick up the first feather or bones from an ItemStack
     * @return true if pickup was attempted
     */
    private boolean attemptPickup(ItemStack itemStack) {
        return itemStack.getItems().stream()
                .filter(item -> item.getName().equalsIgnoreCase("Feather") ||
                              item.getName().equalsIgnoreCase("Bones"))
                .findFirst()
                .map(item -> {
                    logger.accept("Found ground item: " + item.getName() + " (quantity: " + item.getQuantity() + ")");
                    int pickup = item.interact(2);
                    lastGroundItemPickupTime = System.currentTimeMillis();

                    if (pickup != 0) {
                        logger.accept("Sent pickup command for: " + item.getName() + " with result: " + pickup);
                        return true;
                    } else {
                        logger.accept("Failed to send pickup command for: " + item.getName());
                        return false;
                    }
                })
                .orElse(false);
    }
}
