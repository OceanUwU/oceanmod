package disablemainmenu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

@SpirePatch(clz=MainMenuScreen.class, method="render")
public class DisableMainMenu {
    @SpireInsertPatch(loc=518)
    public static SpireReturn<Void> Insert() {
        return SpireReturn.Return();
    }
}