package org.keremulutas.mockeyjockey.core.generator;

import java.util.Random;

public class ConstantGenerator<T> extends Generator<T, T> {

    public ConstantGenerator(T value, Random randomizer) {
        super(randomizer);
        this.accept(value);
    }

    @Override
    protected T generate() {
        return this._input;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getTargetObjectClass() {
        return (Class<T>) this._input.getClass();
    }

}
