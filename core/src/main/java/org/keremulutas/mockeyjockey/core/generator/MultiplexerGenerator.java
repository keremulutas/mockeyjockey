package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class MultiplexerGenerator<T> extends Generator<Void, T> {

    private List<Supplier<List<T>>> _sourceGenerators = new ArrayList<>();
    private Class<T> _objectClass;
    private boolean _isCircular = false;
    private int _currentIndex = 0;
    private List<T> _buffer;

    private MultiplexerGenerator(Class<T> clz, Random randomizer) {
        super(randomizer);
        this._objectClass = clz;
    }

    public MultiplexerGenerator<T> sampleSource(Supplier<List<T>> sourceGenerator) {
        this._sourceGenerators.add(sourceGenerator);
        return this;
    }

    public final MultiplexerGenerator<T> withSamples(T... elements) {
        return this.withSamples(Arrays.asList(elements));
    }

    public MultiplexerGenerator<T> withSamples(List<T> elements) {
        this._sourceGenerators.add(new ConstantGenerator<>(elements, this._randomizer));
        this._isCircular = true;
        return this;
    }

    public MultiplexerGenerator<T> isCircular(boolean isCircular) {
        this._isCircular = isCircular;
        return this;
    }

    protected abstract void calcBuffer();

    public List<T> getBuffer() {
        if(this._buffer == null || this._buffer.size() == 0) {
            this.calcBuffer();
        }
        return this._buffer;
    }

    public static class Integers extends MultiplexerGenerator<Integer> {

        public Integers(Random randomizer) {
            super(Integer.class, randomizer);
        }

        protected void calcBuffer() {
            if(super._buffer == null || super._buffer.size() == 0) {
                List<List<Integer>> samples = new ArrayList<>();
                for(int i = 0, j = super._sourceGenerators.size(); i < j; i++) {
                    List<Integer> val = super._sourceGenerators.get(i).get();
                    samples.add(val);
                }

                super._buffer = new ArrayList<>(samples.get(0));

                for(int i = 0, j = samples.size() - 1; i < j; i++) {
                    int total = 0;
                    for(int x : super._buffer) {
                        total += x;
                    }
                    double[] ratios = new double[super._buffer.size() - 1];
                    for(int k = 0, l = super._buffer.size() - 1; k < l; k++) {
                        ratios[k] = (super._buffer.get(k) * 1.0) / total;
                    }
                    List<Integer> currentList = samples.get(i+1);
                    for(int k = (currentList.get(0) == total) ? 1 : 0, l = currentList.size(); k < l; k++) {
                        int subtotal = 0;
                        for(int m = 0, n = ratios.length; m < n; m++) {
                            int val = (int) Math.round(currentList.get(k) * ratios[m]);
                            super._buffer.add(val);
                            subtotal += val;
                        }
                        super._buffer.add(currentList.get(k) - subtotal);
                    }
                }
            }
        }

        @Override
        protected Integer generate() {
            if (super._sourceGenerators.size() == 0) {
                throw new MockeyJockeyException("Source generator or elements must be supplied", this);
            }

            if (super._buffer == null || super._buffer.size() == 0 || (!super._isCircular && super._currentIndex % super._buffer.size() == 0)) {
                this.calcBuffer();

                if(super._buffer.size() == 0) {
                    throw new MockeyJockeyException("Operation generated a list with 0 elements", this);
                }
            }

            Integer value  = super._buffer.get(super._currentIndex % super._buffer.size());
            super._currentIndex++;

            return value;
        }

    }

    public static class Longs extends MultiplexerGenerator<Long> {

        public Longs(Random randomizer) {
            super(Long.class, randomizer);
        }

        protected void calcBuffer() {
            List<List<Long>> samples = new ArrayList<>();
            for(int i = 0, j = super._sourceGenerators.size(); i < j; i++) {
                List<Long> val = super._sourceGenerators.get(i).get();
                samples.add(val);
            }

            super._buffer = new ArrayList<>(samples.get(0));

            for(int i = 0, j = samples.size() - 1; i < j; i++) {
                int total = 0;
                for(long x : super._buffer) {
                    total += x;
                }
                double[] ratios = new double[super._buffer.size()];
                for(int k = 0, l = super._buffer.size(); k < l; k++) {
                    ratios[k] = (super._buffer.get(k) * 1.0) / total;
                }
                for(int k = (samples.get(i+1).get(0) == total) ? 1 : 0, l = samples.get(i+1).size(); k < l; k++) {
                    for(int m = 0, n = ratios.length; m < n; m++) {
                        super._buffer.add(Math.round(samples.get(i+1).get(k) * ratios[m]));
                    }
                }
            }
        }

        @Override
        protected Long generate() {
            if (super._sourceGenerators.size() == 0) {
                throw new MockeyJockeyException("Source generator or elements must be supplied", this);
            }

            if (super._buffer == null || super._buffer.size() == 0 || (!super._isCircular && super._currentIndex % super._buffer.size() == 0)) {
                this.calcBuffer();

                if(super._buffer.size() == 0) {
                    throw new MockeyJockeyException("Operation generated a list with 0 elements", this);
                }
            }

            Long value  = super._buffer.get(super._currentIndex % super._buffer.size());
            super._currentIndex++;

            return value;
        }

    }

    public static class Doubles extends MultiplexerGenerator<Double> {

        public Doubles(Random randomizer) {
            super(Double.class, randomizer);
        }

        protected void calcBuffer() {
            List<List<Double>> samples = new ArrayList<>();
            for(int i = 0, j = super._sourceGenerators.size(); i < j; i++) {
                List<Double> val = super._sourceGenerators.get(i).get();
                samples.add(val);
            }

            super._buffer = new ArrayList<>(samples.get(0));

            for(int i = 0, j = samples.size() - 1; i < j; i++) {
                int total = 0;
                for(double x : super._buffer) {
                    total += x;
                }
                double[] ratios = new double[super._buffer.size()];
                for(int k = 0, l = super._buffer.size(); k < l; k++) {
                    ratios[k] = (super._buffer.get(k) * 1.0) / total;
                }
                for(int k = (samples.get(i+1).get(0) == total) ? 1 : 0, l = samples.get(i+1).size(); k < l; k++) {
                    for(int m = 0, n = ratios.length; m < n; m++) {
                        super._buffer.add(samples.get(i+1).get(k) * ratios[m]);
                    }
                }
            }
        }

        @Override
        protected Double generate() {
            if (super._sourceGenerators.size() == 0) {
                throw new MockeyJockeyException("Source generator or elements must be supplied", this);
            }

            if (super._buffer == null || super._buffer.size() == 0 || (!super._isCircular && super._currentIndex % super._buffer.size() == 0)) {
                this.calcBuffer();

                if(super._buffer.size() == 0) {
                    throw new MockeyJockeyException("Operation generated a list with 0 elements", this);
                }
            }

            Double value  = super._buffer.get(super._currentIndex % super._buffer.size());
            super._currentIndex++;

            return value;
        }

    }

    @Override
    public Class<T> getTargetObjectClass() {
        return this._objectClass;
    }

}
