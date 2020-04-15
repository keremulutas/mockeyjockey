package org.keremulutas.mockeyjockey.samples.gamer;

import org.keremulutas.mockeyjockey.MockeyJockey;
import org.keremulutas.mockeyjockey.core.generator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class Gamer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Gamer.class);

    private static MockeyJockey mj = new MockeyJockey();

    private static Integer eventCount = 10;

    public static void main(String[] args) throws IOException {
        Generator<Void, Integer> idGenerator = mj.integers().min(1).withExplicitReset(true);
        Generator<Void, String> tokenGenerator = mj.formattedString("token-%d").param(idGenerator).withExplicitReset(true);

        Generator<Void, String> loginDateTimeGenerator = mj.zonedDateTimesWithFrequency()
            .start(ZonedDateTime.now())
            .frequency(10L, ChronoUnit.MINUTES)
            .transform(String.class, zonedDateTime -> String.valueOf(zonedDateTime.toInstant().getEpochSecond()));

        // {
        //     "token": "test2",
        //     "category": "Shop",
        //     "subCategory": "Weapon",
        //     "subCategory2": "Melee",
        //     "event": "ClickItem",
        //     "gameItemId": 135,
        //     "itemId": 12893,
        //     "itemName": "Karambit",
        //     "isNew": true,
        //     "isWithGift": false,
        //     "campaignTypeDetail": "3 Günlük Bıçak",
        //     "isDiscount": false,
        //     "discountRate": "31",
        //     "periodType": "0(Day)",
        //     "periodInfo": "4",
        //     "period": 30,
        //     "priceType": "ZA",
        //     "price": "421",
        //     "Status": "Fail",
        //     "timeStamp": "1580280778"
        // }
        GenericObjectGenerator marketGenerator = mj.genericObjects()
            .field("token", tokenGenerator)
            .field("category", mj.constant("Shop"))
            .field("subCategory", mj.constant("Weapon"))
            .field("subCategory2", mj.constant("Melee"))
            .field("event", mj.randomSelection(String.class).withElements("ClickItem", "BuyItem"))
            .field("gameItemId", mj.constant(135))
            .field("itemId", mj.constant(12893))
            .field("itemName", mj.constant("Karambit"))
            .field("isNew", mj.constant("true"))
            .field("isWithGift", mj.constant("false"))
            .field("campaignTypeDetail", mj.constant("3 Günlük Bıçak"))
            .field("isDiscount", mj.constant("false"))
            .field("discountRate", mj.constant("31"))
            .field("periodType", mj.constant("0(Day)"))
            .field("periodInfo", mj.constant("4"))
            .field("period", mj.constant("30"))
            .field("priceType", mj.constant("ZA"))
            .field("price", mj.constant("421"))
            .field("Status", mj.randomSelection(String.class).withElements("Fail", "Success"))
            .field("timeStamp", loginDateTimeGenerator);

        // {
        //     "MemberId":6,
        //     "Token":"7A84AFBE-7DAD-4CAD-A90C-D7D61BAFDEB4",
        //     "LastLevel":4,
        //     "ZulaCredit":5,
        //     "UserFunnel":76,
        //     "TotalZAPurchased":0,
        //     "Elo":1415.32705441776,
        //     "EloLevel":4,
        //     "RS":0,
        //     "PS":0,
        //     "RSType":0,
        //     "PSType":0
        // }
        GenericObjectGenerator loginGenerator = mj.genericObjects()
            .field("MemberId", idGenerator)
            .field("Token", tokenGenerator)
            .field("LastLevel", mj.constant(4))
            .field("ZulaCredit", mj.integers().min(1).max(10))
            .field("UserFunnel", mj.integers().min(50).max(150))
            .field("TotalZAPurchased", mj.constant(0))
            .field("Elo", mj.doubles().min(1.0).max(10000.0).precision(11))
            .field("EloLevel", mj.integers().min(1).max(15))
            .field("RS", mj.constant(0))
            .field("PS", mj.constant(0))
            .field("RSType", mj.constant(0))
            .field("PSType", mj.constant(0));

        for (int i = 1; i <= eventCount; i++) {
            Map<String, Object> loginEvent = loginGenerator.get();
            Map<String, Object> marketEvent = marketGenerator.get();

            idGenerator.reset();
            tokenGenerator.reset();

            LOGGER.info("[login event]  token: " + loginEvent.get("Token"));
            LOGGER.info("[market event] token: " + marketEvent.get("token") + ", timestamp: " + marketEvent.get("timeStamp"));

            LOGGER.info("\n\n");
        }
    }

}
