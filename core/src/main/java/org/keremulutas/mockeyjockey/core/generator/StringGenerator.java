package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.util.Random;
import java.util.function.Supplier;

public class StringGenerator extends Generator<Void, String> {

    private static final char[] basic_subset = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] extended_subset = "0123456789abcçdefgğhıijklmnoöpqrsştuüvwxyzABCÇDEFGĞHIİJKLMNOÖPQRSŞTUÜVWXYZ!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}".toCharArray();

    private Supplier<Integer> _lengthGenerator;
    private char[] _subset = basic_subset;

    public StringGenerator(Random randomizer) {
        super(randomizer);
    }

    public StringGenerator length(int length) {
        this._lengthGenerator = new ConstantGenerator<>(length, this._randomizer);
        return this;
    }

    public StringGenerator length(Supplier<Integer> lengthGenerator) {
        this._lengthGenerator = lengthGenerator;
        return this;
    }

    public StringGenerator useExtendedSubset() {
        this._subset = extended_subset;
        return this;
    }

    @Override
    protected String generate() {
        if (this._lengthGenerator == null) {
            throw new MockeyJockeyException("Length must be supplied", this);
        }
        int length = this._lengthGenerator.get();

        char[] buf = new char[length];
        for (int i = 0, subsetLength = this._subset.length; i < length; i++) {
            buf[i] = this._subset[this._randomizer.nextInt(subsetLength)];

            // buf[i] = (char) ( this._randomizer.nextInt(25) + 97);

            // int bound = this._randomizer.nextBoolean() ? 65 : 97;
            // buf[i] = (char) ( this._randomizer.nextInt(25) + bound);
        }
        return String.valueOf(buf);
    }

    @Override
    public Class<String> getTargetObjectClass() {
        return String.class;
    }

}
