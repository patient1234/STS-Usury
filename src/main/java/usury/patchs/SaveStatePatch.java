package usury.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import usury.Usury;

import java.io.IOException;

@SpirePatch(
        clz = SaveFile.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {
                SaveFile.SaveType.class
        }
)
public class SaveStatePatch {

    public static final Logger logger = LogManager.getLogger(SaveStatePatch.class.getName());

    @SpirePostfixPatch
    public static void Postfix() {
        saveInt("loanAmount", Usury.getLoanAmount());
        saveInt("loanFloorNum", Usury.getLoanFloorNum());
    }

    private static void saveInt(String key, int value) {
        Usury.config.setInt(key, value);
        logger.info("保存整型{}: {}", key, value);
        try {
            Usury.config.save();
        } catch (IOException e) {
            logger.error("保存配置失败", e);
        }
    }

}
