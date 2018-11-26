package org.keremulutas.mockeyjockey.samples.lottery;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.keremulutas.mockeyjockey.MockeyJockey;
import org.keremulutas.mockeyjockey.core.generator.ListGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SuperLottery {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperLottery.class);

    private static MockeyJockey mj = new MockeyJockey();

    private static int count = 1_000_000;

    public static void main(String[] args) throws IOException {
        // https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string/35446009#35446009
        String jsonString = CharStreams.toString(
            new InputStreamReader(
                System.class.getResourceAsStream("/real_lottery_results.json"),
                Charsets.UTF_8
            )
        );
        Map<String, List<Integer>> resultsMap = new Gson().fromJson(
            jsonString,
            new TypeToken<Map<String, List<Integer>>>() {
            }.getType()
        );

        Set<List<Integer>> results = new HashSet<>();
        ListGenerator<Integer> couponGenerator = mj.randomSelection(Integer.class)
            .source(
                mj.integerSequences().start(1).end(55).diff(1).list(54)
            )
            .isCircular(true)
            .list(6);

        while (results.size() < count) {
            List<Integer> result = couponGenerator.get()
                .stream()
                .sorted()
                .collect(Collectors.toList());
            results.add(result);
        }

        Map<Integer, Integer> counterMap = new HashMap<>();
        resultsMap.forEach((key, next) -> next.forEach(val -> {
            counterMap.put(val, counterMap.getOrDefault(val, 0) + 1);
        }));

        LOGGER.info("Occurrence counts for numbers:");
        LinkedHashMap<Integer, Integer> orderedCounterMap = counterMap
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
        orderedCounterMap.forEach((number, occurrenceCount) -> LOGGER.info(String.format("%2d: %d", number, occurrenceCount)));

        resultsMap.entrySet().forEach(new Consumer<Map.Entry<String, List<Integer>>>() {
            private int matchesCount = 0;

            @Override
            public void accept(Map.Entry<String, List<Integer>> drawResult) {
                List<Integer> next = drawResult.getValue();
                if (results.contains(next)) {
                    LocalDate drawDate = LocalDate.parse(drawResult.getKey(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    LOGGER.info((++matchesCount) + " - draw date: " + drawDate + ", matching numbers: " + drawResult.getValue().toString());
                }
            }
        });
    }

}