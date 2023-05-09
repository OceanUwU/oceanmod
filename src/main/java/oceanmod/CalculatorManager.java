package oceanmod;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.PreUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import oceanmod.ui.calculator.Calculator;
import oceanmod.ui.calculator.PanelItem;

import java.util.ArrayList;

@SpireInitializer
public class CalculatorManager implements RenderSubscriber, PreUpdateSubscriber, PostUpdateSubscriber, PostInitializeSubscriber {
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

    public void receiveRender(SpriteBatch sb) {
        for (Calculator c : calculators)
            c.render(sb);
    }

    public void receivePreUpdate() {
        for (int i = calculators.size() - 1; i >= 0; i--) //front to back
            calculators.get(i).update();
    }

    public void receivePostUpdate() {
        Calculator.postUpdate();
    }

    @SpirePatch(clz=MainMenuScreen.class, method=SpirePatch.CONSTRUCTOR, paramtypez={boolean.class})
    public static class MainMenuPatch {
        public static void Postfix() {
            calculators.clear();
        }
    }
}