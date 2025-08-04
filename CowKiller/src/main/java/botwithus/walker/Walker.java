package botwithus.walker;

import java.util.function.Consumer;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.world.Coordinate;


/**
 * Handles walking and movement operations with Bresenham pathfinding
 * and coordinate API conversion utilities.
 */
public class Walker {

    private static Consumer<String> logger = System.out::println;
    public static void setLogger(Consumer<String> logger) {
        Walker.logger = logger;
    }

    /**
     * Gets the X coordinate from a Coordinate object
     * @param coordinate The coordinate object
     * @return X coordinate value
     */
    public static int getX(Coordinate coordinate) {
        return coordinate.x();
    }

    /**
     * Gets the Y coordinate from a Coordinate object
     * @param coordinate The coordinate object
     * @return Y coordinate value
     */
    public static int getY(Coordinate coordinate) {
        return coordinate.y();
    }

    /**
     * Gets the Z coordinate from a Coordinate object
     * @param coordinate The coordinate object
     * @return Z coordinate value
     */
    public static int getZ(Coordinate coordinate) {
        return coordinate.z();
    }

    /**
     * Walks to a coordinate using Bresenham line algorithm for pathfinding
     * @param coordinate The destination coordinate
     * @param minimap Whether to use minimap for walking (currently ignored, uses MiniMenu)
     * @param stepSize Maximum step size for each movement
     * @return true if walking was initiated successfully
     */
    public static boolean bresenhamWalkTo(Coordinate coordinate, boolean minimap, int stepSize) {
        LocalPlayer player = LocalPlayer.self();
        if (player == null) {
            logger.accept("MoveTo | Player is null");
            return false;
        }

        Coordinate currentCoordinate = player.getCoordinate();
        if (currentCoordinate == null) {
            logger.accept("MoveTo | Current coordinate is null");
            return false;
        }

        int dx = getX(coordinate) - getX(currentCoordinate);
        int dy = getY(coordinate) - getY(currentCoordinate);
        int distance = (int)Math.hypot(dx, dy);

        if (distance > stepSize) {
            int stepX = getX(currentCoordinate) + dx * stepSize / distance;
            int stepY = getY(currentCoordinate) + dy * stepSize / distance;
            return walkTo(stepX, stepY, minimap);
        } else {
            return walkTo(getX(coordinate), getY(coordinate), minimap);
        }
    }

    /**
     * Basic walk to coordinate using MiniMenu
     * @param x X coordinate
     * @param y Y coordinate
     * @param minimap Whether to use minimap (currently ignored)
     * @return true if walking was initiated successfully
     */
    public static boolean walkTo(int x, int y, boolean minimap) {
        try {
            logger.accept("Attempting to walk to " + x + ", " + y);

            if (Math.abs(x) > 10000 || Math.abs(y) > 10000) {
                logger.accept("ERROR: Invalid coordinates: " + x + ", " + y);
                return false;
            }

            LocalPlayer player = LocalPlayer.self();
            if (player != null && player.getCoordinate() != null) {
                Coordinate currentPos = player.getCoordinate();
                int currentX = getX(currentPos);
                int currentY = getY(currentPos);
                double distance = Math.hypot(x - currentX, y - currentY);

                if (distance < 2) {
                    logger.accept("Already close to target location, skipping walk");
                    return true;
                }
            }

            logger.accept("Calling MiniMenu.doAction(WALK, 0, " + x + ", " + y + ")");

            int result = MiniMenu.doAction(Action.WALK, 0, x, y);

            logger.accept("MiniMenu.doAction returned: " + result);

            if (result > 0) {
                logger.accept("Successfully initiated walk to " + x + ", " + y);
                return true;
            } else {
                logger.accept("Failed to walk to " + x + ", " + y + " - result: " + result);
                return false;
            }
        } catch (Exception e) {
            logger.accept("ERROR: Exception while walking to " + x + ", " + y + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the distance between two coordinates
     * @param from Source coordinate
     * @param to Destination coordinate
     * @return Distance between coordinates
     */
    public static double getDistance(Coordinate from, Coordinate to) {
        int dx = getX(to) - getX(from);
        int dy = getY(to) - getY(from);
        return Math.hypot(dx, dy);
    }

    /**
     * Walks directly to a coordinate using Bresenham line algorithm for pathfinding
     * @param coordinate The destination coordinate
     * @param minimap Whether to use minimap for walking (currently ignored, uses MiniMenu)
     * @param stepSize Maximum step size for each movement
     * @return true if walking was initiated successfully
     */
    public static boolean walkToCoordinate(Coordinate coordinate, boolean minimap, int stepSize) {
        return bresenhamWalkTo(coordinate, minimap, stepSize);
    }
}
