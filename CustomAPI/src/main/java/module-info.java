module botwithus.walker.module {
    requires transitive BotWithUs.api;
    requires transitive BotWithUs.imgui;

    exports botwithus.walker;
    exports botwithus.ui;
    exports botwithus.script;
    exports botwithus.areas;
}