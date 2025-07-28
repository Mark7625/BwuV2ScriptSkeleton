module FlaxPicker.main {
    requires BotWithUs.xapi;
    requires BotWithUs.api;

    exports botwithus;
    exports botwithus.state;

    provides net.botwithus.scripts.Script with botwithus.FlaxPicker;
}