package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.relics.Cauldron;
import com.megacrit.cardcrawl.rewards.RewardItem;
import oceanmod.rewards.SingleCardReward;

import java.util.ArrayList;

public class HiddenCardRewards {
    @SpirePatch(clz=NeowReward.class, method="activate")
    public static class NeowPotions {
        public static void Postfix(NeowReward __instance) {
            if (__instance.type == NeowReward.NeowRewardType.THREE_SMALL_POTIONS)
                hideCardReward();
        }
    }

    @SpirePatch(clz=Cauldron.class, method="onEquip")
    public static class CauldronPatch {
        public static void Postfix(Cauldron __instance) {
            hideCardReward();
        }
    }

    public static void hideCardReward() {
        ArrayList<SingleCardReward> toRemove = new ArrayList<>();
        for (int i = 0; i < AbstractDungeon.combatRewardScreen.rewards.size(); i++) {
            RewardItem r = AbstractDungeon.combatRewardScreen.rewards.get(i);
            if (r instanceof SingleCardReward)
                toRemove.add((SingleCardReward)r);
        }
        AbstractDungeon.combatRewardScreen.rewards.removeAll(toRemove);
    }
}