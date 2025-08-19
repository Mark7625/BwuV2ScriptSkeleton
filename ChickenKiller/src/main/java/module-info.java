module ChickenKiller.main {
    requires BotWithUs.api;
    requires BotWithUs.imgui;
    requires org.slf4j;

    provides net.botwithus.scripts.Script with botwithus.ChickenKiller;
}