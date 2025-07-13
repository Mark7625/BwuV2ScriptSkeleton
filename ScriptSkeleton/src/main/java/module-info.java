module ScriptSkeleton.main {
    requires kotlin.stdlib;
    requires BotWithUs.api;
    requires BotWithUs.imgui; 
       
    exports botwithus;
    
    provides net.botwithus.scripts.Script with botwithus.ScriptSkeleton;
}