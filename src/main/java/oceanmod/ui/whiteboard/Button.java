package oceanmod.ui.whiteboard;

import basemod.BaseMod;
import basemod.interfaces.PreUpdateSubscriber;
import oceanmod.OceanMod;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public abstract class Button implements PreUpdateSubscriber {
    public static int width = 64, height = 64;

    public Texture texture;
    public Hitbox hb;
    public float x, y;

    public Button(String texturePath) {
        loadTexture(texturePath);
        hb = new Hitbox(x, y, width, height);
        BaseMod.subscribe(this);
    }

    public void loadTexture(String texturePath) {
        texture = new Texture(texturePath);
    }

    public void move(float newX, float newY) {
        x = newX;
        y = newY;
        hb.x = x;
        hb.y = y;
    }
    
    public void receivePreUpdate() {
        hb.update();
        if (OceanMod.whiteboardOpen) {
            if (hb.hovered && InputHelper.justClickedLeft)
                hb.clickStarted = true;
            if (hb.clicked) {
                onClick();
                hb.clicked = false;
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.draw(texture, x, y);
    }

    protected abstract void onClick();
}