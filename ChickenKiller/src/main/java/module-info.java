module CowKiller.main {
    requires kotlin.stdlib;
    requires transitive BotWithUs.api;
    requires BotWithUs.imgui;

    exports botwithus;
    exports botwithus.gui;
    exports botwithus.combat;
    exports botwithus.loot;
    exports botwithus.movement;
    exports botwithus.banking;

    provides net.botwithus.scripts.Script with botwithus.ChickenKiller;
}