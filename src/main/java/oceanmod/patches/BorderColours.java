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
    public static Color basic = new Color(OceanMod.config.getFloat("br"), OceanMod.config.getFloat("bg"), OceanMod.config.getFloat("bb"), 1.0F);
    public static Color special = new Color(OceanMod.config.getFloat("spr"), OceanMod.config.getFloat("spg"), OceanMod.config.getFloat("spb"), 1.0F);
    public static Color status = new Color(OceanMod.config.getFloat("str"), OceanMod.config.getFloat("stg"), OceanMod.config.getFloat("stb"), 1.0F);
    public static Color curse = new Color(OceanMod.config.getFloat("cr"), OceanMod.config.getFloat("cg"), OceanMod.config.getFloat("cb"), 1.0F);
    public static boolean doBasic = OceanMod.config.getBool("be");
    public static boolean doSpecial = OceanMod.config.getBool("spe");
    public static boolean doStatus = OceanMod.config.getBool("ste");
    public static boolean doCurse = OceanMod.config.getBool("ce");

    private static TextureAtlas ucbCardUiAtlas;
    public static TextureAtlas.AtlasRegion FRAME_ATTACK;
    public static TextureAtlas.AtlasRegion FRAME_SKILL;
    public static TextureAtlas.AtlasRegion FRAME_POWER;
    public static TextureAtlas.AtlasRegion BANNER;
    public static TextureAtlas.AtlasRegion FRAME_ATTACK_L;
    public static TextureAtlas.AtlasRegion FRAME_SKILL_L;
    public static TextureAtlas.AtlasRegion FRAME_POWER_L;
    public static TextureAtlas.AtlasRegion BANNER_L;

    public static Method renderHelperMethod;
    public static Field renderColorField;
    public static Method popupRenderHelperMethod;
    public static Field popupCardField;
    
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
        if (c.type == AbstractCard.CardType.STATUS) {
            if (doStatus) return status;
            return null;
        }
        switch (c.rarity) {
            case BASIC:
                if (doBasic) return basic;
                return null;
            case SPECIAL:
                if (doSpecial) return special;
                return null;
            case CURSE:
                if (doCurse) return curse;
                return null;
            default:
                return null;
        }
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