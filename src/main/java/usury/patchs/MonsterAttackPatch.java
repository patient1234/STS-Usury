package usury.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import usury.Usury;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "damage"
)
public class MonsterDamagePatch {
    @SpirePrefixPatch
    public static void Prefix(AbstractMonster __instance) {
        int loanFloors = AbstractDungeon.floorNum - Usury.getLoanFloorNum();
        if (loanFloors > 10) {
            AbstractDungeon.player.loseGold(5);
        }
    }
}
