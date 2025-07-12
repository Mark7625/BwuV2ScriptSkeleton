module FlaxPicker.main {
    requires BotWithUs.api;
    requires BotWithUs.imgui;    
    exports botwithus;
    provides net.botwithus.scripts.Script with botwithus.FlaxPicker;
}