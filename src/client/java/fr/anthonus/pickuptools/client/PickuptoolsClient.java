package fr.anthonus.pickuptools.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PickuptoolsClient implements ClientModInitializer {
    public static final String MOD_ID = "pickuptools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Pickuptools initialized");
    }
}
