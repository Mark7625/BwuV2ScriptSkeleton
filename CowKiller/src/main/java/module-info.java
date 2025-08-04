module CowKiller.main {
    requires kotlin.stdlib;
    requires transitive BotWithUs.api;
    requires BotWithUs.imgui;

    exports botwithus;
    exports botwithus.gui;

    provides net.botwithus.scripts.Script with botwithus.CowKiller;
}