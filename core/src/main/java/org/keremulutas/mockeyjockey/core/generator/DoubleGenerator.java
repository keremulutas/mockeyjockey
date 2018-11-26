package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.utils.Utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

// https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
public class DoubleGenerator extends Generator<Void, Double> {

    private Supplier<Double> _minGenerator;
    private Supplier<Double> _maxGenerator;
    private Supplier<Integer> _precisionGenerator;
    private double _lowerBound;
    private double _upperBound;
    private int _precision;

    public DoubleGenerator(Random randomizer) {
        super(randomizer);
        this._minGenerator = new ConstantGenerator<>(0.0, this._randomizer);
        this._maxGenerator = new ConstantGenerator<>(Long.MAX_VALUE * 1.0, this._randomizer);
        this._lowerBound = 0.0;
        this._upperBound = Long.MAX_VALUE * 1.0;
        this._precisionGenerator = new ConstantGenerator<>(Integer.MAX_VALUE, this._randomizer);
        this._precision = Integer.MAX_VALUE;
    }

    // TODO: accept Generators in .min(), .max() and .precision() calls
    public DoubleGenerator min(double lowerBound) {
        if (Double.valueOf(this._upperBound - lowerBound).isInfinite()) {
            throw new IllegalArgumentException("Lower bound value " + lowerBound + " will result in always producing infinite value.");
        }
        this._minGenerator = new ConstantGenerator<>(lowerBound, this._randomizer);
        this._lowerBound = lowerBound;
        return this;
    }

    public DoubleGenerator min(Supplier<Double> minGenerator) {
        this._minGenerator = minGenerator;
        this._lowerBound = this._minGenerator.get();
        return this;
    }

    public DoubleGenerator max(double upperBound) {
        if (Double.valueOf(upperBound - this._lowerBound).isInfinite()) {
            throw new IllegalArgumentException("Upper bound value " + upperBound + " will result in always producing infinite value.");
        }
        this._maxGenerator = new ConstantGenerator<>(upperBound, this._randomizer);
        this._upperBound = upperBound;
        return this;
    }

    public DoubleGenerator max(Supplier<Double> maxGenerator) {
        this._maxGenerator = maxGenerator;
        this._upperBound = this._maxGenerator.get();
        return this;
    }

    public DoubleGenerator precision(int precision) {
        this._precisionGenerator = new ConstantGenerator<>(precision, this._randomizer);
        this._precision = precision;
        return this;
    }

    public DoubleGenerator precision(Supplier<Integer> precisionGenerator) {
        this._precisionGenerator = precisionGenerator;
        this._precision = this._precisionGenerator.get();
        return this;
    }

    @Override
    protected Double generate() {
        if (!( this._minGenerator instanceof ConstantGenerator )) {
            this._lowerBound = this._minGenerator.get();
        }
        if (!( this._maxGenerator instanceof ConstantGenerator )) {
            this._upperBound = this._maxGenerator.get();
        }
        if (!( this._precisionGenerator instanceof ConstantGenerator )) {
            this._precision = this._precisionGenerator.get();
        }
        double value;
        if (this._randomizer instanceof ThreadLocalRandom) {
            value = ( (ThreadLocalRandom) this._randomizer ).nextDouble(this._lowerBound, this._upperBound);
        } else {
            value = this._lowerBound + ( this._upperBound - this._lowerBound ) * this._randomizer.nextDouble();
        }
        if (this._precision != Integer.MAX_VALUE) {
            value = Utils.formatDouble(value, this._precision);
        }
        return value;
    }

    @Override
    public Class<Double> getTargetObjectClass() {
        return double.class;
    }

}
