package oceanmod;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import oceanmod.ui.calculator.Calculator;
import oceanmod.ui.calculator.PanelItem;

import java.util.ArrayList;

@SpireInitializer
public class CalculatorManager implements PostInitializeSubscriber {
    public static ArrayList<Calculator> calculators = new ArrayList<>();

    public static void initialize() {
        BaseMod.subscribe(new CalculatorManager());
    }

    public void receivePostInitialize() {
        if (OceanMod.doCalculator)
            BaseMod.addTopPanelItem(new PanelItem());
    }

    public static void openCalculator(float x, float y) {
        calculators.add(new Calculator(x, y));
    }

    public static void closeCalculators() {
        while (calculators.size() > 0) {
            calculators.get(0).close();
        }
    }

    @SpirePatch(
        clz=MainMenuScreen.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez={boolean.class}
    )
    public static class MainMenuPatch {
        public static void Postfix() {
            closeCalculators();
        }
    }
}