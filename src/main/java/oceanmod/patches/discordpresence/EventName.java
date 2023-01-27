package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import oceanmod.DiscordClient;

@SpirePatch(
    clz=LocalizedStrings.class,
    method="getEventString"
)
public class EventName {
    @SpirePostfixPatch
    public static void Postfix() {
        DiscordClient.FloorInfo();
    }
}