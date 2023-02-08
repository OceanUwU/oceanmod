package oceanmod.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import oceanmod.OceanMod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BorderColours {
    public static String[] rarityStrings = {"co", "un", "ra", "ba", "sp", "st", "cu"};
    public static boolean[] rarityConfigs = new boolean[rarityStrings.length];
    public static Color[] colors = new Color[rarityStrings.length];

    private static TextureAtlas ucbCardUiAtlas;
    private static TextureAtlas.AtlasRegion FRAME_ATTACK;
    private static TextureAtlas.AtlasRegion FRAME_SKILL;
    private static TextureAtlas.AtlasRegion FRAME_POWER;
    private static TextureAtlas.AtlasRegion BANNER;
    private static TextureAtlas.AtlasRegion FRAME_ATTACK_L;
    private static TextureAtlas.AtlasRegion FRAME_SKILL_L;
    private static TextureAtlas.AtlasRegion FRAME_POWER_L;
    private static TextureAtlas.AtlasRegion BANNER_L;

    private static Method renderHelperMethod;
    private static Field renderColorField;
    private static Method popupRenderHelperMethod;
    private static Field popupCardField;

    static {
        ucbCardUiAtlas = new TextureAtlas(Gdx.files.internal(OceanMod.resourcePath("images/borders/uniquebordercardui.atlas")));
        FRAME_ATTACK = ucbCardUiAtlas.findRegion("512/frame_attack_rare");
        FRAME_SKILL = ucbCardUiAtlas.findRegion("512/frame_skill_rare");
        FRAME_POWER = ucbCardUiAtlas.findRegion("512/frame_power_rare");
        BANNER = ucbCardUiAtlas.findRegion("512/banner_rare");
        FRAME_ATTACK_L = ucbCardUiAtlas.findRegion("1024/frame_attack_rare");
        FRAME_SKILL_L = ucbCardUiAtlas.findRegion("1024/frame_skill_rare");
        FRAME_POWER_L = ucbCardUiAtlas.findRegion("1024/frame_power_rare");
        BANNER_L = ucbCardUiAtlas.findRegion("1024/banner_rare");

        try {
            renderHelperMethod = AbstractCard.class.getDeclaredMethod("renderHelper", new Class[] { SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class });
            renderHelperMethod.setAccessible(true);
            renderColorField = AbstractCard.class.getDeclaredField("renderColor");
            renderColorField.setAccessible(true);
            popupRenderHelperMethod = SingleCardViewPopup.class.getDeclaredMethod("renderHelper", new Class[] { SpriteBatch.class, float.class, float.class, TextureAtlas.AtlasRegion.class });
            popupRenderHelperMethod.setAccessible(true);
            popupCardField = SingleCardViewPopup.class.getDeclaredField("card");
            popupCardField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color getColor(AbstractCard c) {
        int n = -1;
        switch (c.rarity) {
            case COMMON: n = 0; break;
            case UNCOMMON: n = 1; break;
            case RARE: n = 2; break;
            case BASIC: n = 3; break;
            case SPECIAL: n = 4; break;
            case CURSE: n = 6; break;
        }
        if (c.type == AbstractCard.CardType.STATUS) n = 5;
        if (n == -1) return null;
        if (rarityConfigs[n]) return colors[n];
        return null;
    }

    @SpirePatch(
        clz=AbstractCard.class,
        method="renderImage"
    )
    public static class SmallCardBorderPatch {
        public static void Postfix(AbstractCard c, SpriteBatch sb, boolean hovered, boolean selected) {
            try {
                TextureAtlas.AtlasRegion frame;
                switch (c.type) {
                    case ATTACK:
                        frame = FRAME_ATTACK;
                        break;
                    case POWER:
                        frame = FRAME_POWER;
                        break;
                    default:
                        frame = FRAME_SKILL;
                        break;
                }
                Color color = getColor(c);
                if (color == null) return;
                Color renderColor = new Color(color.r, color.g, color.b, ((Color)renderColorField.get(c)).a);
                renderHelperMethod.invoke(c, new Object[] { sb, renderColor, frame, c.current_x, c.current_y });
                renderHelperMethod.invoke(c, new Object[] { sb, renderColor, BANNER, c.current_x, c.current_y });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
        clz=SingleCardViewPopup.class,
        method="renderCardBanner"
    )
    public static class LargeCardBorderPatch {
        public static void Postfix(SingleCardViewPopup popup, SpriteBatch sb) {
            try {
                AbstractCard c = ((AbstractCard)popupCardField.get(popup));
                TextureAtlas.AtlasRegion frame;
                switch (c.type) {
                    case ATTACK:
                        frame = FRAME_ATTACK_L;
                        break;
                    case POWER:
                        frame = FRAME_POWER_L;
                        break;
                    default:
                        frame = FRAME_SKILL_L;
                        break;
                }
                Color color = getColor(c);
                if (color == null) return;
                sb.setColor(color);
                popupRenderHelperMethod.invoke(popup, new Object[] { sb, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, frame});
                popupRenderHelperMethod.invoke(popup, new Object[] { sb, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, BANNER_L});
                sb.setColor(Color.WHITE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}