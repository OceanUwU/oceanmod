package oceanmod.ui.calculator;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import oceanmod.CalculatorManager;
import oceanmod.OceanMod;


public class PanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture(OceanMod.resourcePath("images/calculator/icon.png"));
    public static final String ID = "calculator:CalculatorPanelItem";

    public PanelItem() {
	    super(IMG, ID);
    }

    @Override
    protected void onClick() {
        CalculatorManager.openCalculator(this.x + (hb_w / 2) + (int)(Math.random() * 21) - 10, this.y - 100 + (int)(Math.random() * 21) - 10);
    }
}