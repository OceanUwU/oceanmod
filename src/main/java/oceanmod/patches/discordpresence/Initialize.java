package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordEventHandlers;

@SpireInitializer
public class Initialize {
    private static String CLIENT_ID = "971123328708411413";

    public static void initialize() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();
        DiscordRPC.discordInitialize(CLIENT_ID, handlers, true);
    }
}