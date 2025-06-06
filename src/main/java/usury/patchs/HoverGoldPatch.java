package usury.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = TopPanel.class,
        method = "update"
)
public class ClickGoldPatch {

    public static final Logger logger = LogManager.getLogger(ClickGoldPatch.class.getName());

    @SpirePostfixPatch
    public static void Postfix(TopPanel __instance) {
        if (__instance.goldHb.hovered && InputHelper.justClickedLeft) {
            logger.info("金币被点击了");
        }
    }
}
