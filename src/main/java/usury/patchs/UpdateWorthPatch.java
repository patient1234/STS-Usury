package usury.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import usury.Usury;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "update"
)
public class UpdateWorthPatch {

    public static final Logger logger = LogManager.getLogger(UpdateWorthPatch.class.getName());

    private static final String[] TEXT;

    private static final ArrayList<AbstractCard> cards = new ArrayList<>();
    private static final ArrayList<AbstractPotion> potions = new ArrayList<>();
    private static final ArrayList<AbstractRelic> relics = new ArrayList<>();
    private static int gold = -1;
    private static int loanAmount = -1;
    private static int loanFloorNum = -1;
    private static int floorNum = -1;
    private static AbstractDungeon.CurrentScreen currentScreen = null;

    private static boolean isUpdated() {
        return ! cards.equals(AbstractDungeon.player.masterDeck.group) ||
                ! potions.equals(AbstractDungeon.player.potions) ||
                ! relics.equals(AbstractDungeon.player.relics) ||
                gold != AbstractDungeon.player.gold ||
                loanAmount != Usury.getLoanAmount() ||
                loanFloorNum != Usury.getLoanFloorNum() ||
                floorNum != AbstractDungeon.floorNum;
    }

    private static void updateStatus() {
        cards.clear();
        cards.addAll(AbstractDungeon.player.masterDeck.group);
        potions.clear();
        potions.addAll(AbstractDungeon.player.potions);
        relics.clear();
        relics.addAll(AbstractDungeon.player.relics);
        gold = AbstractDungeon.player.gold;
        loanAmount = Usury.getLoanAmount();
        loanFloorNum = Usury.getLoanFloorNum();
        floorNum = AbstractDungeon.floorNum;
    }

    @SpirePostfixPatch
    public static void Postfix() {

        if (currentScreen != AbstractDungeon.screen) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP) {
                if (Usury.getLoanAmount() > 0 && AbstractDungeon.player.gold > 0) {
                    if (AbstractDungeon.player.gold < Usury.getLoanAmount()) {
                        Usury.setLoanAmount(Usury.getLoanAmount() - AbstractDungeon.player.gold);
                        AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
                        AbstractDungeon.shopScreen.createSpeech(TEXT[0]);
                    } else {
                        AbstractDungeon.player.loseGold(Usury.getLoanAmount());
                        AbstractDungeon.shopScreen.createSpeech(TEXT[1]);
                        Usury.setLoanAmount(0);
                        Usury.setLoanFloorNum(AbstractDungeon.floorNum);
                    }
                    Usury.updateLoanableAmount();
                } else if (Usury.getLoanAmount() == 0) {
                    if (Usury.loanableAmount > 0) {
                        AbstractDungeon.shopScreen.createSpeech(TEXT[2]);
                    } else {
                        AbstractDungeon.shopScreen.createSpeech(TEXT[3]);
                    }
                } else {
                    if (Usury.loanableAmount > 0) {
                        AbstractDungeon.shopScreen.createSpeech(TEXT[4]);
                    } else {
                        AbstractDungeon.shopScreen.createSpeech(TEXT[5]);
                    }
                }
                AbstractDungeon.player.gainGold(Usury.loanableAmount);
            } else if (currentScreen == AbstractDungeon.CurrentScreen.SHOP) {
                int gold = AbstractDungeon.player.gold;
                int leftGold = AbstractDungeon.player.gold - Usury.loanableAmount;
                if (leftGold < 0) {
                    Usury.setLoanAmount(Usury.getLoanAmount() - leftGold);
                    leftGold = 0;
                }
                AbstractDungeon.player.loseGold(gold - leftGold);
            }
            currentScreen = AbstractDungeon.screen;
        }

        if (floorNum != AbstractDungeon.floorNum) {
            if (AbstractDungeon.floorNum == 0) {
                Usury.setLoanAmount(0);
            }
            Usury.setLoanAmount((int) (Usury.getLoanAmount() * 1.05F));
            if (Usury.getLoanAmount() == 0) {
                Usury.setLoanFloorNum(AbstractDungeon.floorNum);
            }
        }

        if (isUpdated()) {
            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.SHOP) {
                Usury.updateLoanableAmount();
            } else {
                logger.info("当前正在购买，跳过更新贷款");
            }
            updateStatus();
        }

    }

    static {
        TEXT = Usury.getText("MerchantTips");
    }
}
