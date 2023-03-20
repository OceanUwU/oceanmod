package launchslot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(clz=CardCrawlGame.class, method="create")
public class ChangeProfile {
    public static int profileNum=0;

    @SpireInsertPatch(loc=231)
    public static void Insert(CardCrawlGame __instance) {
        CardCrawlGame.saveSlot = profileNum-1;
    }
}