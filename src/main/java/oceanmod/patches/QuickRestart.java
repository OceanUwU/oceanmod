package oceanmod.patches;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.DownloadAndRestarter;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import oceanmod.OceanMod;

@SpireInitializer
public class QuickRestart implements PostUpdateSubscriber {
    public static void initialize() {
        BaseMod.subscribe(new QuickRestart());
    }

    public void receivePostUpdate() {
        if (OceanMod.canRestart && Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            for (int i = 0; i < Loader.ARGS.length; i++)
                if (Loader.ARGS[i].contains("skip-launcher"))
                    Loader.ARGS[i] = "";
            ReflectionHacks.privateStaticMethod(DownloadAndRestarter.class, "restartApplication").invoke();
        }
    }
}