package botwithus;

import net.botwithus.scripts.Info;
import net.botwithus.scripts.Script;

/**
 * A barebone skeleton script example in Java.
 * This demonstrates the minimal structure needed for a BotWithUs script using Java.
 */
@Info(name = "ScriptSkeletonJava", description = "A skeleton script example in Java", version = "1.0.0", author = "YourName")
public class ScriptSkeleton extends Script {

    @Override
    public void run() {
        // Your script logic goes here
        println("Script skeleton (Java) is running!");

    }
} 