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
import java.util.function.Supplier;

public class DummyPackageInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyPackageInformation.class);

    private static MockeyJockey mj = new MockeyJockey();

    private static int count;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            LOGGER.warn("Usage: .... <lines count: int>");
        }

        try {
            count = Integer.parseInt(args[0], 10);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(-1);
        }

        long currentTimestamp = (System.currentTimeMillis() / 1000);
        NumericSequenceGenerator<Long> timestampGenerator = mj.longSequences().start(currentTimestamp).diff(1L);

        IpAddressGenerator ipGenerator = mj.ipAddressesSequential().startFrom("192.168.1.1");
        MapGenerator mg = mj.maps()
            .field("source_ip", ipGenerator)
            .field("source_port", mj.integers().min(1000).max(65535))
            .field("start_ts", timestampGenerator)
            .field("end_ts", timestampGenerator)
            .field("in", mj.longs().min(1000).max(100_000))
            .field("out", mj.longs().min(1000).max(100_000))
            .field("fqdn", mj.formattedString("fqdn.%s").param(mj.randomSelection(String.class).withElements("com", "net", "org", "info", "io", "com.tr", "net.tr", "org.tr")))
            .field("user", mj.strings().length(6))
            .field("target_ip", mj.custom(String.class, new Supplier<String>() {
                int counter = 0;
                String val;

                @Override
                public String get() {
                    if(counter % 10_000 == 0) {
                        val = ipGenerator.getLastGeneratedValue();
                    }
                    counter++;
                    return val;
                }

            }))
            .field("target_port", mj.integers().min(1000).max(65535));

        for (int i = 0; i < count; i++) {
            List<String> linesList = new ArrayList<>();
            for (int j = 0; j < 100_000; j++) {
                Map<String, Object> next = mg.get();
                linesList.add(String.join(
                    ";",
                    next.get("source_ip").toString(),
                    next.get("source_port").toString(),
                    next.get("end_ts").toString(),
                    next.get("start_ts").toString(),
                    next.get("in").toString(),
                    next.get("out").toString(),
                    next.get("fqdn").toString(),
                    next.get("user").toString(),
                    next.get("target_ip").toString(),
                    next.get("target_port").toString(),
                    "",
                    "",
                    "",
                    "1"
                ));
            }
            for (int j = 0; j < 100_000; j++) {
                FileOutputStream outputStream = new FileOutputStream("dummy" + count + ".csv");
                String result = linesList.get(i) + "\n";
                byte[] strToBytes = result.getBytes();
                outputStream.write(strToBytes);
                outputStream.close();
            }
        }

    }

}