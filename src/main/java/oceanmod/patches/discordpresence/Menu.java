package oceanmod.patches.discordpresence;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import oceanmod.DiscordClient;

import java.util.ArrayList;

@SpirePatch(
    clz=MainMenuScreen.class,
    method=SpirePatch.CONSTRUCTOR,
    paramtypez={
        boolean.class
    }
)
public class Menu {
    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
        case 11:
        case 12:
        case 13:
            return i + "th";
        default:
            return i + suffixes[i % 10];

        }
    }

    @SpirePostfixPatch
    public static void Postfix() {
        int totalRuns = 0;
        ArrayList<CharStat> allCharStats = CardCrawlGame.characterManager.getAllCharacterStats();
        for (CharStat cs : allCharStats) {
            totalRuns += cs.getVictoryCount();
            totalRuns += cs.getDeathCount(); 
        }
        DiscordClient.startTime = -1L;
        DiscordClient.UpdatePresence("Main menu", "Gearing up for their " + ordinal(totalRuns+1) + " run", "cover", "", false);
    }
}