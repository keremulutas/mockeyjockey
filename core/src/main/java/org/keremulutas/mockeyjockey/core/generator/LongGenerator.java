package org.keremulutas.mockeyjockey.core.generator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class LongGenerator extends Generator<Void, Long> {

    private Supplier<Long> _minGenerator;
    private Supplier<Long> _maxGenerator;
    private long _lowerBound;
    private long _upperBound;

    public LongGenerator(Random randomizer) {
        super(randomizer);
        this._minGenerator = new ConstantGenerator<>(0L, this._randomizer);
        this._maxGenerator = new ConstantGenerator<>(Long.MAX_VALUE, this._randomizer);
        this._lowerBound = 0L;
        this._upperBound = Long.MAX_VALUE;
    }

    public LongGenerator min(long lowerBound) {
        this._minGenerator = new ConstantGenerator<>(lowerBound, this._randomizer);
        this._lowerBound = lowerBound;
        return this;
    }

    public LongGenerator min(Supplier<Long> minGenerator) {
        this._minGenerator = minGenerator;
        this._lowerBound = this._minGenerator.get();
        return this;
    }

    public LongGenerator max(long upperBound) {
        this._maxGenerator = new ConstantGenerator<>(upperBound, this._randomizer);
        this._upperBound = upperBound;
        return this;
    }

    public LongGenerator max(Supplier<Long> maxGenerator) {
        this._maxGenerator = maxGenerator;
        this._upperBound = this._maxGenerator.get();
        return this;
    }

    // https://stackoverflow.com/questions/2546078/java-random-long-number-in-0-x-n-range
    private long nextLong(long n) {
        long bits, val;
        do {
            bits = ( this._randomizer.nextLong() << 1 ) >>> 1;
            val = bits % n;
        } while (bits - val + ( n - 1 ) < 0L);
        return val;
    }

    @Override
    protected Long generate() {
        if (!( this._minGenerator instanceof ConstantGenerator )) {
            this._lowerBound = this._minGenerator.get();
        }
        if (!( this._maxGenerator instanceof ConstantGenerator )) {
            this._upperBound = this._maxGenerator.get();
        }
        long value;
        if (this._randomizer instanceof ThreadLocalRandom) {
            value = ( (ThreadLocalRandom) this._randomizer ).nextLong(this._lowerBound, this._upperBound);
        } else {
            value = this.nextLong(this._upperBound - this._lowerBound) + this._lowerBound;
        }
        return value;
    }

    @Override
    public Class<Long> getTargetObjectClass() {
        return long.class;
    }

}
