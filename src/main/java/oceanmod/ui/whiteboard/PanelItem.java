package oceanmod.ui.whiteboard;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import oceanmod.OceanMod;

public class PanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture(OceanMod.resourcePath("images/whiteboard/icon.png"));
    public static final String ID = "whiteboard:WhiteboardPanelItem";

    public PanelItem() {
	    super(IMG, ID);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb, OceanMod.whiteboardMenu.color);
    }

    public void correctMenuVisibility() {
        if (OceanMod.whiteboardOpen)
            OceanMod.whiteboardMenu.move(this.x + (hb_w / 2), this.y);
        else
            OceanMod.whiteboardMenu.move(-1000, -1000);
    }

    @Override
    protected void onClick() {
        OceanMod.whiteboardOpen = !OceanMod.whiteboardOpen;
        correctMenuVisibility();
    }
}