package org.keremulutas.mockeyjockey.samples.dummy;

import org.keremulutas.mockeyjockey.MockeyJockey;
import org.keremulutas.mockeyjockey.core.generator.IpAddressGenerator;
import org.keremulutas.mockeyjockey.core.generator.MapGenerator;
import org.keremulutas.mockeyjockey.core.generator.NumericSequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DummyPackageInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyPackageInformation.class);

    private static MockeyJockey mj = new MockeyJockey();

    private static int count;
    private static int lines;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            LOGGER.warn("Usage: .... <file count: int> <line count: int>");
        }

        try {
            count = Integer.parseInt(args[0], 10);
            lines = Integer.parseInt(args[1], 10);
            LOGGER.info("file count = {}, lines per file = {}", count, lines);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(-1);
        }

        long currentTimestamp = (System.currentTimeMillis() / 1000);
        NumericSequenceGenerator<Long> timestampGenerator = mj.longSequences().start(currentTimestamp).diff(1L);

        IpAddressGenerator ipGenerator = mj.ipAddressesSequential().startFrom("192.168.1.1");

        MapGenerator mg = mj.maps()
            .field("source_port", mj.integers().min(1000).max(65535))
            .field("start_ts", timestampGenerator)
            .field("end_ts", timestampGenerator)
            .field("in", mj.longs().min(1000).max(100_000))
            .field("out", mj.longs().min(1000).max(100_000))
            .field("fqdn", mj.formattedString("fqdn.%s").param(mj.randomSelection(String.class).withElements("com", "net", "org", "info", "biz", "com.tr", "net.tr", "org.tr")))
            .field("user", mj.strings().length(6))
            .field("target_ip", ipGenerator)
            .field("target_port", mj.integers().min(1000).max(65535))
            .mutate(new Function<Map<String, Object>, Map<String, Object>>() {
                int counter = 0;
                String val;

                @Override
                public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
                    if (counter % 10_000 == 0) {
                        val = ipGenerator.getLastGeneratedValue();
                    }
                    counter++;
                    stringObjectMap.put("source_ip", val);
                    return stringObjectMap;
                }
            });

        for (int i = 0; i < count; i++) {
            List<String> linesList = new ArrayList<>();

            for (int j = 0; j < lines; j++) {
                Map<String, Object> next = mg.get();
                linesList.add(String.join(
                    ";",
                    next.get("target_ip").toString(),
                    next.get("target_port").toString(),
                    next.get("end_ts").toString(),
                    next.get("start_ts").toString(),
                    next.get("in").toString(),
                    next.get("out").toString(),
                    next.get("fqdn").toString(),
                    next.get("user").toString(),
                    next.get("source_ip").toString(),
                    next.get("source_port").toString(),
                    "",
                    "",
                    "",
                    "1"
                ));
            }

            LOGGER.info("processing file: dummy{}.csv", i);
            String result = String.join("\n", linesList);
            FileOutputStream outputStream = new FileOutputStream("./dummy" + i + ".csv");
            outputStream.write(result.getBytes());
            outputStream.close();
        }

    }

}