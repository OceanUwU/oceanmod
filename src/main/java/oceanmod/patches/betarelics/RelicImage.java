package oceanmod.patches.betarelics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.LandingSound;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import oceanmod.BetaRelics;


public class RelicImage {
    @SpirePatch(clz=AbstractRelic.class, method=SpirePatch.CLASS)
    public static class Images {
        public static SpireField<Texture> normalImg = new SpireField<>(() -> null);
        public static SpireField<Texture> normalOutlineImg = new SpireField<>(() -> null);
        public static SpireField<Texture> normalLargeImg = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractRelic.class, method=SpirePatch.CONSTRUCTOR)
    public static class Initialize {
        public static void Postfix(AbstractRelic __instance) {
            if (!(__instance instanceof CustomRelic) && BetaRelics.betas.containsKey(__instance.relicId)) {
                Images.normalImg.set(__instance, __instance.img);
                Images.normalOutlineImg.set(__instance, __instance.outlineImg);
                Images.normalLargeImg.set(__instance, __instance.largeImg);
                if (BetaRelics.config != null && BetaRelics.config.getBool(__instance.relicId))
                    toggleBeta(__instance, true);
            }
        }
    }
    

    public static class InitializeCustom {
        @SpirePatch(clz=CustomRelic.class, method=SpirePatch.CONSTRUCTOR, paramtypez={String.class, Texture.class, Texture.class, RelicTier.class, LandingSound.class})
        public static class WithOutline {
            public static void Postfix(CustomRelic __instance) {
                setupCustom(__instance);
            }
        }

        @SpirePatch(clz=CustomRelic.class, method=SpirePatch.CONSTRUCTOR, paramtypez={String.class, Texture.class, RelicTier.class, LandingSound.class})
        public static class WithoutOutline {
            public static void Postfix(CustomRelic __instance) {
                setupCustom(__instance);
            }
        }

        @SpirePatch(clz=CustomRelic.class, method=SpirePatch.CONSTRUCTOR, paramtypez={String.class, String.class, RelicTier.class, LandingSound.class})
        public static class imgName {
            public static void Postfix(CustomRelic __instance) {
                setupCustom(__instance);
            }
        }

        public static void setupCustom(CustomRelic relic) {
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
        System.out.println(relic instanceof CustomRelic);
        if (relic instanceof CustomRelic) {
            if (beta)
                ((CustomRelic)relic).setTextureOutline(ImageMaster.loadImage(BetaRelics.betas.get(relic.relicId).get(0)), ImageMaster.loadImage(BetaRelics.betas.get(relic.relicId).get(1)));
            else
                ((CustomRelic)relic).setTextureOutline(Images.normalImg.get(relic), Images.normalOutlineImg.get(relic));
        } else {
            if (beta) {
                relic.img = ImageMaster.loadImage(BetaRelics.betas.get(relic.relicId).get(0));
                relic.outlineImg = ImageMaster.loadImage(BetaRelics.betas.get(relic.relicId).get(1));
                relic.largeImg = relic.img;
            } else {
                relic.img = Images.normalImg.get(relic);
                relic.outlineImg = Images.normalOutlineImg.get(relic);
                relic.largeImg = Images.normalLargeImg.get(relic);
            }
        }
    }
}