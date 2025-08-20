module BWU2VScriptsExample.FlaxPicker.main {
    requires BotWithUs.api;
    requires BotWithUs.imgui;
    requires org.slf4j;
    requires static xapi;

    provides net.botwithus.scripts.Script with botwithus.FlaxPicker;
}