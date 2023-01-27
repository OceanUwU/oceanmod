package oceanmod.patches.discordpresence;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import oceanmod.DiscordClient;

import java.time.Instant;

@SpirePatch(
    clz=AbstractDungeon.class,
    method=SpirePatch.CONSTRUCTOR,
    paramtypez={
        String.class,
        AbstractPlayer.class,
        SaveFile.class,
    }
)
public class DungeonLoadSave {
    @SpirePostfixPatch
    public static void Postfix(AbstractDungeon d, String name, AbstractPlayer p, SaveFile saveFile) {
        DiscordClient.startTime = Instant.now().getEpochSecond() - saveFile.play_time;
        DiscordClient.FloorInfo();
    }
}