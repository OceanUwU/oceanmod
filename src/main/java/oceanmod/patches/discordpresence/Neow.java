package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import oceanmod.DiscordClient;

import java.util.ArrayList;
import java.time.Instant;

@SpirePatch(
    clz=Exordium.class,
    method=SpirePatch.CONSTRUCTOR,
    paramtypez={
        AbstractPlayer.class,
        ArrayList.class
    }
)
public class Neow {
    @SpirePostfixPatch
    public static void Postfix() {
        DiscordClient.startTime = Instant.now().getEpochSecond();
        DiscordClient.FloorInfo();
    }
}