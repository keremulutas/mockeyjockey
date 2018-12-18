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

public class DummyPackageInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyPackageInformation.class);

    private static MockeyJockey mj = new MockeyJockey();

    private static int count;

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            LOGGER.warn("Usage: .... <lines count: int>");
        }

        try {
            count = Integer.parseInt(args[0], 10);
        } catch(Exception e) {
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
            .field("target_ip", ipGenerator)
            .field("target_port", mj.integers().min(1000).max(65535));

        List<String> linesList = new ArrayList<>();
        String lastIP = null;
        for (int i = 0; i < count; i++) {
            Map<String, Object> next = mg.get();
            if (i % 10_000 == 0) {
                lastIP = next.get("source_ip").toString();
            }
            linesList.add(String.join(
                ";",
                lastIP,
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

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(linesList.get(i));
            result.append("\n");
        }
        FileOutputStream outputStream = new FileOutputStream("dummy.csv");
        byte[] strToBytes = result.toString().getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

}