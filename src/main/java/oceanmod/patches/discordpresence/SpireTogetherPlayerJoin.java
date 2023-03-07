package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import oceanmod.DiscordClient;
import spireTogether.network.P2P.P2PCallbacks;
import spireTogether.util.SpireHelp;

@SpirePatch(
    clz=P2PCallbacks.class,
    method="OnPlayerRegistered",
    requiredModId="spireTogether"
)
public class SpireTogetherPlayerJoin {
    @SpirePostfixPatch
    public static void Postfix() {
        if (SpireHelp.Gameplay.IsInRun())
            DiscordClient.FloorInfo();
        else
            DiscordClient.SpireTogetherLobbyInfo();
    }
}