package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.mod.stslib.relics.CardRewardSkipButtonRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import oceanmod.rewards.SingleCardReward;

import static oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_BOWLREWARD;

public class BowlPickup {
    private static boolean bowlPickedUp = false;
    private static AbstractRelic skipRelic;

    @SpirePatch(clz=AbstractRelic.class, method="onEquip")
    public static class BowlTrack {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof SingingBowl && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
                bowlPickedUp = true;
                skipRelic = __instance;
            } else if (Loader.isModLoaded("stslib") && CardRewardSkipButtonRelic.class.isAssignableFrom(__instance.getClass()))
                skipRelic = __instance;
        }
    }

    @SpirePatch(clz=CombatRewardScreen.class, method="rewardViewUpdate")
    public static class AddReward {
        public static void Postfix(CombatRewardScreen __instance) {
            if (skipRelic != null || bowlPickedUp) {
                int index = 0;
                for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
                    index++;
                    if (reward instanceof SingleCardReward) {
                        boolean hasHpReward = false;
                        for (SingleCardReward link : ((SingleCardReward)reward).cardLinks) {
                            if (link.type == VCR_BOWLREWARD && link.skipRelic.getClass().equals(skipRelic.getClass())) {
                                hasHpReward = true;
                                break;
                            }
                        }
                        if (hasHpReward) continue;
                        if (!((SingleCardReward)reward).isLast()) continue;
                        SingleCardReward skipReward;
                        if (bowlPickedUp)
                            skipReward = new SingleCardReward(SingingBowlButton.TEXT[2], ImageMaster.TP_HP, AbstractDungeon.player.getRelic("Singing Bowl"));
                        else
                            skipReward = new SingleCardReward(((CardRewardSkipButtonRelic)skipRelic).getButtonLabel(), ImageMaster.TICK, skipRelic);
                        skipReward.addCardLink((SingleCardReward)reward);
                        for (SingleCardReward link : ((SingleCardReward)reward).cardLinks) {
                            skipReward.addCardLink(link);
                        }
                        AbstractDungeon.combatRewardScreen.rewards.add(index, skipReward);
                        AbstractDungeon.combatRewardScreen.positionRewards();
                        Postfix(__instance);
                        bowlPickedUp = false;
                        skipRelic = null;
                        return;
                    }
                }
                bowlPickedUp = false;
                skipRelic = null;
            }
        }
    }
}