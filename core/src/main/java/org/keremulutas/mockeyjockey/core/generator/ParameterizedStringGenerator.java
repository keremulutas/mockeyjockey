package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ParameterizedStringGenerator extends Generator<Void, String> {

    private String _format;
    private List<Supplier<String>> _generators = new ArrayList<>();

    public ParameterizedStringGenerator(String format, Random randomizer) {
        super(randomizer);
        this._format = format;
    }

    private static String replace(String source, String os, String ns) {
        if (source == null) {
            return null;
        }
        int i = 0;
        if (( i = source.indexOf(os, i) ) >= 0) {
            char[] sourceArray = source.toCharArray();
            char[] nsArray = ns.toCharArray();
            int oLength = os.length();
            StringBuilder buf = new StringBuilder(sourceArray.length);
            buf.append(sourceArray, 0, i).append(nsArray);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while (( i = source.indexOf(os, i) ) > 0) {
                buf.append(sourceArray, j, i - j).append(nsArray);
                i += oLength;
                j = i;
            }
            buf.append(sourceArray, j, sourceArray.length - j);
            source = buf.toString();
            buf.setLength(0);
        }
        return source;
    }

    public ParameterizedStringGenerator param(Supplier<String> partialStringGenerator) {
        this._generators.add(partialStringGenerator);
        return this;
    }

    // https://github.com/greenlaw110/java-str-benchmark
    @Override
    protected String generate() {
        if (this._generators.size() == 0) {
            throw new MockeyJockeyException("Partial string generators must be supplied", this.getClass().getName(), this._tag);
        }

        String result = this._format;
        for (int i = 0; i < this._generators.size(); i++) {
            result = StringUtils.replace(
                result,
                new StringBuilder(3 + Math.max(1, (int) ( Math.log10(i) + 1 ))).append("${").append(i).append("}").toString(),
                this._generators.get(i).get()
            );
            // result = S.replace(new StringBuilder(3 + Math.max(1, (int)(Math.log10(i)+1))).append("${").append(i).append("}").toString())
            //     .in(result)
            //     .with(this._generators.get(i).get());
            // result = replace(
            //     result,
            //     new StringBuilder(3 + Math.max(1, (int)(Math.log10(i)+1))).append("${").append(i).append("}").toString(),
            //     this._generators.get(i).get()
            // );
        }
        return result;
    }

    @Override
    public Class<String> getTargetObjectClass() {
        return String.class;
    }

}
