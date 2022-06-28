package me.seren

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Mod : ModInitializer {
    // private val MOD_ID = "fabric-mod
    private val logger: Logger = LogManager.getLogger()

    override fun onInitialize() {
        logger.info("Example mod has been initialized.")
    }
}
