package oceanmod.patches.whiteboard;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import oceanmod.OceanMod;

@SpirePatch(clz=TopPanel.class, method="render")
public class Render {
    public static void Prefix(TopPanel __instance, SpriteBatch sb) {
        OceanMod.whiteboardDrawing.render(sb);
        OceanMod.whiteboardMenu.render(sb);
    }
}