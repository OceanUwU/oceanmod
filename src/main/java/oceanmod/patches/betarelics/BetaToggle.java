package oceanmod.patches.betarelics;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import oceanmod.BetaRelics;

import java.io.IOException;
import java.util.ArrayList;

public class BetaToggle {
    private static AbstractRelic currRelic;
    private static boolean available;
    private static boolean enabled;
    private static Hitbox enableHb = null;
    public static String lastToggled;
    public static boolean relicsUpdated = false;

    @SpirePatch(clz=SingleRelicViewPopup.class, method=SpirePatch.CLASS)
    public static class Image {
        public static SpireField<Texture> normalLargeImg = new SpireField<>(() -> null);
    }

    public static class Open {
        @SpirePatch(clz=SingleRelicViewPopup.class, method="open", paramtypez={AbstractRelic.class, ArrayList.class})
        public static class Group {public static void Postfix(SingleRelicViewPopup __instance, AbstractRelic relic, ArrayList<AbstractRelic> group) {loadPref(__instance, relic);}}
        @SpirePatch(clz=SingleRelicViewPopup.class, method="open", paramtypez={AbstractRelic.class})
        public static class NoGroup {public static void Postfix(SingleRelicViewPopup __instance, AbstractRelic relic) {loadPref(__instance, relic);}}

        public static void loadPref(SingleRelicViewPopup popup, AbstractRelic relic) {
            currRelic = relic;
            available = BetaRelics.betas.containsKey(relic.relicId);
            enabled = false;
            if (available) {
                Image.normalLargeImg.set(popup, ReflectionHacks.getPrivate(popup, SingleRelicViewPopup.class, "largeImg"));
                enabled = BetaRelics.config.getBool(relic.relicId);
                enableHb = new Hitbox(250.0F * Settings.scale, 80.0F * Settings.scale);
                enableHb.move(Settings.WIDTH / 2.0F, 70.0F * Settings.scale);
                toggleBeta(popup, true);
            }
        }
    }

    public static void toggleBeta(SingleRelicViewPopup popup, boolean firstTime) {
        if (!firstTime)
            currRelic.update();
        if (enabled) ReflectionHacks.setPrivate(popup, SingleRelicViewPopup.class, "largeImg", null);
        else ReflectionHacks.setPrivate(popup, SingleRelicViewPopup.class, "largeImg", Image.normalLargeImg.get(popup));
    }

    @SpirePatch(clz=SingleRelicViewPopup.class, method="close")
    public static class Close {
        public static void Postfix(SingleRelicViewPopup __instance) {
            enableHb = null;
        }
    }

    @SpirePatch(clz=SingleRelicViewPopup.class, method="updateInput")
    public static class Update {
        public static SpireReturn<Void> Prefix(SingleRelicViewPopup __instance) throws IOException {
            if (relicsUpdated) {
                lastToggled = null;
                relicsUpdated = true;
            }
            if (available) {
                if (enableHb == null) return SpireReturn.Continue(); 
                enableHb.update();
                if (enableHb.hovered && InputHelper.justClickedLeft)
                    enableHb.clickStarted = true; 
                if (enableHb.clicked || CInputActionSet.topPanel.isJustPressed()) {
                    CInputActionSet.topPanel.unpress();
                    enableHb.clicked = false;
                    enabled = !enabled;
                    BetaRelics.config.setBool(currRelic.relicId, enabled);
                    BetaRelics.config.save();
                    lastToggled = currRelic.relicId;
                    relicsUpdated = false;
                    toggleBeta(__instance, false);
                }
                if (enableHb.hovered) return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz=SingleRelicViewPopup.class, method="render")
    public static class Render {
        public static void Postfix(SingleRelicViewPopup __instance, SpriteBatch sb) {
            if (available) {
                if (enableHb == null) return; 
                FontHelper.cardTitleFont.getData().setScale(1.0F);
                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.CHECKBOX, enableHb.cX - 80.0F * Settings.scale - 32.0F, enableHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                if (enableHb.hovered)
                    FontHelper.renderFont(sb, FontHelper.cardTitleFont, SingleCardViewPopup.TEXT[14], enableHb.cX - 45.0F * Settings.scale, enableHb.cY + 10.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);
                else
                    FontHelper.renderFont(sb, FontHelper.cardTitleFont, SingleCardViewPopup.TEXT[14], enableHb.cX - 45.0F * Settings.scale, enableHb.cY + 10.0F * Settings.scale, Settings.GOLD_COLOR);
                if (enabled) {
                    sb.setColor(Color.WHITE);
                    sb.draw(ImageMaster.TICK, enableHb.cX - 80.0F * Settings.scale - 32.0F, enableHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                } 
                enableHb.render(sb);
            }
        }
    }
}