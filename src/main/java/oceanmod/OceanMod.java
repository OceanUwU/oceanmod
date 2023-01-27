package oceanmod;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import basemod.ModToggleButton;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
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
    public static boolean whiteboardOpen = false;

    private AbstractCard exampleBasic;
    private AbstractCard exampleSpecial;
    private AbstractCard exampleStatus;
    private AbstractCard exampleCurse;

    public static void initialize() throws IOException {
        defaults.setProperty("whiteboard", "true");
        defaults.setProperty("calculator", "true");
        defaults.setProperty("color", "0");
        defaults.setProperty("size", "1");
        defaults.setProperty("middle", "false");
        defaults.setProperty("discord", "true");
        defaults.setProperty("visiblerewards", "true");
        defaults.setProperty("be", "true");
        defaults.setProperty("br", "0.44");
        defaults.setProperty("bg", "0.44");
        defaults.setProperty("bb", "0.44");
        defaults.setProperty("spe", "true");
        defaults.setProperty("spr", "0.31");
        defaults.setProperty("spg", "0.69");
        defaults.setProperty("spb", "0.47");
        defaults.setProperty("ste", "true");
        defaults.setProperty("str", "0.67");
        defaults.setProperty("stg", "0.52");
        defaults.setProperty("stb", "0.54");
        defaults.setProperty("ce", "false");
        defaults.setProperty("cr", "0.30");
        defaults.setProperty("cg", "0.30");
        defaults.setProperty("cb", "0.28");
        config = new SpireConfig(ID, "config", defaults);
        whiteboardEnabled = config.getBool("whiteboard");
        doVisibleRewards = config.getBool("visiblerewards");
        doCalculator = config.getBool("visiblerewards");
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
        String[] TEXT = CardCrawlGame.languagePack.getUIString(ID+":badge").TEXT;
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

        configY = 565;
        settingsPanel.addUIElement(new ModToggleButton(Settings.WIDTH/2 - 450 - 18F * Settings.scale,configY,config.getBool("be"),false,settingsPanel,(button) -> {
                config.setBool("be", button.enabled);
                BorderColours.doBasic = button.enabled;
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModToggleButton(Settings.WIDTH/2 - 150 - 18F * Settings.scale,configY,config.getBool("spe"),false,settingsPanel,(button) -> {
                config.setBool("spe", button.enabled);
                BorderColours.doSpecial = button.enabled;
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModToggleButton(Settings.WIDTH/2 + 150 - 18F * Settings.scale,configY,config.getBool("ste"),false,settingsPanel,(button) -> {
                config.setBool("ste", button.enabled);
                BorderColours.doStatus = button.enabled;
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModToggleButton(Settings.WIDTH/2 + 450 - 18F * Settings.scale,configY,config.getBool("ce"),false,settingsPanel,(button) -> {
                config.setBool("ce", button.enabled);
                BorderColours.doCurse = button.enabled;
                try {config.save();} catch (Exception e) {}
        }));
        configY -=15;
        settingsPanel.addUIElement(new ModMinMaxSlider(
            TEXT[7],                         Settings.WIDTH/2 - 450 - 115F * Settings.scale,configY,0,1,
            config.getFloat("br")," ",settingsPanel,(slider) -> {
                config.setFloat("br", slider.getValue());
                BorderColours.basic.r = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 - 150 - 115F * Settings.scale,configY,0,1,
            config.getFloat("spr")," ",settingsPanel,(slider) -> {
                config.setFloat("spr", slider.getValue());
                BorderColours.special.r = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 + 150 - 115F * Settings.scale,configY,0,1,
            config.getFloat("str")," ",settingsPanel,(slider) -> {
                config.setFloat("str", slider.getValue());
                BorderColours.status.r = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 + 450 - 115F * Settings.scale,configY,0,1,
            config.getFloat("cr")," ",settingsPanel,(slider) -> {
                config.setFloat("cr", slider.getValue());
                BorderColours.curse.r = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        configY -=30;
        settingsPanel.addUIElement(new ModMinMaxSlider(
            TEXT[8],                         Settings.WIDTH/2 - 450 - 115F * Settings.scale,configY,0,1,
            config.getFloat("bg")," ",settingsPanel,(slider) -> {
                config.setFloat("bg", slider.getValue());
                BorderColours.basic.g = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 - 150 - 115F * Settings.scale,configY,0,1,
            config.getFloat("spg")," ",settingsPanel,(slider) -> {
                config.setFloat("spg", slider.getValue());
                BorderColours.special.g = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 + 150 - 115F * Settings.scale,configY,0,1,
            config.getFloat("stg")," ",settingsPanel,(slider) -> {
                config.setFloat("stg", slider.getValue());
                BorderColours.status.g = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 + 450 - 115F * Settings.scale,configY,0,1,
            config.getFloat("cg")," ",settingsPanel,(slider) -> {
                config.setFloat("cg", slider.getValue());
                BorderColours.curse.g = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        configY -=30;
        settingsPanel.addUIElement(new ModMinMaxSlider(
            TEXT[9],                         Settings.WIDTH/2 - 450 - 115F * Settings.scale,configY,0,1,
            config.getFloat("bb")," ",settingsPanel,(slider) -> {
                config.setFloat("bb", slider.getValue());
                BorderColours.basic.b = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 - 150 - 115F * Settings.scale,configY,0,1,
            config.getFloat("spb")," ",settingsPanel,(slider) -> {
                config.setFloat("spb", slider.getValue());
                BorderColours.special.b = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 + 150 - 115F * Settings.scale,configY,0,1,
            config.getFloat("stb")," ",settingsPanel,(slider) -> {
                config.setFloat("stb", slider.getValue());
                BorderColours.status.b = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        settingsPanel.addUIElement(new ModMinMaxSlider(
            "",Settings.WIDTH/2 + 450 - 115F * Settings.scale,configY,0,1,
            config.getFloat("cb")," ",settingsPanel,(slider) -> {
                config.setFloat("cb", slider.getValue());
                BorderColours.curse.b = slider.getValue();
                try {config.save();} catch (Exception e) {}
        }));
        configY -=30;

        BaseMod.registerModBadge(new Texture(resourcePath("images/badge.jpg")), TEXT[0], TEXT[1], TEXT[2], settingsPanel);
    }

    public void receiveRender(SpriteBatch sb) {
        if (settingsPanel.isUp) {
            if (exampleBasic == null) {
                exampleBasic = new Bash();
                exampleSpecial = new Miracle();
                exampleStatus = new Dazed();
                exampleCurse = new Injury();
                exampleBasic.current_x = Settings.WIDTH/2 - 450;
                exampleBasic.current_y = 370;
                exampleBasic.drawScale = 0.5f;
                exampleSpecial.current_x = Settings.WIDTH/2 - 150;
                exampleSpecial.current_y = 370;
                exampleSpecial.drawScale = 0.5f;
                exampleStatus.current_x = Settings.WIDTH/2 + 150;
                exampleStatus.current_y = 370;
                exampleStatus.drawScale = 0.5f;
                exampleCurse.current_x = Settings.WIDTH/2 + 450;
                exampleCurse.current_y = 370;
                exampleCurse.drawScale = 0.5f;
                System.out.println(exampleBasic.current_x);
                System.out.println(exampleBasic.current_x);
                System.out.println(exampleBasic.current_x);
                System.out.println(exampleBasic.current_x);
            }
            exampleBasic.render(sb);
            exampleSpecial.render(sb);
            exampleStatus.render(sb);
            exampleCurse.render(sb);
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