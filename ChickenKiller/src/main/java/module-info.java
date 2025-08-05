module ChickenKiller.main {
    requires kotlin.stdlib;
    requires transitive BotWithUs.api;
    requires BotWithUs.imgui;


    provides net.botwithus.scripts.Script with botwithus.ChickenKiller;
}