package stringrandomiser;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@SpireInitializer
@SpirePatch(clz=LocalizedStrings.class, method=SpirePatch.CONSTRUCTOR)
public class StringRandomiser implements PostUpdateSubscriber {
    private static String[] stringsToGet = new String[] {"monsters", "powers", "cards", "relics", "events", "potions", "credits", "tutorials", "keywords", "scoreBonuses", "characters", "ui", "orb", "stance", "mod", "blights", "achievements"};
    private static ArrayList<String> exclude = new ArrayList<>(Arrays.asList("DailyScreen", "RichPresence", "RunHistoryScreen", "RunHistoryPathNodes"));
    private static ArrayList<String> allStrings;

    public static void initialize() {
        BaseMod.subscribe(new StringRandomiser());
    }

    public void receivePostUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9))
            Randomise();
    }

    private static void add(String s) {
        if (s != null)
            allStrings.add(s);
    }

    private static void getStrings(Object s) {
        try {
            for (Field f : s.getClass().getDeclaredFields()) {
                if (f.getType() == String.class)
                    add((String)f.get(s));
                else if (f.getType() == String[].class)
                    for (String str : (String[])f.get(s))
                        add(str);
            }
        } catch (Exception e) {}
    }

    private static String getRandomString() {
        return allStrings.get(MathUtils.random(0, allStrings.size() - 1));
    }

    private static void randomiseStrings(Object s) {
        try {
            for (Field f : s.getClass().getDeclaredFields()) {
                if (f.getType() == String.class)
                    f.set(s, getRandomString());
                else if (f.getType() == String[].class) {
                    String[] arr = (String[])f.get(s);
                    for (int i = 0; i < arr.length; i++)
                        arr[i] = getRandomString();
                }
            }
        } catch (Exception e) {}
    }

    public static void Postfix() {
        if (allStrings == null) {
            allStrings = new ArrayList<>();
            for (String stringsMap : stringsToGet)
                for (Object s : ((Map<String, Object>)ReflectionHacks.getPrivateStatic(LocalizedStrings.class, stringsMap)).values())
                    getStrings(s);
            Randomise();
        }
    }

    public static void Randomise() {
        for (String stringsMap : stringsToGet)
            for (Map.Entry<String, Object> s : ((Map<String, Object>)ReflectionHacks.getPrivateStatic(LocalizedStrings.class, stringsMap)).entrySet())
                if (!exclude.contains(s.getKey()))
                    randomiseStrings(s.getValue());
    }
}