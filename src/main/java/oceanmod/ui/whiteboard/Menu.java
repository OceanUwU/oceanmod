package oceanmod.ui.whiteboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import oceanmod.OceanMod;

import java.io.IOException;

public class Menu {
    private static Texture background = new Texture(OceanMod.resourcePath("images/whiteboard/menu.png"));
    private static int Y_BELOW_TOP = 40;
    private static int PADDING = 20;
    private static Color[] colors = {
        new Color(1.00f, 0.16f, 0.16f, 1), //red
        new Color(1.00f, 1.00f, 0.00f, 1), //yellow
        new Color(0.05f, 0.42f, 1.00f, 1), //blue
        new Color(0.05f, 1.00f, 0.15f, 1), //green
        new Color(0.96f, 0.26f, 0.81f, 1), //pink
        new Color(1.00f, 0.65f, 0.22f, 1), //orange
        new Color(0.41f, 0.24f, 0.79f, 1), //purple
        new Color(0.90f, 0.90f, 0.90f, 1), //white
        new Color(0.10f, 0.10f, 0.10f, 1), //black
    };
    private static int[] sizes = {1, 2, 4, 6};

    private float x;
    private float y;
    private int colorIndex = OceanMod.config.getInt("color");
    public Color color;
    private int size = OceanMod.config.getInt("size");
    private boolean erasing = false;

    private Button penTypeButton = new Button(OceanMod.resourcePath("images/whiteboard/pen.png")) {
        public void onClick() {
            toggleErasing();
        }
    };
    private Button colorButton = new Button(OceanMod.resourcePath("images/whiteboard/color.png")) {
        public void onClick() {
            try {nextColor();} catch(Exception e) {}
        }
    };
    private Button sizeButton = new Button(OceanMod.resourcePath("images/whiteboard/size"+String.valueOf(size)+".png")) {
        public void onClick() {
            try {nextSize();} catch(Exception e) {}
        }
    };
    private Button clearButton = new Button(OceanMod.resourcePath("images/whiteboard/clear.png")) {
        public void onClick() {
            clear();
        }
    };

    public Menu() {
        color = colors[colorIndex];
        OceanMod.whiteboardDrawing.setColor(color);
        OceanMod.whiteboardDrawing.size = sizes[size];
    }

    public void move(float newX, float newY) {
        x = newX - background.getWidth() * 0.5f;
        y = newY - Y_BELOW_TOP - background.getHeight();
        float btnX = x+PADDING;
        float btnY = y+PADDING;
        penTypeButton.move(btnX, btnY);
        colorButton.move(btnX+64, btnY);
        sizeButton.move(btnX+128, btnY);
        clearButton.move(btnX+196, btnY);
    }

    private void toggleErasing() {
        erasing = !erasing;
        OceanMod.whiteboardDrawing.setColor(erasing ? new Color(0,0,0,0) : color);
        OceanMod.whiteboardDrawing.size = sizes[size] + (erasing ? 8 : 0);
        penTypeButton.loadTexture(erasing ? OceanMod.resourcePath("images/whiteboard/eraser.png") : OceanMod.resourcePath("images/whiteboard/pen.png"));
    }

    private void nextColor() throws IOException {
        colorIndex = ++colorIndex % colors.length;
        color = colors[colorIndex];
        if (!erasing)
            OceanMod.whiteboardDrawing.setColor(color);
        OceanMod.config.setInt("color", colorIndex);
        OceanMod.config.save();
    }

    private void nextSize() throws IOException {
        size = ++size % sizes.length;
        OceanMod.whiteboardDrawing.size = sizes[size] + (erasing ? 8 : 0);
        sizeButton.loadTexture(OceanMod.resourcePath("images/whiteboard/size")+String.valueOf(size)+".png");
        OceanMod.config.setInt("size", size);
        OceanMod.config.save();
    }

    private void clear() {
        OceanMod.whiteboardDrawing.clear();
    }

    public void render(SpriteBatch sb) {
        if (OceanMod.whiteboardOpen) {
            sb.setColor(color);
            sb.draw(background, x, y);
            colorButton.render(sb);
            sb.setColor(Color.WHITE);
            penTypeButton.render(sb);
            sizeButton.render(sb);
            clearButton.render(sb);
        }
    }
}