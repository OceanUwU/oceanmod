package oceanmod;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.*;
import oceanmod.patches.betarelics.RelicImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpireInitializer
public class BetaRelics implements PostInitializeSubscriber {
    public static SpireConfig config;
    public static Map<String, ArrayList<Object>> betas = new HashMap<>();

    public static void initialize() {
        BaseMod.subscribe(new BetaRelics());
        registerBetaRelicsRelics();
    }

    public void receivePostInitialize() {
        Properties defaults = new Properties();
        for (String relicId : betas.keySet())
            defaults.setProperty(relicId, "false");
        try {
            config = new SpireConfig(OceanMod.ID, "betarelics", defaults);
        } catch(Exception e) {}
        RelicImage.initializeLibraryRelics();
    }

    public static void register(String relicId, String texturePath, String outlineTexturePath) {
        betas.put(relicId, new ArrayList<>(Arrays.asList(texturePath, outlineTexturePath)));
    }

    private static void registerBetaRelicsRelic(String relicId, String textureName) {
        String dir = OceanMod.resourcePath("images/betarelics/");
        String file = "/"+textureName+".png";
        BetaRelics.register(relicId, dir+"normal"+file, dir+"outline"+file);
    }

    public static Texture getSpecificTexture(String relicId, int n) {
        ArrayList<Object> textures = betas.get(relicId);
        if (textures.get(n) instanceof String)
            textures.set(n, ImageMaster.loadImage((String)textures.get(n)));
        return (Texture)betas.get(relicId).get(n);
    }

    public static Texture getTexture(String relicId) {
        return getSpecificTexture(relicId, 0);
    }

    public static Texture getOutline(String relicId) {
        return getSpecificTexture(relicId, 1);
    }

    public static void registerBetaRelicsRelics() {
        registerBetaRelicsRelic(AncientTeaSet.ID, "teaSet");
        registerBetaRelicsRelic(BirdFacedUrn.ID, "ancientUrn");
        registerBetaRelicsRelic(BagOfPreparation.ID, "bagOfPreparation");
        registerBetaRelicsRelic(BloodVial.ID, "bloodVial");
        registerBetaRelicsRelic(BottledFlame.ID, "flameBottle");
        registerBetaRelicsRelic(BottledLightning.ID, "bottledLightning");
        registerBetaRelicsRelic(EternalFeather.ID, "eternalFeather");
        registerBetaRelicsRelic(OddlySmoothStone.ID, "smoothStone");
        registerBetaRelicsRelic(Orichalcum.ID, "orichalcum");
        registerBetaRelicsRelic(PandorasBox.ID, "pandorasBox");
        registerBetaRelicsRelic(RegalPillow.ID, "pillow");
        registerBetaRelicsRelic(RedSkull.ID, "redSkull");
        registerBetaRelicsRelic(RingOfTheSerpent.ID, "serpentRing");
        registerBetaRelicsRelic(SnakeRing.ID, "snakeRing");
        registerBetaRelicsRelic(Sundial.ID, "sundial");
        registerBetaRelicsRelic(TheSpecimen.ID, "specimen");
        registerBetaRelicsRelic(Toolbox.ID, "toolbox");
        registerBetaRelicsRelic(ToughBandages.ID, "toughBandages");
        registerBetaRelicsRelic(Vajra.ID, "vajra");
        registerBetaRelicsRelic(Whetstone.ID, "whetstone");
        registerBetaRelicsRelic(WhiteBeast.ID, "whiteElephant");
    }
}