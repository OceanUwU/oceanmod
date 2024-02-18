package oceanmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import java.util.Map;
import oceanmod.OceanMod;

@SpirePatch(clz=PreservedInsect.class, method="atBattleStart")
public class PreservedEnemies {
    public static boolean enabled;
    private static Map<String, String> TEXT_DICT;

    @SpireInsertPatch(rloc=4, localvars={"m"})
    public static void Insert(PreservedInsect __instance, AbstractMonster m) {
        if (TEXT_DICT == null)
            TEXT_DICT = CardCrawlGame.languagePack.getUIString(OceanMod.ID + ":PreservedEnemies").TEXT_DICT;
        if (enabled && TEXT_DICT.containsKey(m.id))
            m.name = TEXT_DICT.get(m.id);
    }
}
