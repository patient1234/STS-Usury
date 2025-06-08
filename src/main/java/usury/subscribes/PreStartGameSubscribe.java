package usury.subscribes;

import basemod.BaseMod;
import basemod.interfaces.PreStartGameSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import usury.Usury;
import usury.patchs.UpdateWorthPatch;

@SpireInitializer
public class PreStartGameSubscribe implements PreStartGameSubscriber {
    public PreStartGameSubscribe() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new PreStartGameSubscribe();
    }

    @Override
    public void receivePreStartGame() {
        UpdateWorthPatch.initialize();
        Usury.setLoanFloorNum(Usury.config.getInt("loanFloorNum"));
        Usury.setLoanAmount(Usury.config.getInt("loanAmount"));
    }
}
