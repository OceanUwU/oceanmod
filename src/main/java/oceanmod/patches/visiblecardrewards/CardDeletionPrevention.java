package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class CardDeletionPrevention {
    @SpirePatch(clz=ShowCardAndObtainEffect.class, method=SpirePatch.CLASS)
    public static class FromSingleRewardField {
        public static SpireField<Boolean> single = new SpireField<>(() -> false);
    }

    @SpirePatch(clz=ShowCardAndObtainEffect.class, method="update")
    public static class StopLateAdd {
        @SpireInsertPatch(loc=106)
        public static SpireReturn<Void> Insert(ShowCardAndObtainEffect __instance) {
            if (FromSingleRewardField.single.get(__instance))
                return SpireReturn.Return();
            else
                return SpireReturn.Continue();
        }
    }
}