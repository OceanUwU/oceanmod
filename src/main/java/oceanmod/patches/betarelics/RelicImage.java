package oceanmod.patches.betarelics;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.LandingSound;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import oceanmod.BetaRelics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RelicImage {
    @SpirePatch(clz=AbstractRelic.class, method=SpirePatch.CLASS)
    public static class Images {
        public static SpireField<Texture> normalImg = new SpireField<>(() -> null);
        public static SpireField<Texture> normalOutlineImg = new SpireField<>(() -> null);
        public static SpireField<Texture> normalLargeImg = new SpireField<>(() -> null);
    }

    public static void initializeLibraryRelics() {
        for (String listName : Arrays.asList("sharedRelics", "redRelics", "greenRelics", "blueRelics", "purpleRelics")) {
            HashMap<String, AbstractRelic> list = ReflectionHacks.getPrivateStatic(RelicLibrary.class, listName);
            for (Map.Entry<String, AbstractRelic> r : list.entrySet())
                InitializeRelic.initialize(r.getValue());
        }
        HashMap<AbstractCard.CardColor, ArrayList<AbstractRelic>> customLists = ReflectionHacks.getPrivateStatic(BaseMod.class, "customRelicLists");
        for (Map.Entry<AbstractCard.CardColor, ArrayList<AbstractRelic>> set : customLists.entrySet())
            for (AbstractRelic r : set.getValue())
                InitializeRelic.initialize(r);
    }


    public static class InitializeRelic {
        @SpirePatch(clz=AbstractRelic.class, method=SpirePatch.CONSTRUCTOR)
        public static class InitAbstractRelic {public static void Postfix(AbstractRelic __instance) {if (!(__instance instanceof CustomRelic)) initialize(__instance);}}
        @SpirePatch(clz=CustomRelic.class, method=SpirePatch.CONSTRUCTOR, paramtypez={String.class, Texture.class, Texture.class, RelicTier.class, LandingSound.class})
        public static class InitCustomWithOutline {public static void Postfix(CustomRelic __instance) {initialize(__instance);}}
        @SpirePatch(clz=CustomRelic.class, method=SpirePatch.CONSTRUCTOR, paramtypez={String.class, Texture.class, RelicTier.class, LandingSound.class})
        public static class InitCustomWithoutOutline {public static void Postfix(CustomRelic __instance) {initialize(__instance);}}
        @SpirePatch(clz=CustomRelic.class, method=SpirePatch.CONSTRUCTOR, paramtypez={String.class, String.class, RelicTier.class, LandingSound.class})
        public static class imgName {public static void Postfix(CustomRelic __instance) {initialize(__instance);}}

        public static void initialize(AbstractRelic relic) {
            if (BetaRelics.betas.containsKey(relic.relicId)) {
                Images.normalImg.set(relic, relic.img);
                Images.normalOutlineImg.set(relic, relic.outlineImg);
                Images.normalLargeImg.set(relic, relic.largeImg);
                if (BetaRelics.config != null && BetaRelics.config.getBool(relic.relicId))
                    toggleBeta(relic, true);
            } 
        }
    }

    @SpirePatch(clz=AbstractRelic.class, method="update")
    public static class UpdateImage {
        public static void Postfix(AbstractRelic __instance) {
            if (BetaToggle.lastToggled == __instance.relicId) {
                toggleBeta(__instance, BetaRelics.config.getBool(__instance.relicId));
                BetaToggle.relicsUpdated = true;
            }
        }
    }

    public static void toggleBeta(AbstractRelic relic, boolean beta) {
        if (relic instanceof CustomRelic) {
            if (beta)
                ((CustomRelic)relic).setTextureOutline(BetaRelics.getTexture(relic.relicId), BetaRelics.getOutline(relic.relicId));
            else
                ((CustomRelic)relic).setTextureOutline(Images.normalImg.get(relic), Images.normalOutlineImg.get(relic));
        } else {
            if (beta) {
                relic.img = BetaRelics.getTexture(relic.relicId);
                relic.outlineImg = BetaRelics.getOutline(relic.relicId);
                relic.largeImg = relic.img;
            } else {
                relic.img = Images.normalImg.get(relic);
                relic.outlineImg = Images.normalOutlineImg.get(relic);
                relic.largeImg = Images.normalLargeImg.get(relic);
            }
        }
    }
}