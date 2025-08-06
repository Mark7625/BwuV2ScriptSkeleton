package botwithus.combat;

import botwithus.areas.GameAreas;
import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.world.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public class Combat {

    private long lastChickenInteractionTime = 0;
    private static final long CHICKEN_ATTACK_DELAY = 5000;
    private final Consumer<String> logger;

    public Combat(Consumer<String> logger) {
        this.logger = logger;
    }

    /**
     * Attempts to attack chickens in the chicken area
     * @return true if an attack was attempted
     */
    public boolean attackChickens() {
        LocalPlayer player = LocalPlayer.self();

        // Only attack if player is not already targeting something and is in chicken area
        if (!GameAreas.CHICKEN_AREA.contains(player)) {
            return false;
        }

        // Check if enough time has passed since last attack
        if (System.currentTimeMillis() - lastChickenInteractionTime < CHICKEN_ATTACK_DELAY) {
            return false;
        }

        Collection<PathingEntity> chickens = World.getNpcs();
        return chickens.stream()
                .filter(npc -> npc.getName().equalsIgnoreCase("Chicken"))
                .filter(GameAreas.CHICKEN_AREA::contains)
                .filter(npc -> npc.getHealth() > 0)
                .filter(Entity::isValid)
                .filter(npc -> npc.getFollowingType() != EntityType.NPC_ENTITY)
                .min(Comparator.comparingDouble(player::distanceTo))
                .map(this::attackChicken)
                .orElse(false);
    }

    /**
     * Attacks a specific chicken
     * @return true if attack was attempted
     */
    private boolean attackChicken(PathingEntity chicken) {
        LocalPlayer player = LocalPlayer.self();
        logger.accept("Found nearest chicken: " + chicken.getName());

        int attack = chicken.interact("Attack");
        lastChickenInteractionTime = System.currentTimeMillis();

        if (attack != 0) {
            logger.accept("Sent attack command to chicken: " + chicken.getName() +
                             " with result: " + attack + " distance: " + player.distanceTo(chicken));
            return true;
        }
        return false;
    }
}
