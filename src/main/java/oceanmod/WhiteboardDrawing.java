package oceanmod;

import basemod.interfaces.PreUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class WhiteboardDrawing implements PreUpdateSubscriber {
    private static FileHandle saveHandle = new FileHandle(SpireConfig.makeFilePath(OceanMod.ID, "whiteboardDrawing", "cim"));

    public Color color;
    public int size = 0;
    public boolean middle = OceanMod.config.getBool("middle");
    private Pixmap pixmap;
    private Texture texture;
    private Vector2 lastPos;
    private boolean mouseMiddle = false;
    private boolean justMouseMiddle = false;
    private boolean prevMouseMiddle = false;
    private boolean releasedMouseMiddle = false;

    public WhiteboardDrawing() {
        pixmap = new Pixmap(Settings.WIDTH, Settings.HEIGHT, Pixmap.Format.RGBA8888);
        if (saveHandle.exists()) {
            Pixmap prevPixmap = PixmapIO.readCIM(saveHandle);
            pixmap.drawPixmap(prevPixmap, 0, 0);
            prevPixmap.dispose();
        }
        texture = new Texture(pixmap);
    }

    public void setColor(Color newColor) {
        color = newColor;
		pixmap.setColor(color);
    }

	public void clear() {
		pixmap.setColor(new Color(0,0,0,0));
		pixmap.fill();
		pixmap.setColor(color);
        updateTexture();
        savePixmap();
	}

    private void draw(Vector2 dot) {
        pixmap.fillCircle((int)dot.x, (int)dot.y, size);
    }

    private void updateTexture() {
        texture.dispose();
        texture = new Texture(pixmap);
    }

    public void savePixmap() {
        PixmapIO.writeCIM(saveHandle, pixmap);
    }

    public void receivePreUpdate() {
        if (middle) {
            mouseMiddle = Gdx.input.isButtonPressed(2);
            justMouseMiddle = mouseMiddle && !prevMouseMiddle;
            releasedMouseMiddle = !mouseMiddle && prevMouseMiddle;
            prevMouseMiddle = mouseMiddle;
            if (justMouseMiddle && !OceanMod.whiteboardOpen) {
                if (OceanMod.whiteboardEnabled) OceanMod.whiteboardOpen = true;
                OceanMod.whiteboardPanelItem.correctMenuVisibility();
            }
        }
        if (OceanMod.whiteboardOpen) {
            if ((middle && justMouseMiddle) || (!middle && InputHelper.justClickedRight))
                Pixmap.setBlending(Pixmap.Blending.None);
            if ((middle && mouseMiddle) || (!middle && InputHelper.isMouseDown_R)) {
                if (!middle) {
                    InputHelper.isMouseDown_R = false;
                    InputHelper.justClickedRight = false;
                }
                Vector2 pos = new Vector2(InputHelper.mX, Settings.HEIGHT - InputHelper.mY);
                if (lastPos == null) {
                    draw(pos);
                    updateTexture();
                    texture = new Texture(pixmap);
                } else if (!pos.equals(lastPos)) {
                    float step = size / (16.0f * pos.dst(lastPos));
                    for (float a = 0; a < 1f; a += step)
                        draw(pos.lerp(lastPos, a));
                    updateTexture();
                }
                lastPos = new Vector2(InputHelper.mX, Settings.HEIGHT - InputHelper.mY);
            } else if ((middle && releasedMouseMiddle) || (!middle && InputHelper.justReleasedClickRight)) {
                lastPos = null;
                Pixmap.setBlending(Pixmap.Blending.SourceOver);
            }
        }   
    }

    public void render(SpriteBatch sb) {
        if (OceanMod.whiteboardOpen) {
            sb.setColor(Color.WHITE);
            sb.draw(texture, 0, 0);
        }
    }
}