package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;
import org.keremulutas.mockeyjockey.utils.Printf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class FormattedStringGenerator extends Generator<Void, String> {

    private String _format;
    private List<Supplier<?>> _generators = new ArrayList<>();
    private Printf printf;

    public FormattedStringGenerator(String format, Random randomizer) {
        super(randomizer);
        this._format = format;
        this.printf = new Printf(format);
    }

    public FormattedStringGenerator param(Supplier<?> paramSupplier) {
        this._generators.add(paramSupplier);
        return this;
    }

    // https://github.com/greenlaw110/java-str-benchmark
    @Override
    protected String generate() {
        if (this._generators.size() == 0) {
            throw new MockeyJockeyException("Partial string generators must be supplied", this);
        }
        int argCount = this._generators.size();
        Object[] args = new Object[argCount];
        for (int i = 0; i < argCount; i++) {
            args[i] = this._generators.get(i).get();
        }
        try {
            return this.printf.format(args).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class<String> getTargetObjectClass() {
        return String.class;
    }

}
