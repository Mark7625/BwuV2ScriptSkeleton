package botwithus;

import botwithus.banking.Banking;
import botwithus.combat.Combat;
import botwithus.gui.ChickenKillerGUI;
import botwithus.loot.Looting;
import botwithus.areas.GameAreas;
import botwithus.movement.Movement;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.scripts.Info;
import net.botwithus.scripts.Script;
import net.botwithus.ui.workspace.Workspace;

@Info(name = "ChickenKiller", description = "ChickenKiller script", version = "1.0.0", author = "BotWithUs")
public class ChickenKiller extends Script {

    private boolean bankingEnabled = true;
    private final ChickenKillerGUI chickenKillerGUI;

    // components
    private final Banking banking;
    private final Combat combat;
    private final Looting looting;
    private final Movement movement;

    public ChickenKiller() {
        this.chickenKillerGUI = new ChickenKillerGUI(this);
        this.banking = new Banking(this::println);
        this.combat = new Combat(this::println);
        this.looting = new Looting(this::println);
        this.movement = new Movement();
    }

    @Override
    public void run() {
        try {
            if (Client.getClientState() != ClientState.GAME) {
                println("Client is not in game state. Please log in to start the script.");
                return;
            }

            // Handle low health - go to bank if needed
            if (banking.handleLowHealth()) {
                return;
            }

            // Handle banking when backpack is full
            if (bankingEnabled && banking.handleBanking()) {
                return;
            }

            // Handle health restoration at bank
            if (banking.handleHealthAtBank()) {
                return;
            }

            // Move to chicken area if not already there
            if (moveToChickenArea()) {
                return;
            }

            // Only proceed with combat/looting if ready
            if (!isReadyForCombat()) {
                return;
            }

            // Priority 1: Pick up ground items (feathers and bones)
            if (looting.pickupGroundItems()) {
                println("Picked up ground item");
                return;
            }

            // Priority 2: Attack chickens if no loot to pick up
            if (combat.attackChickens()) {
                println("Attacked chicken");
                return;
            }

            println("No actions available - waiting...");

        } catch (Exception e) {
            println("ERROR in ChickenKiller.run(): " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            println("CRITICAL ERROR in ChickenKiller.run(): " + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * Get the current banking setting.
     */
    public boolean isBankingEnabled() {
        return bankingEnabled;
    }

    /**
     * Set the banking setting.
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
        movement.resetNavPath();
        println("ChickenKiller deactivated.");
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        println("ChickenKiller initialized.");
    }

    /**
     * Handles movement to chicken area using Movement class
     * @return true if movement is in progress or needs to continue
     */
    private boolean moveToChickenArea() {
        LocalPlayer player = LocalPlayer.self();

        // If already in area, no movement needed
        if (GameAreas.CHICKEN_AREA.contains(player)) {
            if (movement.hasActiveNavPath()) {
                println("Reached chicken area!");
                movement.resetNavPath();
            }
            return false;
        }

        // Process any existing navigation
        if (movement.processNavPath()) {
            return true;
        }

        // If no active path, create one to chicken area
        println("Creating path to chicken area...");
        movement.navigateToTile(GameAreas.CHICKEN_AREA.getRandomCoordinate(), true, false, true);
        return true;
    }

    /**
     * Checks if player is ready for combat (in area and not moving)
     */
    private boolean isReadyForCombat() {
        LocalPlayer player = LocalPlayer.self();
        return GameAreas.CHICKEN_AREA.contains(player) && !movement.hasActiveNavPath() && !player.isMoving();
    }
}
