package oceanmod;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.rewards.chests.MediumChest;
import com.megacrit.cardcrawl.rewards.chests.LargeChest;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordRPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class DiscordClient {
    public static long startTime = -1L;
    public static ArrayList<String> portraits = new ArrayList<String>(Arrays.asList("ironclad", "silent", "defect", "watcher", "hermit", "slimebound", "guardian", "hexaghost", "champ", "automaton", "gremlin", "snecko", "unchained"));
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(OceanMod.ID+":discord").TEXT;
    private static final boolean enabled = OceanMod.config.getBool("discord");

    public static void UpdatePresence(String details, String state, String image, String smallImage, boolean showTime) {
        if (!enabled) return;
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(state)
            .setDetails(details)
            .setBigImage(image, "Slay the Spire");
        if (showTime)
            builder.setStartTimestamps(showTime ? startTime : null);
        if (smallImage != "")
            builder.setSmallImage(smallImage, null);
        DiscordRichPresence rich = builder.build();
        DiscordRPC.discordUpdatePresence(rich);
        System.out.println("set discord rich presence");
    }

    public static void FloorInfo() {
        if (startTime == -1L)
            return;
        if (AbstractDungeon.currMapNode == null)
            return;

        boolean hasKeys = Settings.isFinalActAvailable && Settings.hasRubyKey && Settings.hasEmeraldKey && Settings.hasSapphireKey;
        String roomType;
        String smallImage;

        if (AbstractDungeon.currMapNode.room instanceof NeowRoom) {
            roomType = TEXT[0];
            smallImage = "neow";
        } else if (AbstractDungeon.currMapNode.room instanceof MonsterRoom) {
            String enemyType;
            if (AbstractDungeon.currMapNode.room instanceof MonsterRoomBoss) {
                enemyType = TEXT[3];
                smallImage = "boss";
            } else if (AbstractDungeon.currMapNode.room instanceof MonsterRoomElite) {
                enemyType = TEXT[2];
                smallImage = "elite";
            } else {
                enemyType = TEXT[1];
                smallImage = "enemy";
            }
            roomType = enemyType + " - " + Pattern.compile(".+:", Pattern.MULTILINE).matcher(AbstractDungeon.lastCombatMetricKey).replaceAll("") + " - " + TEXT[21] + " " + GameActionManager.turn;
        }
        else if (AbstractDungeon.currMapNode.room instanceof RestRoom) {
            roomType = TEXT[4];
            smallImage = "rest";
        } else if (AbstractDungeon.currMapNode.room instanceof ShopRoom) {
            roomType = TEXT[5] + " - " + AbstractDungeon.player.gold + TEXT[6];
            smallImage = "shop";
        } else if (AbstractDungeon.currMapNode.room instanceof TreasureRoom) {
            String chestSize;
            if (((TreasureRoom)AbstractDungeon.currMapNode.room).chest instanceof LargeChest)
                chestSize = TEXT[10];
            else if (((TreasureRoom)AbstractDungeon.currMapNode.room).chest instanceof MediumChest)
                chestSize = TEXT[9];
            else
                chestSize = TEXT[8];
            roomType = TEXT[7] + " - " + chestSize;
            smallImage = "chest";
        }
        else if (AbstractDungeon.currMapNode.room instanceof TreasureRoomBoss) {
            roomType = TEXT[11];
            smallImage = "bosschest";
        }
        else if (AbstractDungeon.currMapNode.room instanceof VictoryRoom) {
            roomType = hasKeys ? TEXT[12] : TEXT[13];
            smallImage = "win";
        } else if (AbstractDungeon.currMapNode.room instanceof TrueVictoryRoom) {
            roomType = TEXT[14];
            smallImage = "win";
        } else if (AbstractDungeon.currMapNode.room instanceof EventRoom) {
            AbstractEvent event = AbstractDungeon.currMapNode.room.event;
            if (event == null)
                return;
            roomType = TEXT[15] + " - " + Pattern.compile(".+\\.", Pattern.MULTILINE).matcher(event.getClass().getName()).replaceAll("");
            smallImage = "event";
        }
        else {
            try {
                roomType = TEXT[16] + " - " + Pattern.compile(".+\\.", Pattern.MULTILINE).matcher(AbstractDungeon.currMapNode.room.getClass().getName()).replaceAll("");
            } catch (Exception e) {
                e.printStackTrace();
                roomType = TEXT[16];
            }
            smallImage = "win";
        }
        
        String portrait = AbstractDungeon.player.getClass().getSimpleName().toLowerCase().replace("the", "").replace("character", "").replace("char", "").replaceAll(" ", "");
        
        UpdatePresence(AbstractDungeon.player.title.substring(0, 1).toUpperCase() + AbstractDungeon.player.title.substring(1) + " - " + TEXT[17] + AbstractDungeon.ascensionLevel + (hasKeys ? TEXT[18] : "") + " - " + AbstractDungeon.player.currentHealth + "/" + AbstractDungeon.player.maxHealth + TEXT[19], TEXT[20] + AbstractDungeon.floorNum + " - " + roomType, portraits.contains(portrait) ? portrait : "cover", smallImage, true);
    }
}