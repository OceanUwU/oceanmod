package aaaaamod.patches;

import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(clz=BitmapFontCache.class, method="addText", paramtypez={CharSequence.class, float.class, float.class, int.class, int.class, float.class, int.class, boolean.class, String.class})
public class A {
    public static void Prefix(BitmapFontCache __instance, @ByRef CharSequence[] str) {
        str[0] = new String(new char[str[0].length()]).replace("\0", "a");
    }
}