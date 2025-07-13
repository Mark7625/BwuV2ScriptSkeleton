package botwithus

import net.botwithus.rs3.world.ClientState
import net.botwithus.rs3.world.World
import net.botwithus.scripts.Info
import net.botwithus.scripts.Script

/**
 * A barebone skeleton script example in Kotlin.
 * This demonstrates the minimal structure needed for a BotWithUs script using Kotlin.
 */
@Info(name = "ScriptSkeletonKotlin", description = "A skeleton script example in Kotlin", version = "1.0.0", author = "YourName")
class ScriptSkeletonKotlin : Script() {

    override fun run() {
        // Your script logic goes here
        println("Script skeleton (Kotlin) is running!")

    }
} 