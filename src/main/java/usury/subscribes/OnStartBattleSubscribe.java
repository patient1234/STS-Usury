package usury.subscribes;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import usury.Usury;

@SpireInitializer
public class OnStartBattleSubscribe implements OnStartBattleSubscriber {

    public OnStartBattleSubscribe() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new OnStartBattleSubscribe();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        int loanFloors = AbstractDungeon.floorNum - Usury.getLoanFloorNum();
        if (loanFloors > 5) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                AbstractDungeon.actionManager.addToBottom(
                        new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(
                                m, AbstractDungeon.player,
                                new StrengthPower(m, loanFloors % 5)
                        )
                );
            }
        }
    }
}
