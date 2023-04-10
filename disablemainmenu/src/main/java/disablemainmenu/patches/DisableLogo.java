package disablemainmenu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.scenes.TitleBackground;

@SpirePatch(clz=TitleBackground.class, method="render")
public class DisableLogo {
    @SpireInsertPatch(loc=218)
    public static SpireReturn<Void> Insert() {
        return SpireReturn.Return();
    }
}