package usury;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import static basemod.BaseMod.gson;

public class Usury {

    public static final Logger logger = LogManager.getLogger(Usury.class.getName());

    public static final String MOD_ID = "usury";

    public static String langPackDir = MOD_ID + "Resources" + File.separator + "localization" + File.separator + Settings.language.toString().toLowerCase();

    public static final SpireConfig config;

    private static int loanAmount;
    private static int loanFloorNum;
    public static int loanableAmount = 0;

    private static final ArrayList<WorthItem> worthItems = new ArrayList<>();

    public static class WorthItem {
        public int price;
        public int frequency;

        public void rob() {

        }
    }

    public static class CardWorthItem extends WorthItem {
        public AbstractCard card;

        public CardWorthItem(AbstractCard card, int price) {
            this.price = price;
            this.card = card;
        }

        @Override
        public void rob() {
            card.onRemoveFromMasterDeck();
            CardCrawlGame.metricData.addPurgedItem(card.getMetricID());
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.masterDeck.removeCard(card);
        }

        @Override
        public String toString() {
            return card.name;
        }
    }

    public static class RelicWorthItem extends WorthItem {
        public AbstractRelic relic;

        public RelicWorthItem(AbstractRelic relic, int price) {
            this.price = price;
            this.relic = relic;
        }

        @Override
        public void rob() {
            AbstractDungeon.player.loseRelic(relic.relicId);
        }

        @Override
        public String toString() {
            return relic.name;
        }
    }

    public static WorthItem getMostWorthItem() {
        if (worthItems.isEmpty()) {
            return null;
        }
        WorthItem mostWorthItem = worthItems.get(0);
        for (WorthItem item : worthItems) {
            if (item.price > mostWorthItem.price) {
                mostWorthItem = item;
            }
        }
        return mostWorthItem;
    }

    public static String[] getText(String key) {
        String textPath = langPackDir + File.separator + "text.json";
        Type textType = (new TypeToken<Map<String, String[]>>() {
        }).getType();
        Map<String, String[]> text = gson.fromJson(loadJson(textPath), textType);
        return text.get(key);
    }

    public static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    public static void setLoanAmount(int amount) {
        loanAmount = amount;
    }

    public static int getLoanAmount() {
        return loanAmount;
    }

    public static void setLoanFloorNum(int floorNum) {
        loanFloorNum = floorNum;
    }

    public static int getLoanFloorNum() {
        return loanFloorNum;
    }

    public static void updateLoanableAmount() {
        loanableAmount = getLoanableAmount();
        if (!isInBusiness()) {
            loanableAmount = 0;
        }
    }

    public static boolean isInBusiness() {
        return (AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("Exordium"));
    }

    private static int getLoanableAmount() {
        int worth = (int)(getPlayerWorth() * 0.5) - getLoanAmount() - (AbstractDungeon.floorNum - getLoanFloorNum()) * 5;
        if (worth < 0) {
            worth = 0;
        }
        return worth;
    }

    private static int getPlayerWorth() {
        worthItems.clear();
        worthItems.addAll(getRelicsWorth());
        worthItems.addAll(getCardsWorth());
        logger.info("玩家总身家: {}", worthItems);
        int worth = 0;
        for (WorthItem item : worthItems) {
            worth += item.price;
        }
        logger.info("玩家总价值: {}", worth);
        return worth;
    }

    private static ArrayList<RelicWorthItem> getRelicsWorth() {
        ArrayList<RelicWorthItem> relicsWorth = new ArrayList<>();
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.usedUp) {
                continue;
            }
            switch (relic.tier) {
                case STARTER:
                    relicsWorth.add(new RelicWorthItem(relic, 50));
                    break;
                case COMMON:
                case SHOP:
                    relicsWorth.add(new RelicWorthItem(relic, 30));
                    break;
                case UNCOMMON:
                    relicsWorth.add(new RelicWorthItem(relic, 80));
                    break;
                case RARE:
                    relicsWorth.add(new RelicWorthItem(relic, 120));
                    break;
                case SPECIAL:
                    relicsWorth.add(new RelicWorthItem(relic, 180));
                    break;
                case BOSS:
                    relicsWorth.add(new RelicWorthItem(relic, 200));
                    break;
                default:
                    continue;
            }
            logger.info("---遗物: {}({})", relic.name, relic.tier);
        }
        return relicsWorth;
    }

    private static ArrayList<CardWorthItem> getCardsWorth() {
        ArrayList<CardWorthItem> cardsWorth = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            switch (card.rarity) {
                case UNCOMMON:
                    cardsWorth.add(new CardWorthItem(card, 30));
                    break;
                case RARE:
                    cardsWorth.add(new CardWorthItem(card, 70));
                    break;
                case SPECIAL:
                    cardsWorth.add(new CardWorthItem(card, 100));
                    break;
                default:
                    continue;
            }
            logger.info("---卡牌: {}({})", card.name, card.rarity);
        }
        return cardsWorth;
    }

    static {
        try {
            config = new SpireConfig(MOD_ID, "config");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
