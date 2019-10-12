package org.keremulutas.mockeyjockey.samples.dummy;

import org.keremulutas.mockeyjockey.MockeyJockey;
import org.keremulutas.mockeyjockey.core.generator.IpAddressGenerator;
import org.keremulutas.mockeyjockey.core.generator.MapGenerator;
import org.keremulutas.mockeyjockey.core.generator.ZonedDateTimeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DummyPackageInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyPackageInformation.class);

    private static MockeyJockey mj = new MockeyJockey();

    private static int fileCount;
    private static int fileCountDigits;
    private static int lineCount;
    private static String fieldSeparator = "|";
    private static String lineSeparator = "\n";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            LOGGER.info("Usage: .... <file fileCount: int> <line fileCount: int>");
            System.exit(1);
        }

        try {
            fileCount = Integer.parseInt(args[0], 10);
            if (fileCount <= 0) {
                throw new RuntimeException("File count must be positive, given: " + fileCount);
            }
            fileCountDigits = 1 + (int) Math.floor(Math.log10(fileCount));
            lineCount = Integer.parseInt(args[1], 10);
            if (lineCount <= 0) {
                throw new RuntimeException("Line count must be positive, given: " + lineCount);
            }
            LOGGER.info("Going to create {} files with {} line(s) each", fileCount, lineCount);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(-1);
        }

        ZonedDateTimeGenerator.WithFrequency dateTimeGenerator = mj.zonedDateTimesWithFrequency()
            .start(ZonedDateTime.now())
            .frequency(10L, ChronoUnit.SECONDS);

        IpAddressGenerator ipGenerator = mj.ipAddressesSequential().startFrom("192.168.1.1");

        MapGenerator mg = mj.maps()
            .field("user", mj.strings().length(6))
            .field("start_ts", mj.custom(String.class, new Supplier<String>() {
                private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

                @Override
                public String get() {
                    return dtf.format(dateTimeGenerator.get());
                }
            }))
            .field("end_ts", mj.integers().min(1).max(300))
            .field("in", mj.longs().min(1000).max(100_000))
            .field("out", mj.longs().min(1000).max(100_000))
            .field("fqdn", mj.formattedString("fqdn.%s").param(mj.randomSelection(String.class).withElements("com", "net", "org", "info", "biz", "com.tr", "net.tr", "org.tr")))
            .mutate(new Function<Map<String, Object>, Map<String, Object>>() {
                int counter = 0;
                String val;

                @Override
                public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
                    if (counter % 10_000 == 0) {
                        val = ipGenerator.get();
                    }
                    counter++;
                    stringObjectMap.put("target_ip", val);
                    return stringObjectMap;
                }
            })
            .field("target_port", mj.integers().min(1000).max(65535))
            .field("source_ip", ipGenerator)
            .field("source_port", mj.integers().min(1000).max(65535))
            .field("device_ip", mj.randomSelection(String.class).withElements("dev1", "dev2", "dev3", "dev4"));

        long ts = Instant.now().toEpochMilli();
        for (int i = 1; i <= fileCount; i++) {
            String currentFileName = String.format(ts + "_dummy%0" + fileCountDigits + "d.csv", i);
            LOGGER.info("processing file: {}", currentFileName);
            FileOutputStream outputStream = new FileOutputStream("./" + currentFileName);

            for (int j = 0; j < lineCount; j++) {
                Map<String, Object> next = mg.get();
                String line = String.join(
                    fieldSeparator,
                    next.get("user").toString() + "@ttnet",
                    next.get("source_ip").toString(),
                    next.get("source_port").toString(),
                    "",
                    "",
                    next.get("start_ts").toString(),
                    next.get("end_ts").toString(),
                    next.get("target_ip").toString(),
                    next.get("target_port").toString(),
                    next.get("fqdn").toString(),
                    next.get("in").toString(),
                    next.get("out").toString(),
                    next.get("device_ip").toString()
                );
                outputStream.write(line.getBytes());
                outputStream.write(lineSeparator.getBytes());
            }

            outputStream.close();
        }

    }

}
