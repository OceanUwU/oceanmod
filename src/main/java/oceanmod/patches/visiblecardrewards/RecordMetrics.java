package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import oceanmod.rewards.SingleCardReward;

import static oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_SINGLECARDREWARD;

@SpirePatch(clz=ProceedButton.class, method="update")
public class RecordMetrics {
    @SpireInsertPatch(loc=200,localvars={"item"})
    public static void Insert(ProceedButton __instance, RewardItem item) {
        if (item.type == VCR_SINGLECARDREWARD)
            ((SingleCardReward)item).recordSkipMetrics();
    }
}