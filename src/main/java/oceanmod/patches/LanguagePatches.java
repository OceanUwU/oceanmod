package oceanmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;

import java.util.Arrays;

public class LanguagePatches {
    @SpireEnum
    public static Settings.GameLanguage CYM;

    @SpirePatch(clz=OptionsPanel.class, method="LanguageOptions")
    public static class ChangeOptions {
        public static Settings.GameLanguage[] Postfix(Settings.GameLanguage[] __result) {
            Settings.GameLanguage[] options = Arrays.copyOf(__result, __result.length+1);
            options[options.length-1] = CYM;
            return options;
        }
    }

    @SpirePatch(clz=OptionsPanel.class, method="languageLabels")
    public static class ChangeLabels {
        public static String[] Postfix(String[] __result) {
            String[] labels = Arrays.copyOf(__result, __result.length+1);
            labels[labels.length-1] = Settings.language == CYM ? "Cymraeg" : "Welsh";
            return labels;
        }
    }

    @SpirePatch(clz=LocalizedStrings.class, method=SpirePatch.CONSTRUCTOR)
    public static class FindPath {
        @SpireInsertPatch(loc=136, localvars={"langPackDir"})
        public static void Insert(LocalizedStrings __instance, @ByRef String[] langPackDir) {
            if (Settings.language == CYM)
                langPackDir[0] = "localization/cym";
        }
    }
}