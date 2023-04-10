package disablemainmenu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.GameCursor;

@SpirePatch(clz=GameCursor.class, method="render")
public class DisableCursor {
    public static SpireReturn<Void> Prefix() {
        return SpireReturn.Return();
    }
}