package botwithus.movement;

import botwithus.areas.GameAreas;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;

import java.util.function.Consumer;

public class Movement {

    private final Consumer<String> logger;

    public Movement(Consumer<String> logger) {
        this.logger = logger;
    }

    /**
     * Moves the player to the chicken area if not already there
     * @return true if movement was initiated
     */
    public boolean moveToChickenArea() {
        LocalPlayer player = LocalPlayer.self();

        if (GameAreas.CHICKEN_AREA.contains(player) || player.isMoving()) {
            return false;
        }

        logger.accept("Moving to chicken area...");
        int result = MiniMenu.doAction(Action.WALK, 0,
                GameAreas.CHICKEN_AREA.getRandomCoordinate().x(),
                GameAreas.CHICKEN_AREA.getRandomCoordinate().y());

        return result != 0;
    }

    /**
     * Checks if player is ready for combat (in area and not moving)
     */
    public boolean isReadyForCombat() {
        LocalPlayer player = LocalPlayer.self();
        return GameAreas.CHICKEN_AREA.contains(player) && !player.isMoving();
    }
}
