package oceanmod.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;

import java.lang.reflect.Field;

public class DiscountEventOption {
    @SpirePatch(clz=LargeDialogOptionButton.class, method=SpirePatch.CLASS)
    public static class DiscountedField {
        public static SpireField<Boolean> discounted = new SpireField<>(() -> false);
    }

    @SpirePatch(clz=LargeDialogOptionButton.class, method="hoverAndClickLogic")
    public static class RightClick {
        private static Field boxColorField;

        static {
            try {
                boxColorField = LargeDialogOptionButton.class.getDeclaredField("boxColor");
                boxColorField.setAccessible(true);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public static void Postfix(LargeDialogOptionButton __instance) {
            if (__instance.hb.hovered && InputHelper.justClickedRight)
                DiscountedField.discounted.set(__instance, !(boolean)DiscountedField.discounted.get(__instance));

            if (DiscountedField.discounted.get(__instance)) {
                try {
                    boxColorField.set(__instance, new Color(0.1F, 0.1F, 0.1F, 0.0F));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}