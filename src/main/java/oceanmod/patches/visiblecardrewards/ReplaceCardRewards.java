package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.mod.stslib.relics.CardRewardSkipButtonRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import pansTrinkets.patches.TrinketRewardTypePatch;
import pansTrinkets.rewards.TrinketReward;
import oceanmod.OceanMod;
import oceanmod.rewards.SingleCardReward;

import java.util.ArrayList;
import java.util.Arrays;

import static oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_SINGLECARDREWARD;

@SpirePatch(clz=CombatRewardScreen.class, method="setupItemReward")
public class ReplaceCardRewards {
    public static ArrayList<String> rewardsToReplace = new ArrayList<String>(Arrays.asList("CARD", "DAZINGPULSE", "DECABEAM", "DONUBEAM", "EXPLODE", "SPIKE", "BOSSCARD", "JAXCARD", "UPGRADEDUNKNOWNCARD", "SEALCARD", "GEM", "GEMALLRARITIES", "PANS_TRINKET_TRINKET_REWARD"));

    public static void Postfix(CombatRewardScreen __instance) {
        if (!OceanMod.doVisibleRewards) return;
        boolean keepGoing = true;
        while (keepGoing) {
            keepGoing = false;
            for (RewardItem reward : __instance.rewards)
                if (replaceReward(reward)) {
                    keepGoing = true;
                    break;
                }
        }
        AbstractDungeon.combatRewardScreen.positionRewards();
    }

    public static boolean replaceReward(RewardItem reward) {
        if (rewardsToReplace.contains(reward.type.name())) {
            ArrayList<RewardItem> rewardsToAdd = new ArrayList<RewardItem>();
            ArrayList<RewardItem> rewardsToRemove = new ArrayList<RewardItem>();
            ArrayList<SingleCardReward> cardOptions = new ArrayList<SingleCardReward>();

            if (Loader.isModLoaded("PansTrinkets") && reward.type == TrinketRewardTypePatch.PANS_TRINKET_TRINKET_REWARD) {
                for (AbstractCard c : ((TrinketReward)reward).linkedReward.cards)
                    cardOptions.add(new SingleCardReward(c));
                for (AbstractCard c : reward.cards) {
                    SingleCardReward scr = new SingleCardReward(c);
                    scr.isTrinket = true;
                    cardOptions.add(scr);
                }
                rewardsToRemove.add(((TrinketReward)reward).linkedReward);
            } else {
                for (AbstractCard c : reward.cards)
                    cardOptions.add(new SingleCardReward(c));
            }

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof SingingBowl)
                    cardOptions.add(new SingleCardReward(SingingBowlButton.TEXT[2], ImageMaster.TP_HP, r));
                if (Loader.isModLoaded("stslib") && CardRewardSkipButtonRelic.class.isAssignableFrom(r.getClass()))
                    cardOptions.add(new SingleCardReward(((CardRewardSkipButtonRelic)r).getButtonLabel(), ImageMaster.TICK, r));
            }
            
            for (SingleCardReward option : cardOptions) {
                if (AbstractDungeon.player.hasRelic("YesRelic") && option.type == VCR_SINGLECARDREWARD)
                    continue;
                for (SingleCardReward otherOption : cardOptions) {
                    if (option != otherOption)
                        option.addCardLink(otherOption);
                }
            }

            rewardsToAdd.addAll(cardOptions);
            rewardsToRemove.add(reward);
            AbstractDungeon.combatRewardScreen.rewards.removeAll(rewardsToRemove);
            AbstractDungeon.combatRewardScreen.rewards.addAll(rewardsToAdd);
            return true;
        }
        return false;
    }
}