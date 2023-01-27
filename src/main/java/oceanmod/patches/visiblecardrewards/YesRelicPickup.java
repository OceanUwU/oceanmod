package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import oceanmod.rewards.SingleCardReward;

import java.util.Iterator;

import static oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_SINGLECARDREWARD;

@SpirePatch(clz=AbstractRelic.class, method="onEquip")
public class YesRelicPickup {
    public static void Postfix(AbstractRelic __instance) {
        if (__instance.relicId == "YesRelic") {
            for (Iterator<RewardItem> i = AbstractDungeon.combatRewardScreen.rewards.iterator(); i.hasNext(); ) {
                RewardItem reward = i.next();
                if (reward.type == VCR_SINGLECARDREWARD) {
                    for (Iterator<SingleCardReward> j = ((SingleCardReward)reward).cardLinks.iterator(); j.hasNext(); ) {
                        SingleCardReward link = j.next();
                        if (link.type == VCR_SINGLECARDREWARD) {
                            j.remove();
                            link.cardLinks.remove(reward);
                        }
                    }
                }
            }
        }
    }
}