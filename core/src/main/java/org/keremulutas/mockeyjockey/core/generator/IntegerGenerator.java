package org.keremulutas.mockeyjockey.core.generator;

import java.util.Random;
import java.util.function.Supplier;

public class IntegerGenerator extends Generator<Void, Integer> {

    private Supplier<Integer> _minGenerator;
    private Supplier<Integer> _maxGenerator;
    private int _lowerBound;
    private int _upperBound;

    public IntegerGenerator(Random randomizer) {
        super(randomizer);
        this._minGenerator = new ConstantGenerator<>(0, this._randomizer);
        this._maxGenerator = new ConstantGenerator<>(Integer.MAX_VALUE, this._randomizer);
        this._lowerBound = 0;
        this._upperBound = Integer.MAX_VALUE;
    }

    public IntegerGenerator min(int min) {
        this._minGenerator = new ConstantGenerator<>(min, this._randomizer);
        this._lowerBound = min;
        return this;
    }

    public IntegerGenerator min(Supplier<Integer> minGenerator) {
        this._minGenerator = minGenerator;
        this._lowerBound = this._minGenerator.get();
        return this;
    }

    public IntegerGenerator max(int max) {
        this._maxGenerator = new ConstantGenerator<>(max, this._randomizer);
        this._upperBound = max;
        return this;
    }

    public IntegerGenerator max(Supplier<Integer> maxGenerator) {
        this._maxGenerator = maxGenerator;
        this._upperBound = this._maxGenerator.get();
        return this;
    }

    @Override
    protected Integer generate() {
        if (!( this._minGenerator instanceof ConstantGenerator )) {
            this._lowerBound = this._minGenerator.get();
        }
        if (!( this._maxGenerator instanceof ConstantGenerator )) {
            this._upperBound = this._maxGenerator.get();
        }
        return this._randomizer.nextInt(this._upperBound - this._lowerBound) + this._lowerBound;
    }

    @Override
    public Class<Integer> getTargetObjectClass() {
        return int.class;
    }

}
