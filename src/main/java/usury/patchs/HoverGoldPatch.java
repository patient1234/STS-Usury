package usury.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import usury.Usury;

import static com.megacrit.cardcrawl.ui.panels.TopPanel.LABEL;
import static com.megacrit.cardcrawl.ui.panels.TopPanel.MSG;

@SpirePatch(
        clz = TopPanel.class,
        method = "updateTips"
)
public class HoverGoldPatch {

    private static final float TIP_OFF_X;
    private static final float TIP_Y;
    private static final String[] TEXT;

    @SpireInsertPatch(
            rloc = 4
    )
    public static void Insert() {
        String body = MSG[4] + " NL ";
        if (Usury.isInBusiness()) {
            body += TEXT[0] + Usury.loanableAmount + TEXT[1];
        }
        if (Usury.getLoanAmount() > 0) {
            body +=  TEXT[2] + Usury.getLoanAmount() + TEXT[3];
        }
        if (Usury.getLoanFloorNum() != AbstractDungeon.floorNum) {
            body += TEXT[4] + (AbstractDungeon.floorNum - Usury.getLoanFloorNum()) + TEXT[5];
        }
        if (!Usury.isInBusiness()) {
            body += TEXT[6];
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP) {
            body += TEXT[7];
        }
        TipHelper.renderGenericTip((float)InputHelper.mX - TIP_OFF_X, TIP_Y, LABEL[4], body);
    }

    static {
        TIP_Y = (float) Settings.HEIGHT - 120.0F * Settings.scale;
        TIP_OFF_X = 140.0F * Settings.scale;
        TEXT  = Usury.getText("GoldTips");
    }
}
