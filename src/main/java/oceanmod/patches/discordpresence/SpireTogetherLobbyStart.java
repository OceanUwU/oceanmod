package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import oceanmod.DiscordClient;
import spireTogether.screens.lobby.MPLobbyScreen;

@SpirePatch(
    clz=MPLobbyScreen.class,
    method="init",
    requiredModId="spireTogether"
)
public class SpireTogetherLobbyStart {
    @SpirePostfixPatch
    public static void Postfix() {
        DiscordClient.SpireTogetherLobbyInfo();
    }
}