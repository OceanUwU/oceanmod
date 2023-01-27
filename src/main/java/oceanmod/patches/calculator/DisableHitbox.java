package oceanmod.patches.calculator;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.Hitbox;

import oceanmod.ui.calculator.Button;

@SpirePatch(clz=Hitbox.class, method="update", paramtypez={})
public class DisableHitbox {
    public static void Postfix(Hitbox __instance) {
        if (Button.anyPressed)
            __instance.clicked = false;
    }
}