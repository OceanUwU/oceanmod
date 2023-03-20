package forkedflamefrenzy.patches;

import basemod.ReflectionHacks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;

public class RenderArt {
    public static Texture ATTACK_IMG = loadImage("a");
    public static Texture ATTACK_IMG_L = loadImage("al");
    public static Texture SKILL_IMG = loadImage("s");
    public static Texture SKILL_IMG_L = loadImage("sl");
    public static Texture POWER_IMG = loadImage("p");
    public static Texture POWER_IMG_L = loadImage("pl");

    @SpirePatch(clz=OptionsPanel.class, method=SpirePatch.CONSTRUCTOR)
    public static class ReplaceText {
        public static void Postfix() {
            ReflectionHacks.setPrivateStaticFinal(OptionsPanel.class, "PLAYTESTER_ART_TEXT", "Forked Flame Mode");
        }
    }

    @SpirePatch(clz=AbstractCard.class, method="renderJokePortrait")
    public static class Small {
        private static Color transparent = new Color(0f,0f,0f,0f);

        public static void stopRender(AbstractCard c, SpriteBatch sb) {
            if (Settings.PLAYTESTER_ART_MODE && getPortrait(c.type, false) != null)
                sb.setColor(transparent);
        }

        @SpireInsertPatch(loc=1974)
        public static void draw1(AbstractCard __instance, SpriteBatch sb) {
            stopRender(__instance, sb);
        }

        @SpireInsertPatch(loc=1987)
        public static void draw2(AbstractCard __instance, SpriteBatch sb) {
            stopRender(__instance, sb);
        }

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            Texture image = getPortrait(__instance.type, false);
            if (!Settings.PLAYTESTER_ART_MODE || image == null) return;
            sb.setColor(colorFromString(__instance.cardID, __instance.transparency));
            sb.draw(image, __instance.current_x - 125f, __instance.current_y - 23f, 125.0F, 23.0F, 250.0F, 190.0F, __instance.drawScale * Settings.scale, __instance.drawScale * Settings.scale, __instance.angle, 0, 0, 250, 190, false, false);
        }
    }
    
    @SpirePatch(clz=SingleCardViewPopup.class, method="renderPortrait")
    public static class Large {
        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
            AbstractCard c = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            Texture image = getPortrait(c.type, true);
            if (!Settings.PLAYTESTER_ART_MODE || image == null) return;
            sb.setColor(colorFromString(c.cardID, 1f));
            sb.draw(image, Settings.WIDTH / 2.0F - 250.0F, Settings.HEIGHT / 2.0F - 190.0F + 136.0F * Settings.scale, 250.0F, 190.0F, 500.0F, 380.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 500, 380, false, false);
            sb.setColor(Color.WHITE);
        }
    }

    public static Texture getPortrait(AbstractCard.CardType type, boolean large) {
        switch (type) {
            case ATTACK: return large ? ATTACK_IMG_L : ATTACK_IMG;
            case SKILL: return large ? SKILL_IMG_L : SKILL_IMG;
            case POWER: return large ? POWER_IMG_L : POWER_IMG;
            default: return null;
        }
    }

    public static Color colorFromString(String str, float alpha) {
        int num = str.hashCode();
        float r = num % 50;
        float g = Math.round(num / 50) % 50;
        float b = Math.round(num / Math.pow(50, 2)) % 50;
        return new Color(0.5f + r / 100f, 0.5f + g / 100f, 0.5f + b / 100f, alpha);
    }

    public static Texture loadImage(String path) {
        return new Texture("forkedflamefrenzyresources/"+path+".png");
    }
}