package oceanmod.ui.calculator;

import basemod.BaseMod;
import basemod.interfaces.PreUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import oceanmod.CalculatorManager;
import oceanmod.OceanMod;

import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Calculator implements RenderSubscriber, PreUpdateSubscriber {
    private static Texture background = ImageMaster.loadImage(OceanMod.resourcePath("images/calculator/calculator.png"));
    private static Texture display = ImageMaster.loadImage(OceanMod.resourcePath("images/calculator/display.png"));
    public static int width = 300, height = 500;
    private static int displayWidth = 264;
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("js");
    private static FileHandle fontFile = Gdx.files.internal("font/Kreon-Regular.ttf");
    public static BitmapFont font;
    private static DecimalFormat df = new DecimalFormat("#.##");

    private Hitbox hb;
    private float x, y;
    public ArrayList<Button> buttons = new ArrayList<>();
    private String expression = "";
    private float startX, startY;
    public Color color;
    public boolean dark;
    private String result = "0";

    public static void setupFont() {
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(50.0f * Settings.scale);
        p.gamma = 1.2F;
        p.minFilter = Texture.TextureFilter.Linear;
        p.magFilter = Texture.TextureFilter.Linear;
        g.scaleForPixelHeight(p.size);
        font = g.generateFont(p);
        font.setUseIntegerPositions(false);
        (font.getData()).markupEnabled = true;
        if (LocalizedStrings.break_chars != null)
            (font.getData()).breakChars = LocalizedStrings.break_chars.toCharArray(); 
        (font.getData()).fontFile = fontFile;
    }

    public Calculator(float xLoc, float yLoc) {
        x = xLoc - (width / 2);
        y = yLoc - height;
        color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        dark = (0.2126f * color.r + 0.7152f * color.g + 0.0722f * color.b) < 0.4f;
        hb = new Hitbox(x, y, width, height);

        int index = 0;
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 4; column++) {
                Button button = new Button(13+(70*column), 20+(70*row), index++, this);
                button.move(x, y);
                buttons.add(button);
            }
        }

        CloseButton closeButton = new CloseButton(this);
        closeButton.move(x, y);
        buttons.add(closeButton);

        if (font == null) setupFont();

        BaseMod.subscribe(this);
    }

    public void close() {
        for (Button button : buttons) {
            button.remove();
        }
        buttons.clear();
        CalculatorManager.calculators.remove(this);
        BaseMod.unsubscribeLater(this);
    }

    public void addToExpr(String text, boolean isNum) {
        if (expression.contains("="))
            expression = isNum ? "" : result;
        expression += text;
    }

    public void evaluate() {
        try {
            if (!expression.contains("=")) {
                Object engineResult = engine.eval(expression);
                if (engineResult instanceof Integer) {
                    result = Integer.toString((int)engineResult);
                } else {
                    double num = (double)engine.eval(expression);
                    if ((Math.floor(num) - num) > 0)
                        result = String.valueOf((int)num);
                    else
                        result = df.format(num);
                }
                expression += "=" + result;
            }
        } catch(Exception e) {
            e.printStackTrace();
            expression += "=" + "ERR";
        }
    }

    public void pressButton(int type) {
        switch(type) {
            case 0:
            case 4: case 5: case 6:
            case 8: case 9: case 10:
            case 12: case 13: case 14:
                addToExpr(Button.texts.get(type), true);
                break;
            case 3:
            case 7:
            case 11:
            case 15:
            case 16: case 17:
                addToExpr(Button.texts.get(type), false);
                break;
            case 1: addToExpr(".", true);
                break;
            case 18:
                if (expression.length() > 0)
                    expression = expression.substring(0, expression.length()-1);
                break;
            case 19: expression = "";
                break;
            case 2: evaluate();
                break;
            case 20: close();
                break;
        }
    }

    public void receivePreUpdate() {
        hb.update();
        if (hb.hovered && InputHelper.justClickedLeft) {
            startX = InputHelper.mX;
            startY = InputHelper.mY;
            hb.clickStarted = true;
        }
        if (hb.clickStarted) {
            x += InputHelper.mX - startX;
            y += InputHelper.mY - startY;
            hb.translate(x, y);
            startX = InputHelper.mX;
            startY = InputHelper.mY;
            for (Button button : buttons) {
                button.move(x, y);
            }
        }
    }

    public void receiveRender(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(background, x, y);
        sb.draw(display, x+13, y+height-112);
        for (Button button : buttons) {
            sb.setColor(color);
            button.render(sb, color);
        }

        if (expression.length() > 0) {
            font.getData().setScale(Math.min(displayWidth / FontHelper.getWidth(font, expression, 1.0f) * 0.9f, 1.2f));
            FontHelper.renderFontRightAligned(sb, font, expression, x+width-18, y+height-69, dark ? Color.WHITE : Color.BLACK);
        }
    }
}