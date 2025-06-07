package usury.subscribes;

import basemod.BaseMod;
import basemod.interfaces.PreMonsterTurnSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import usury.Usury;

@SpireInitializer
public class PreMonsterTurnSubscribe implements PreMonsterTurnSubscriber {

    public PreMonsterTurnSubscribe() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new PreMonsterTurnSubscribe();
    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster abstractMonster) {
        int loanFloors = AbstractDungeon.floorNum - Usury.getLoanFloorNum();
        if (loanFloors > 5) {
            String[] TEXT = Usury.getText("MerchantSpeeches");

            AbstractDungeon.actionManager.addToBottom(
                    new TalkAction(
                            abstractMonster,
                            TEXT[(int) (Math.random() * TEXT.length)],
                            4.0F, 3.0F
                    )
            );
        }
        return true;
    }
}
