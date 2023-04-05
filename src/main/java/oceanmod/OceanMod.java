package oceanmod;

import basemod.BaseMod;
import basemod.ModButton;
import basemod.ModLabel;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import basemod.ModToggleButton;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.RenderSubscriber;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.DoomAndGloom;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.cards.green.DaggerSpray;
import com.megacrit.cardcrawl.cards.purple.Brilliance;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import oceanmod.patches.BorderColours;
import oceanmod.ui.whiteboard.Menu;
import oceanmod.ui.whiteboard.PanelItem;

import java.io.IOException;
import java.util.Properties;

@SpireInitializer
public class OceanMod implements PostInitializeSubscriber, EditStringsSubscriber, RenderSubscriber {
    public static String ID = "oceanmod";
    public static String[] TEXT;
    private static Properties defaults = new Properties(); 
    public static SpireConfig config;
    public static ModPanel settingsPanel = new ModPanel();
    
    public static Settings.GameLanguage[] SupportedLanguages = {
        Settings.GameLanguage.ENG
    };

    public static boolean doVisibleRewards;
    public static boolean doCalculator;

    public static boolean whiteboardEnabled;
    public static WhiteboardDrawing whiteboardDrawing;
    public static Menu whiteboardMenu;
    public static PanelItem whiteboardPanelItem;
    public static ModToggleButton borderToggleButton;
    public static ModMinMaxSlider[] borderColourSliders = {null, null, null};
    public static ModLabel borderLabel;
    public static ModButton borderPrevButton;
    public static ModButton borderNextButton;
    public static boolean whiteboardOpen = false;
    public static int rarityEditing = 0;

    private static AbstractCard[] exampleCards = {null, null, null, null, null, null, null};

    public static void initialize() throws IOException {
        defaults.setProperty("whiteboard", "true");
        defaults.setProperty("calculator", "true");
        defaults.setProperty("color", "0");
        defaults.setProperty("size", "1");
        defaults.setProperty("middle", "false");
        defaults.setProperty("discord", "true");
        defaults.setProperty("visiblerewards", "true");
        defaults.setProperty("coe", "false");
        defaults.setProperty("cor", "0");
        defaults.setProperty("cog", "0");
        defaults.setProperty("cob", "0");
        defaults.setProperty("une", "false");
        defaults.setProperty("unr", "0");
        defaults.setProperty("ung", "0");
        defaults.setProperty("unb", "0");
        defaults.setProperty("rae", "false");
        defaults.setProperty("rar", "0");
        defaults.setProperty("rag", "0");
        defaults.setProperty("rab", "0");
        defaults.setProperty("bae", "true");
        defaults.setProperty("bar", "0.44");
        defaults.setProperty("bag", "0.44");
        defaults.setProperty("bab", "0.44");
        defaults.setProperty("spe", "true");
        defaults.setProperty("spr", "0.31");
        defaults.setProperty("spg", "0.69");
        defaults.setProperty("spb", "0.47");
        defaults.setProperty("ste", "true");
        defaults.setProperty("str", "0.67");
        defaults.setProperty("stg", "0.52");
        defaults.setProperty("stb", "0.54");
        defaults.setProperty("cue", "false");
        defaults.setProperty("cur", "0.30");
        defaults.setProperty("cug", "0.30");
        defaults.setProperty("cub", "0.28");
        config = new SpireConfig(ID, "config", defaults);
        whiteboardEnabled = config.getBool("whiteboard");
        doVisibleRewards = config.getBool("visiblerewards");
        doCalculator = config.getBool("calculator");
        BaseMod.subscribe(new OceanMod());
    }

    public void receiveEditStrings() {
        String lang = "eng";
        for (Settings.GameLanguage i : SupportedLanguages)
            if (i.equals(Settings.language))
                lang = Settings.language.name().toLowerCase();
        
        BaseMod.loadCustomStringsFile(UIStrings.class, resourcePath("localization/"+lang+"/UIStrings.json"));
    }

    public void receivePostInitialize() {
        TEXT = CardCrawlGame.languagePack.getUIString(ID+":badge").TEXT;
        whiteboardPanelItem = new PanelItem();
        if (whiteboardEnabled)
            BaseMod.addTopPanelItem(whiteboardPanelItem);
        whiteboardDrawing = new WhiteboardDrawing();
        BaseMod.subscribe(whiteboardDrawing);
        whiteboardMenu = new Menu();
        //BaseMod.subscribe(whiteboardMenu);
        settingsPanel = new ModPanel();
        int configX = 350;
        int configIndent = configX+50;
        int configY = 800;
        int configStep = 40;
        settingsPanel.addUIElement(new ModLabeledToggleButton(
            TEXT[11],                        configX,configY,Settings.CREAM_COLOR,FontHelper.charDescFont,
            config.getBool("calculator"),    settingsPanel,(label) -> {},(button) -> {
                config.setBool("calculator", button.enabled);
                try {config.save();} catch (Exception e) {}
                doCalculator = button.enabled;

        }));
        configY -= configStep;
        settingsPanel.addUIElement(new ModLabeledToggleButton(
            TEXT[3],                         configX,configY,Settings.CREAM_COLOR,FontHelper.charDescFont,
            config.getBool("whiteboard"),    settingsPanel,(label) -> {},(button) -> {
                config.setBool("whiteboard", button.enabled);
                try {config.save();} catch (Exception e) {}
        }));
        configY -= configStep;
        settingsPanel.addUIElement(new ModLabeledToggleButton(
            TEXT[4],                         configIndent,configY,Settings.CREAM_COLOR,FontHelper.charDescFont,
            config.getBool("middle"),        settingsPanel,(label) -> {},(button) -> {
                config.setBool("middle", button.enabled);
                try {config.save();} catch (Exception e) {}
                whiteboardDrawing.middle = button.enabled;
        }));
        configY -= configStep;
        settingsPanel.addUIElement(new ModLabeledToggleButton(
            TEXT[6],                         configX,configY,Settings.CREAM_COLOR,FontHelper.charDescFont,
            config.getBool("discord"),       settingsPanel,(label) -> {},(button) -> {
                config.setBool("discord", button.enabled);
                try {config.save();} catch (Exception e) {}
        }));
        configY -= configStep;
        settingsPanel.addUIElement(new ModLabeledToggleButton(
            TEXT[10],                         configX,configY,Settings.CREAM_COLOR,FontHelper.charDescFont,
            config.getBool("visiblerewards"), settingsPanel,(label) -> {},(button) -> {
                config.setBool("visiblerewards", button.enabled);
                try {config.save();} catch (Exception e) {}
                doVisibleRewards = button.enabled;
        }));

        float right = 400f * Settings.scale;
        configY = 565;
        exampleCards[0] = new DaggerSpray();
        exampleCards[1] = new DoomAndGloom();
        exampleCards[2] = new Brilliance();
        exampleCards[3] = new Bash();
        exampleCards[4] = new Miracle();
        exampleCards[5] = new Dazed();
        exampleCards[6] = new Injury();
        for (int i = 0; i < BorderColours.rarityStrings.length; i++) {
            exampleCards[i].drawScale = 0.5f;
            exampleCards[i].current_x = Settings.WIDTH/2 + right;
            exampleCards[i].current_y = 370;
            BorderColours.rarityConfigs[i] = OceanMod.config.getBool(BorderColours.rarityStrings[i]+"e");
            BorderColours.colors[i] = new Color(
                OceanMod.config.getFloat(BorderColours.rarityStrings[i]+"r"),
                OceanMod.config.getFloat(BorderColours.rarityStrings[i]+"g"),
                OceanMod.config.getFloat(BorderColours.rarityStrings[i]+"b"), 1.0F);
        }

        borderToggleButton = new ModToggleButton(Settings.WIDTH/2 + right - 18F * Settings.scale,configY,false,false,settingsPanel,(button) -> {
            config.setBool(BorderColours.rarityStrings[rarityEditing]+"e", button.enabled);
            BorderColours.rarityConfigs[rarityEditing] = button.enabled;
            try {config.save();} catch (Exception e) {}
        });
        settingsPanel.addUIElement(borderToggleButton);
        for (int j = 0; j < 3; j++) {
            final Integer inJ = new Integer(j);
            borderColourSliders[j] = new ModMinMaxSlider(
                TEXT[7+j],Settings.WIDTH/2 + right - 115F * Settings.scale,configY-15-30*j,0,1,
                0,null,settingsPanel,(slider) -> {
                    config.setFloat(BorderColours.rarityStrings[rarityEditing]+"rgb".charAt(inJ), slider.getValue());
                    Color color = BorderColours.colors[rarityEditing];
                    float[] rgb = {color.r, color.g, color.b};
                    rgb[inJ] = slider.getValue();
                    color.set(rgb[0], rgb[1], rgb[2], 1.0F);
                    try {config.save();} catch (Exception e) {}
            });
            settingsPanel.addUIElement(borderColourSliders[j]);
        }
        borderLabel = new ModLabel("", Settings.WIDTH/2 + right, configY + 50f * Settings.scale, settingsPanel, (l)->{});
        borderPrevButton = new ModButton(Settings.WIDTH / 2 + right - 150f * Settings.scale, configY + 25f * Settings.scale, ImageMaster.CF_LEFT_ARROW, settingsPanel, b -> selectRarity(-1));
        settingsPanel.addUIElement(borderPrevButton);
        borderNextButton = new ModButton(Settings.WIDTH / 2 + right + 100f * Settings.scale, configY + 25f * Settings.scale, ImageMaster.CF_RIGHT_ARROW, settingsPanel, b -> selectRarity(1));
        settingsPanel.addUIElement(borderNextButton);

        selectRarity(0);

        BaseMod.registerModBadge(new Texture(resourcePath("images/badge.jpg")), TEXT[0], TEXT[1], TEXT[2], settingsPanel);
    }

    public static void selectRarity(int add) {
        rarityEditing += add + BorderColours.rarityStrings.length;
        rarityEditing %= BorderColours.rarityStrings.length;
        borderLabel.text = TEXT[12+rarityEditing];
        borderToggleButton.enabled = config.getBool(BorderColours.rarityStrings[rarityEditing]+"e");
        for (int i = 0; i < borderColourSliders.length; i++)
            borderColourSliders[i].setValue(config.getFloat(BorderColours.rarityStrings[rarityEditing]+"rgb".charAt(i)));
    }

    public void receiveRender(SpriteBatch sb) {
        if (settingsPanel.isUp) {
            exampleCards[rarityEditing].render(sb);
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, borderLabel.text, borderLabel.x, borderLabel.y);
        }
    }

    public static String resourcePath(String path) {
        return ID+"Resources/"+path;
    }

    @SpirePatch(clz=MainMenuScreen.class, method=SpirePatch.CONSTRUCTOR, paramtypez={boolean.class})
    public static class MainMenuPatch {
        public static void Postfix() {
            whiteboardOpen = false;
            whiteboardDrawing.savePixmap();
        }
    }
}