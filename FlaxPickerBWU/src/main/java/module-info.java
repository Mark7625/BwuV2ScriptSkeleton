module BWU2VScriptsExample.FlaxPickerBWU.main {
    requires BotWithUs.api;
    requires BotWithUs.imgui;
    requires org.slf4j;
    requires static xapi;
    requires com.google.gson;

    opens botwithus to BotWithUs.api; // needed for event annotations

    provides net.botwithus.scripts.Script with botwithus.FlaxPickerBWU;
}