module FlaxPicker.main {
    requires kotlin.stdlib;
    requires transitive BotWithUs.api;
    requires BotWithUs.xapi;
    requires BotWithUs.imgui;
    requires java.base;

    exports botwithus;

    provides net.botwithus.scripts.Script with botwithus.FlaxPicker;
}