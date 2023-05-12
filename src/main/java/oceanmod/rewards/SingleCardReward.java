package oceanmod.rewards;

import oceanmod.VisibleCardRewards;
import oceanmod.patches.visiblecardrewards.CardDeletionPrevention;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomReward;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.CardRewardSkipButtonRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import pansTrinkets.cards.AbstractTrinket;
import pansTrinkets.helpers.TrinketHelper;
import sneckomod.cards.unknowns.AbstractUnknownCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_SINGLECARDREWARD;
import static oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_BOWLREWARD;

public class SingleCardReward extends CustomReward {
    private static final float XOFFSET = 25f * Settings.scale;
    public static ArrayList<RewardItem> rewardsToRemove = new ArrayList<RewardItem>();
    public static ArrayList<RewardItem> rewardsToAdd = new ArrayList<RewardItem>();
    public List<SingleCardReward> cardLinks = new ArrayList<>();
    public AbstractCard card;
    public AbstractCard renderCard;
    private boolean discounted = false;
    public boolean isTrinket = false;
    public boolean skipRecorded = false;
    private float switchPreviewTimer = 1.0F;
    private int previewing = 0;
    private Texture rewardTexture;
    public AbstractRelic skipRelic;
    public boolean converting = false;

    public SingleCardReward(AbstractCard c) {
        super((Texture)null, "", VCR_SINGLECARDREWARD);
        card = c;
        init();
    }

    public SingleCardReward(String buttonText, Texture texture, AbstractRelic relic) {
        super((Texture)null, "", VCR_BOWLREWARD);
        text = buttonText;
        rewardTexture = texture;
        skipRelic = relic;
        init();
    }

    protected void init() {
        if (type == VCR_SINGLECARDREWARD) {
            for(AbstractRelic r: AbstractDungeon.player.relics)
                r.onPreviewObtainCard(card);

            renderCard = card.makeStatEquivalentCopy();
            text = card.name;
        }
    }

    public void addCardLink(SingleCardReward setCardLink) {
        if (!cardLinks.contains(setCardLink)) {
            cardLinks.add(setCardLink);
        }
        if (!setCardLink.cardLinks.contains(this)) {
            setCardLink.cardLinks.add(this);
        }
    }

    private boolean isFirst() {
        //if (AbstractDungeon.getCurrRoom().rewards.indexOf(this) > AbstractDungeon.getCurrRoom().rewards.indexOf(relicLink)) {
        int thisIndexOf = AbstractDungeon.combatRewardScreen.rewards.indexOf(this);
        for (SingleCardReward link : cardLinks) {
            if (AbstractDungeon.combatRewardScreen.rewards.indexOf(link) < thisIndexOf) {
                return false;
            }
        }
        return true;
    }

    public boolean isLast() {
        int thisIndexOf = AbstractDungeon.combatRewardScreen.rewards.indexOf(this);
        for (SingleCardReward link : cardLinks) {
            if (AbstractDungeon.combatRewardScreen.rewards.indexOf(link) > thisIndexOf) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean claimReward() {
        if (converting)
            return true;
        if (isTrinket && ((AbstractTrinket)card).weight + TrinketHelper.carriedWeight(AbstractDungeon.player) > TrinketHelper.maxWeight)
            return false;
        
        recordMetrics();
        if (type == VCR_SINGLECARDREWARD) {
            ShowCardAndObtainEffect effect = new ShowCardAndObtainEffect(renderCard, renderCard.current_x, renderCard.current_y);
            CardDeletionPrevention.FromSingleRewardField.single.set(effect, true);
            AbstractDungeon.topLevelEffects.add(effect);
            (AbstractDungeon.getCurrRoom()).souls.obtain(renderCard, true);
            for (AbstractRelic r : AbstractDungeon.player.relics)
                r.onMasterDeckChange(); 
        } else if (type == VCR_BOWLREWARD) {
            if (skipRelic instanceof SingingBowl)
                (new SingingBowlButton()).onClick();
            else
                ((CardRewardSkipButtonRelic)skipRelic).onClickedButton();
        }
        for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
            for (SingleCardReward link : cardLinks) {
                if (reward == link)
                    rewardsToRemove.add(reward);
            }
        }
        return true;
    }
    
    @Override
    public void update() {
        super.update();

        for (Iterator<SingleCardReward> i = cardLinks.iterator(); i.hasNext(); ) {
            SingleCardReward link = i.next();
            if (!AbstractDungeon.combatRewardScreen.rewards.contains(link))
                i.remove();
        }

        if (isFirst()) {
            redText = false;
            for (SingleCardReward link : cardLinks)
                link.redText = false;
        }

        if (hb.hovered) {
            for (SingleCardReward link : cardLinks)
                link.redText = hb.hovered;
            if (InputHelper.justClickedRight && type == VCR_SINGLECARDREWARD)
                CardCrawlGame.cardPopup.open(renderCard);
            if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                CardCrawlGame.sound.playV("CARD_OBTAIN", 0.4F);
                ArrayList<AbstractCard> cards = new ArrayList<>();
                cards.add(card);
                for (SingleCardReward link : cardLinks)
                    cards.add(link.card);
                rewardsToRemove.add(this);
                rewardsToRemove.addAll(cardLinks);
                RewardItem cardReward = new RewardItem(0);
                cardReward.type = RewardType.CARD;
                ReflectionHacks.setPrivate(cardReward, RewardItem.class, "isBoss", AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss);
                cardReward.cards = cards;
                cardReward.text = RewardItem.TEXT[2];
                rewardsToAdd.add(cardReward);
            }
        }

        switchPreviewTimer -= Gdx.graphics.getDeltaTime();
        if (switchPreviewTimer < 0.0F) {
            switchPreviewTimer = 2.0F;
            if (type == VCR_SINGLECARDREWARD && !MultiCardPreview.multiCardPreview.get(renderCard).isEmpty())
                renderCard.cardsToPreview = MultiCardPreview.multiCardPreview.get(renderCard).get(++previewing % MultiCardPreview.multiCardPreview.get(renderCard).size());
        }

        if (hb.justHovered && InputHelper.isMouseDown_R)
            discounted = !discounted;
    }

    private void recordMetrics() {
        HashMap<String, Object> choice = new HashMap<>();
        ArrayList<String> notpicked = new ArrayList<>();
        for (SingleCardReward reward : cardLinks)
            if (reward.type == VCR_SINGLECARDREWARD)
                notpicked.add(reward.card.getMetricID()); 
        if (type == VCR_SINGLECARDREWARD)
            choice.put("picked", card.getMetricID());
        else if (type == VCR_BOWLREWARD)
            choice.put("picked", "Singing Bowl");
        choice.put("not_picked", notpicked);
        choice.put("floor", Integer.valueOf(AbstractDungeon.floorNum));
        CardCrawlGame.metricData.card_choices.add(choice);
    }

    public void recordSkipMetrics() {
        if (skipRecorded) return;
        HashMap<String, Object> choice = new HashMap<>();
        ArrayList<String> notpicked = new ArrayList<>();
        notpicked.add(card.getMetricID()); 
        for (SingleCardReward reward : cardLinks)
            if (reward.type == VCR_SINGLECARDREWARD) {
                reward.skipRecorded = true;
                notpicked.add(reward.card.getMetricID()); 
            }
        choice.put("picked", "SKIP");
        choice.put("not_picked", notpicked);
        choice.put("floor", Integer.valueOf(AbstractDungeon.floorNum));
        CardCrawlGame.metricData.card_choices.add(choice);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (discounted) {
            sb.setColor(new Color(0.5f, 0.6f, 0.6f, 0.3f));
        } else if (hb.hovered) {
            sb.setColor(new Color(0.4f, 0.6f, 0.6f, 1.0f));
        } else {
            sb.setColor(new Color(0.5f, 0.6f, 0.6f, 0.8f));
        }

        if (hb.clickStarted) {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale * 0.98f, Settings.scale * 0.98f, 0.0f, 0, 0, 464, 98, false, false);
        } else {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale, Settings.scale, 0.0f, 0, 0, 464, 98, false, false);
        }

        if (this.flashTimer != 0.0f) {
            sb.setColor(0.6f, 1.0f, 1.0f, this.flashTimer * 1.5f);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale * 1.03f, Settings.scale * 1.15f, 0.0f, 0, 0, 464, 98, false, false);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (type == VCR_SINGLECARDREWARD) {
            float scale = renderCard.drawScale;

            renderCard.drawScale = 0.175f;
            renderCard.current_x = card.target_x = hb.x + ((AbstractCard.RAW_W * renderCard.drawScale) * Settings.scale) / 2f + XOFFSET;
            renderCard.current_y = card.target_y = hb.cY;
            renderCard.render(sb);

            renderCard.drawScale = scale;

            FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, text, Settings.WIDTH * 0.434F, y + 5.0f * Settings.scale, 1000.0f * Settings.scale, 0.0f, card.upgraded ? Settings.GREEN_TEXT_COLOR : Color.WHITE);

            if(hb.hovered || hb.justHovered) {
                VisibleCardRewards.hoverRewardWorkaround = this;
            }
        } else if (type == VCR_BOWLREWARD) {
            sb.setColor(Color.WHITE);
            sb.draw(rewardTexture, RewardItem.REWARD_ITEM_X - 32.0F, this.y - 32.0F - 2.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, text, Settings.WIDTH * 0.434F, y + 5.0f * Settings.scale, 1000.0f * Settings.scale, 0.0f, Color.WHITE);
        }

        if (!cardLinks.isEmpty() && !isFirst())
            renderRelicLink(sb);

        hb.render(sb);
    }

    private void renderCardKeywordTips(SpriteBatch sb) {
        ArrayList<PowerTip> t = new ArrayList<>();
        for (String s : renderCard.keywords)
            if (!s.equals("[R]") && !s.equals("[G]") && !s.equals("[B]") && !s.equals("[W]") && !s.equals("[E]"))
                t.add(new PowerTip(TipHelper.capitalize(s), (String)GameDictionary.keywords.get(s)));
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(renderCard)) {
            List<TooltipInfo> tooltips = modifier.additionalTooltips(renderCard);
            if (tooltips != null)
                for (TooltipInfo tooltip : tooltips)
                    if (!tooltip.title.equals("[R]") && !tooltip.title.equals("[G]") && !tooltip.title.equals("[B]") && !tooltip.title.equals("[W]") && !tooltip.title.equals("[E]"))
                        t.add(new PowerTip(TipHelper.capitalize(tooltip.title), tooltip.description));
        }
        if (!t.isEmpty()) {
            TipHelper.queuePowerTips(renderCard.current_x + renderCard.hb.width * 0.6F, renderCard.current_y + renderCard.hb.height * 0.38F, t); 
            TipHelper.render(sb);
        }
        if (Loader.isModLoaded("downfall") && renderCard instanceof AbstractUnknownCard) {
            renderCard.current_x += renderCard.hb.width * 0.1F;
            if (!t.isEmpty())
                renderCard.current_x += renderCard.hb.width * 0.1F + 320.0F * Settings.scale;
            int WIDTH = Settings.WIDTH;
            Settings.WIDTH = 0;
            ((AbstractUnknownCard)renderCard).renderCardPreviewImpl(sb, false);
            Settings.WIDTH = WIDTH;
        } else if (renderCard.cardsToPreview != null) {
            renderCard.cardsToPreview.current_x = renderCard.current_x + renderCard.hb.width * 1.1F;
            if (!t.isEmpty())
                renderCard.cardsToPreview.current_x += renderCard.hb.width * 0.1F + 320.0F * Settings.scale;
            renderCard.cardsToPreview.current_y = renderCard.current_y;
            renderCard.cardsToPreview.render(sb); 
        }
    }

    @SpireOverride
    protected void renderRelicLink(SpriteBatch sb) {
        SpireSuper.call(sb);
    }

    //Due to reward scrolling's orthographic camera and render order of rewards, the card needs to be rendered outside of the render method
    public void renderCardOnHover(SpriteBatch sb) {
        renderCard.current_x = card.target_x = InputHelper.mX + (AbstractCard.RAW_W * renderCard.drawScale) * Settings.scale;
        renderCard.current_y = card.target_y = InputHelper.mY;
        renderCard.render(sb);
        renderCardKeywordTips(sb);
    }
}