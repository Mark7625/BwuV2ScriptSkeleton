module BWU2VScriptsExample.FlaxPickerPS.main {
    requires BotWithUs.api;
    requires BotWithUs.imgui;
    requires org.slf4j;
    requires static xapi;

    opens botwithus to BotWithUs.api; // needed for event annotations

    provides net.botwithus.scripts.Script with botwithus.FlaxPickerPS;
}