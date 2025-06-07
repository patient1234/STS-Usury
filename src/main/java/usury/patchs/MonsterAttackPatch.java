package usury.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import usury.Usury;

@SpirePatch(
        clz = DamageAction.class,
        method = "update"
)
public class MonsterAttackPatch {

    public static final Logger logger = LogManager.getLogger(MonsterAttackPatch.class.getName());

    private static void robGold(AbstractCreature source, AbstractCreature target) {

        int robGold;

        if (AbstractDungeon.player.gold >= 15) {
            robGold = Math.min(Usury.getLoanAmount(), 15);
            AbstractDungeon.player.loseGold(robGold);
        } else if (AbstractDungeon.player.gold > 0) {
            robGold = Math.min(Usury.getLoanAmount(), AbstractDungeon.player.gold);
            AbstractDungeon.player.loseGold(robGold);
        } else if (Usury.getMostWorthItem() != null) {
            Usury.WorthItem worthItem = Usury.getMostWorthItem();
            if (worthItem.price >= 15){
                robGold = Math.min(Usury.getLoanAmount(), 15);
            } else {
                robGold = Math.min(Usury.getLoanAmount(), worthItem.price);
            }
            AbstractDungeon.player.gainGold(worthItem.price - robGold);
            worthItem.rob();
            logger.info("{}被抢了", worthItem);
        } else {
            logger.info("身无分文嘛，哈基人你这家伙");
            robGold = 15;
        }

        logger.info("被抢劫价值{}金币", robGold);

        Usury.setLoanAmount(Usury.getLoanAmount() - robGold);
        if (Usury.getLoanAmount() == 0) {
            Usury.setLoanFloorNum(AbstractDungeon.floorNum);
        }

        CardCrawlGame.sound.play("GOLD_JINGLE");
        for (int i = 0; i < robGold; i++) {

            AbstractDungeon.effectList.add(
                    new GainPennyEffect(
                            source,
                            target.hb.cX, target.hb.cY,
                            source.hb.cX, source.hb.cY,
                            false
                    )
            );

        }

    }

    @SpireInsertPatch(
            rloc = 8
    )
    public static void Insert(DamageAction __instance) {
        int loanFloors = AbstractDungeon.floorNum - Usury.getLoanFloorNum();
        if (loanFloors > 10 && __instance.target == AbstractDungeon.player) {
            robGold(__instance.source, __instance.target);
        }
    }
}
