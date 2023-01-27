package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import oceanmod.DiscordClient;

@SpirePatch(
    clz=EndTurnButton.class,
    method="enable"
)
public class TurnStart {
    @SpirePostfixPatch
    public static void Postfix() {
        DiscordClient.FloorInfo();
    }
}