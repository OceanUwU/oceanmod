package oceanmod;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.relics.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpireInitializer
public class BetaRelics {
    public static SpireConfig config;
    public static Map<String, ArrayList<String>> betas = new HashMap<>();

    static {
        //example: BetaRelics.register(BloodVial.ID, "betarelicsResources/images/normal/bloodVial.png", "betarelicsResources/images/outline/bloodVial.png", "betarelicsResources/images/large/bloodVial.png");

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

    public static void initialize() {
        Properties defaults = new Properties();
        for (String relicId : betas.keySet())
            defaults.setProperty(relicId, "false");
        try {
            config = new SpireConfig(OceanMod.ID, "betarelics", defaults);
        } catch(Exception e) {}
    }

    public static void register(String relicId, String texturePath, String outlineTexturePath, String largeTexturePath) {
        betas.put(relicId, new ArrayList<>(Arrays.asList(texturePath, outlineTexturePath, largeTexturePath)));
    }

    private static void registerBetaRelicsRelic(String relicId, String textureName) {
        String dir = OceanMod.resourcePath("images/betarelics/");
        String file = "/"+textureName+".png";
        BetaRelics.register(relicId, dir+"normal"+file, dir+"outline"+file, dir+"large"+file);
    }
}