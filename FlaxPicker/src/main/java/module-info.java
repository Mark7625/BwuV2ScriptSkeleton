module FlaxPicker.main {
    requires transitive BotWithUs.xapi;
    requires BotWithUs.api;
    requires java.base;


    provides net.botwithus.scripts.Script with botwithus.FlaxPicker;
}