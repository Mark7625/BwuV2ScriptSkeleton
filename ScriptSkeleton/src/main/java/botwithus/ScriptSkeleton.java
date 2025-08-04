package botwithus;

import java.util.Collection;
import java.util.Comparator;

import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.World;
import net.botwithus.scripts.Info;
import net.botwithus.scripts.Script;

/**
 * A barebone skeleton script example in Java.
 * This demonstrates the minimal structure needed for a BotWithUs script using Java.
 */
@Info(name = "ScriptSkeletonJava", description = "A skeleton script example in Java", version = "1.0.0", author = "YourName")
public class ScriptSkeleton extends Script {

    // Define the cow area
    public static final Area COW_AREA = new Area.Polygonal(
            new Coordinate(2882, 3492, 0),
            new Coordinate(2889, 3492, 0),
            new Coordinate(2889, 3482, 0),
            new Coordinate(2881, 3482, 0),
            new Coordinate(2881, 3491, 0)
    );

    @Override
    public void run() {
        try {
            LocalPlayer player = LocalPlayer.self();
            if (Client.getClientState() != ClientState.GAME) {
                println("Client is not in game state. Please log in to start the script.");
                return;
            }

            println("Running cow killing logic...");

            // If we are in the cow area, we can attack cows unless we are already have a target
            if (LocalPlayer.self().getTargetType() != EntityType.NPC_ENTITY) {
                println("We are not targeting a cow. Let's find a cow to attack.");
                println("We are in the cow area, but not targeting a cow. Let's find a cow to attack.");
                // Get all NPCs in the world
               Collection<PathingEntity> cows = World.getNpcs();
                // Filter the NPCs to find the nearest cow that is valid, has health, and is not
               // already a target
                cows.stream()
                        .filter(npc -> npc.getName().equalsIgnoreCase("Cow"))
                        .filter(COW_AREA::contains) // Filter to only include NPCs in the cow area
                        .filter(npc -> npc.getHealth() > 0) // Filter to only include NPCs with health greater than 0
                        .filter(Entity::isValid) // Filter to only include valid NPCs
                        .filter(npc -> npc.getFollowingType() != EntityType.NPC_ENTITY)
                        .min(Comparator.comparingDouble(player::distanceTo)) // Find the first (nearest) NPC
                        .ifPresent(npc -> {
                            println("Found nearest cow: " + npc.getName());
                        });
            }
        } catch (Exception e) {
            println("ERROR in ScriptSkeleton.run(): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
