package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import oceanmod.rewards.SingleCardReward;

import java.lang.reflect.Method;

@SpirePatch(clz=CombatRewardScreen.class, method="rewardViewUpdate")
public class RemoveLinkedCards {
    private static Method setLabelMethod;
    
    static {
        try {
            setLabelMethod = CombatRewardScreen.class.getDeclaredMethod("setLabel");
            setLabelMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Postfix(CombatRewardScreen __instance) {
        __instance.rewards.removeAll(SingleCardReward.rewardsToRemove);
        __instance.positionRewards();
        try {
            setLabelMethod.invoke(__instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}