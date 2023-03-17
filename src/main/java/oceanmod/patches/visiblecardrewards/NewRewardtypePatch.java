package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class NewRewardtypePatch {
    @SpireEnum
    public static RewardItem.RewardType VCR_SINGLECARDREWARD;
    @SpireEnum
    public static RewardItem.RewardType VCR_BOWLREWARD;
}