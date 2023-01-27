package oceanmod.ui.calculator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import oceanmod.OceanMod;

public class CloseButton extends Button {
    private static Texture texture = ImageMaster.loadImage(OceanMod.resourcePath("images/calculator/close.png"));
    private static int width = 32, height = 32;

    public CloseButton(Calculator setCalculator) {
        super(Calculator.width - (width/2), Calculator.height - (height/2), 20, setCalculator);
        
        hb = new Hitbox(x, y, width, height);
    }

    @Override
    public void render(SpriteBatch sb, Color color) {
        if (super.hb.clickStarted)
            sb.setColor(super.calculator.color.cpy().mul(Button.pressedMult));
        else if (super.hb.hovered)
            sb.setColor(super.calculator.color.cpy().mul(Button.hoveredMult));
        sb.draw(texture, x, y);
    }
}
