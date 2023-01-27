package oceanmod.patches.visiblecardrewards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.FrozenEgg2;
import com.megacrit.cardcrawl.relics.MoltenEgg2;
import com.megacrit.cardcrawl.relics.ToxicEgg2;
import com.megacrit.cardcrawl.rewards.RewardItem;
import oceanmod.rewards.SingleCardReward;

public class EggPickup {
    public static void upgradeAllOfType(AbstractCard.CardType type) {
        for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
            if (reward instanceof SingleCardReward) {
                AbstractCard c = (AbstractCard)(((SingleCardReward)reward).card);
                if (c.type == type && c.canUpgrade() && !c.upgraded) {
                    c.upgrade();
                    (((SingleCardReward)reward).renderCard).upgrade();
                    reward.text = c.name;
                }
            }
        } 
    }

    @SpirePatch(clz=FrozenEgg2.class, method="onEquip")
    public static class FrozenPatch {
        public static void Postfix() {
            upgradeAllOfType(AbstractCard.CardType.POWER);
        }
    }

    @SpirePatch(clz=MoltenEgg2.class, method="onEquip")
    public static class MoltenPatch {
        public static void Postfix() {
            upgradeAllOfType(AbstractCard.CardType.ATTACK);
        }
    }

    @SpirePatch(clz=ToxicEgg2.class, method="onEquip")
    public static class ToxicPatch {
        public static void Postfix() {
            upgradeAllOfType(AbstractCard.CardType.SKILL);
        }
    }
}