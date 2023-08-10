package oceanmod.patches.visiblecardrewards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rewards.RewardItem;
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
        SingleCardReward.rewardsToRemove.clear();
        __instance.rewards.addAll(SingleCardReward.rewardsToAdd);
        SingleCardReward.rewardsToAdd.clear();
        for (RewardItem r : __instance.rewards) {
            if (r.hb.hovered && Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                r.hb.hovered = false;
                ReplaceCardRewards.replaceReward(r);
                CardCrawlGame.sound.playV("CARD_OBTAIN", 0.4F);
                break;
            }
        }
        __instance.positionRewards();
        try {
            setLabelMethod.invoke(__instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}