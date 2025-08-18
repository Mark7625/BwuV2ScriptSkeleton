module ChickenKiller.main {
    requires kotlin.stdlib;
    requires transitive BotWithUs.api;
    requires BotWithUs.imgui;
    requires static BotWithUs.navigation.api;

    provides net.botwithus.scripts.Script with botwithus.ChickenKiller;
}