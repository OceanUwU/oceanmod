package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import oceanmod.DiscordClient;

@SpirePatch(
    clz=AbstractDungeon.class,
    method="nextRoomTransition",
    paramtypez={
        SaveFile.class
    }
)
public class NextFloor {
    @SpirePostfixPatch
    public static void Postfix() {
        DiscordClient.FloorInfo();
    }
}