package botwithus.movement;

import botwithus.navigation.api.NavPath;
import botwithus.navigation.api.State;
import net.botwithus.rs3.world.Coordinate;

public class Movement {
    
    private NavPath navPath;

    public void traverse(NavPath path) {
        if (navPath != null)
            return;
        navPath = path;
        println("Traversing navPath: from: " + path.getStart() + ", to: " + path.getDestination());
    }

    public boolean processNavPath() {
        if (navPath == null)
            return false;
        State state = navPath.state();
        if (state != State.CONTINUE) {
            navPath = null;
            println("Nav path finished with state: " + state);
            return true;
        }
        navPath.process();
        return true;
    }

    public void resetNavPath() {
        if (navPath == null)
            return;
        navPath = null;
        println("Nav path reset.");
    }

    public void navigateToTile(Coordinate toTile, boolean enableSurge, boolean disableTeleports, boolean enableDive) {
        int flags = 0;
        if (enableSurge)
            flags |= NavPath.ENABLE_SURGE;
        if (disableTeleports)
            flags |= NavPath.DISABLE_TELEPORTS;
        if (!enableDive)
            flags |= NavPath.DISABLE_DIVE;
        NavPath path = NavPath.resolve(toTile, flags);
        traverse(path);
    }

    public boolean hasActiveNavPath() {
        return navPath != null;
    }

    private void println(String message) {
        System.out.println(message);
    }
}