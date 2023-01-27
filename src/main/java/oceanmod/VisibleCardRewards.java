package oceanmod;

import basemod.BaseMod;
import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import oceanmod.rewards.SingleCardReward;

@SpireInitializer
public class VisibleCardRewards implements PostRenderSubscriber {
    public static SingleCardReward hoverRewardWorkaround;
    
    public static void initialize() {
        BaseMod.subscribe(new VisibleCardRewards());
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if(hoverRewardWorkaround != null) {
            hoverRewardWorkaround.renderCardOnHover(sb);
            hoverRewardWorkaround = null;
        }
    }
}