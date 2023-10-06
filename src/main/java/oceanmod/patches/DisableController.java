package oceanmod.patches;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import oceanmod.OceanMod;

@SpirePatch(clz=Controllers.class, method="getControllers")
public class DisableController {
    public static SpireReturn<Array<Controllers>> Prefix() {
        if (OceanMod.noController) return SpireReturn.Return(null);
        else return SpireReturn.Continue();
    }
}