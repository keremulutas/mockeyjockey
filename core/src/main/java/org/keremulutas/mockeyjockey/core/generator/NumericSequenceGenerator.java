package org.keremulutas.mockeyjockey.core.generator;

import java.util.Random;
import java.util.function.Supplier;

public abstract class NumericSequenceGenerator<T> extends Generator<Void, T> {

    protected Supplier<T> _startGenerator;
    protected Supplier<T> _endGenerator;
    protected Supplier<T> _diffGenerator;
    protected T _currentStart;
    protected T _currentEnd;
    protected T _currentDiff;
    protected T _next;
    protected boolean _strictBoundaries = true;

    private NumericSequenceGenerator(Random randomizer) {
        super(randomizer);
    }

    public T getCurrentStart() {
        return this._currentStart;
    }

    public T getCurrentEnd() {
        return this._currentEnd;
    }

    public T getCurrentDiff() {
        return this._currentDiff;
    }

    public NumericSequenceGenerator<T> start(T start) {
        this._startGenerator = new ConstantGenerator<>(start, this._randomizer);
        this._currentStart = start;
        this._next = this._currentStart;
        return this;
    }

    public NumericSequenceGenerator<T> start(Supplier<T> start) {
        this._startGenerator = start;
        this._currentStart = this._startGenerator.get();
        this._next = this._currentStart;
        return this;
    }

    public NumericSequenceGenerator<T> end(T end) {
        this._endGenerator = new ConstantGenerator<>(end, this._randomizer);
        this._currentEnd = end;
        return this;
    }

    public NumericSequenceGenerator<T> end(Supplier<T> end) {
        this._endGenerator = end;
        this._currentEnd = this._endGenerator.get();
        return this;
    }

    public NumericSequenceGenerator<T> diff(T diff) {
        this._diffGenerator = new ConstantGenerator<>(diff, this._randomizer);
        this._currentDiff = diff;
        return this;
    }

    public NumericSequenceGenerator<T> diff(Supplier<T> diff) {
        this._diffGenerator = diff;
        this._currentDiff = this._diffGenerator.get();
        return this;
    }

    public NumericSequenceGenerator<T> withStrictBoundaries(boolean strictBoundaries) {
        this._strictBoundaries = strictBoundaries;
        return this;
    }

    public static class Integers extends NumericSequenceGenerator<Integer> {

        public Integers(Random randomizer) {
            super(randomizer);
            this.start(new ConstantGenerator<>(0, _randomizer));
            this.end(new ConstantGenerator<>(Integer.MAX_VALUE, _randomizer));
            this.diff(new ConstantGenerator<>(1, _randomizer));
        }

        @Override
        protected Integer generate() {
            int value = this._next;
            if (( this._next + this._currentDiff ) >= this._currentEnd) {
                if (!( this._startGenerator instanceof ConstantGenerator )) {
                    this._currentStart = this._startGenerator.get();
                }
                if (!( this._endGenerator instanceof ConstantGenerator )) {
                    this._currentEnd = this._endGenerator.get();
                }
                if (!( this._diffGenerator instanceof ConstantGenerator )) {
                    this._currentDiff = this._diffGenerator.get();
                }

                if (this._strictBoundaries) {
                    this._next = this._currentStart;
                } else {
                    this._next = ( this._next + this._currentDiff ) % this._currentEnd;
                }
            } else {
                this._next += this._currentDiff;
            }

            return value;
        }

        @Override
        public Class<Integer> getTargetObjectClass() {
            return int.class;
        }

    }

    public static class Longs extends NumericSequenceGenerator<Long> {

        public Longs(Random randomizer) {
            super(randomizer);
            this.start(new ConstantGenerator<>(0L, _randomizer));
            this.end(new ConstantGenerator<>(Long.MAX_VALUE, _randomizer));
            this.diff(new ConstantGenerator<>(1L, _randomizer));
        }

        @Override
        protected Long generate() {
            long value = this._next;
            if (( this._next + this._currentDiff ) >= this._currentEnd) {
                if (!( this._startGenerator instanceof ConstantGenerator )) {
                    this._currentStart = this._startGenerator.get();
                }
                if (!( this._endGenerator instanceof ConstantGenerator )) {
                    this._currentEnd = this._endGenerator.get();
                }
                if (!( this._diffGenerator instanceof ConstantGenerator )) {
                    this._currentDiff = this._diffGenerator.get();
                }

                if (this._strictBoundaries) {
                    this._next = this._currentStart;
                } else {
                    this._next = ( this._next + this._currentDiff ) % this._currentEnd;
                }
            } else {
                this._next += this._currentDiff;
            }
            return value;
        }

        @Override
        public Class<Long> getTargetObjectClass() {
            return long.class;
        }

    }

    public static class Doubles extends NumericSequenceGenerator<Double> {

        public Doubles(Random randomizer) {
            super(randomizer);
            this.start(new ConstantGenerator<>(0.0, _randomizer));
            this.end(new ConstantGenerator<>(Double.MAX_VALUE, _randomizer));
            this.diff(new ConstantGenerator<>(1.0, _randomizer));
        }

        @Override
        protected Double generate() {
            double value = this._next;
            if (( this._next + this._currentDiff ) >= this._currentEnd) {
                if (!( this._startGenerator instanceof ConstantGenerator )) {
                    this._currentStart = this._startGenerator.get();
                }
                if (!( this._endGenerator instanceof ConstantGenerator )) {
                    this._currentEnd = this._endGenerator.get();
                }
                if (!( this._diffGenerator instanceof ConstantGenerator )) {
                    this._currentDiff = this._diffGenerator.get();
                }

                if (this._strictBoundaries) {
                    this._next = this._currentStart;
                } else {
                    this._next = ( this._next + this._currentDiff ) % this._currentEnd;
                }
            } else {
                this._next += this._currentDiff;
            }
            return value;
        }

        @Override
        public Class<Double> getTargetObjectClass() {
            return double.class;
        }

    }

}
