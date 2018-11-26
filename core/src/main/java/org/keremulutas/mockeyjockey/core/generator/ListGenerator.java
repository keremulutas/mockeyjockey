package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ListGenerator<T> extends Generator<Void, List<T>> {

    private Supplier<Integer> _lengthGenerator;
    private Supplier<T> _sourceGenerator;

    public ListGenerator(Random randomizer) {
        super(randomizer);
    }

    public ListGenerator<T> length(int length) {
        this._lengthGenerator = new ConstantGenerator<>(length, _randomizer);
        return this;
    }

    public ListGenerator<T> length(Supplier<Integer> lengthGenerator) {
        this._lengthGenerator = lengthGenerator;
        return this;
    }

    public ListGenerator<T> source(Supplier<T> sourceGenerator) {
        this._sourceGenerator = sourceGenerator;
        return this;
    }

    @Override
    protected List<T> generate() {
        if (this._lengthGenerator == null || this._sourceGenerator == null) {
            throw new MockeyJockeyException("Source and length generators must be supplied", this);
        }
        int length = this._lengthGenerator.get();

        List<T> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(
                this._sourceGenerator.get()
            );
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<T>> getTargetObjectClass() {
        return (Class<List<T>>) this._output.getClass();
    }

}
