package org.keremulutas.mockeyjockey.core.generator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class PartitionGenerator<T extends Number> extends Generator<Void, T> {

    protected T _value;
    protected int _partitionCount;
    protected T _maxDeviation;
    protected T[] _buffer;
    protected int _counter;
    protected T _evenParts;
    protected T _diff;

    protected PartitionGenerator(T value, int partitionCount, T maxDeviation, Random randomizer) {
        super(randomizer);
        this._value = value;
        this._partitionCount = partitionCount;
        this._maxDeviation = maxDeviation;
    }

    protected boolean isPositive(Number balance) {
        double comparator = this._maxDeviation.doubleValue();
        double doubleBalance = balance.doubleValue();
        boolean result;
        if (( -1 * comparator ) < doubleBalance && doubleBalance < comparator) {
            result = _randomizer.nextBoolean();
        } else {
            result = ( doubleBalance < comparator );
        }
        // if (doubleBalance > 0) {
        //     result = ( doubleBalance <= comparator ) && _randomizer.nextBoolean();
        // } else if (doubleBalance < 0) {
        //     result = ( doubleBalance < ( comparator * -1 ) ) || _randomizer.nextBoolean();
        // } else {
        //     result = _randomizer.nextBoolean();
        // }
        return result;
    }

    public static class Longs extends PartitionGenerator<Long> {

        public Longs(Long value, int partitionCount, Long maxDeviation, Random randomizer) {
            super(value, partitionCount, maxDeviation, randomizer);
            this._buffer = new Long[partitionCount];
            this._evenParts = Math.floorDiv(this._value, this._partitionCount);
            this._diff = this._value - ( this._evenParts * this._partitionCount );
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
            if (this._counter % this._partitionCount == 0) {
                int balance = 0;

                for (int i = 0; i < this._partitionCount; i++) {
                    if (i == this._partitionCount - 1) {
                        this._buffer[i] = this._evenParts - balance + this._diff;
                    } else {
                        long nextPart = 0;
                        while (nextPart <= 0) {
                            long unbalancer;
                            if (this._maxDeviation > 0) {
                                if (this._randomizer instanceof ThreadLocalRandom) {
                                    unbalancer = ( (ThreadLocalRandom) this._randomizer ).nextLong(this._maxDeviation);
                                } else {
                                    unbalancer = this.nextLong(this._maxDeviation);
                                }
                            } else {
                                unbalancer = 0L;
                            }
                            unbalancer = unbalancer % this._evenParts;
                            // boolean isPositive = ( balance < this._evenParts ) && _randomizer.nextBoolean();
                            boolean isPositive = this.isPositive(balance);
                            unbalancer *= ( isPositive ) ? 1 : -1;
                            balance += unbalancer;
                            nextPart = this._evenParts + unbalancer;
                        }
                        this._buffer[i] = nextPart;
                    }
                }
            }
            this._counter++;
            return this._buffer[this._counter % this._partitionCount];
        }

        @Override
        public Class<Long> getTargetObjectClass() {
            return long.class;
        }

    }

    public static class Integers extends PartitionGenerator<Integer> {

        public Integers(Integer value, int partitionCount, Integer maxDeviation, Random randomizer) {
            super(value, partitionCount, maxDeviation, randomizer);
            this._buffer = new Integer[partitionCount];
            this._evenParts = Math.floorDiv(this._value, this._partitionCount);
            this._diff = this._value - ( this._evenParts * this._partitionCount );
        }

        @Override
        protected Integer generate() {
            if (this._counter % this._partitionCount == 0) {
                int balance = 0;

                for (int i = 0; i < this._partitionCount; i++) {
                    if (i == this._partitionCount - 1) {
                        this._buffer[i] = this._evenParts - balance + this._diff;
                    } else {
                        int nextPart = 0;
                        while (nextPart <= 0) {
                            int unbalancer = ( ( this._maxDeviation > 0 ) ? _randomizer.nextInt(this._maxDeviation) : 0 ) % this._evenParts;
                            // boolean isPositive = ( balance < this._evenParts ) && _randomizer.nextBoolean();
                            boolean isPositive = this.isPositive(balance);
                            unbalancer *= ( isPositive ) ? 1 : -1;
                            balance += unbalancer;
                            nextPart = this._evenParts + unbalancer;
                        }
                        this._buffer[i] = nextPart;
                    }
                }
            }
            this._counter++;
            return this._buffer[this._counter % this._partitionCount];
        }

        @Override
        public Class<Integer> getTargetObjectClass() {
            return int.class;
        }

    }

    public static class Doubles extends PartitionGenerator<Double> {

        public Doubles(Double value, int partitionCount, Double maxDeviation, Random randomizer) {
            super(value, partitionCount, maxDeviation, randomizer);
            this._buffer = new Double[partitionCount];
            this._evenParts = this._value / this._partitionCount;
            this._diff = this._value - ( this._evenParts * this._partitionCount );
        }

        @Override
        protected Double generate() {
            if (this._counter % this._partitionCount == 0) {
                double balance = 0;

                for (int i = 0; i < this._partitionCount; i++) {
                    if (i == this._partitionCount - 1) {
                        this._buffer[i] = this._evenParts - balance + this._diff;
                    } else {
                        double nextPart = 0;
                        while (nextPart <= 0) {
                            double unbalancer = ( this._maxDeviation * _randomizer.nextDouble() ) % this._evenParts;
                            // boolean isPositive = ( balance < this._evenParts ) && _randomizer.nextBoolean();
                            boolean isPositive = this.isPositive(balance);
                            unbalancer *= ( isPositive ) ? 1 : -1;
                            balance += unbalancer;
                            nextPart = this._evenParts + unbalancer;
                        }
                        this._buffer[i] = nextPart;
                    }
                }
            }
            this._counter++;
            return this._buffer[this._counter % this._partitionCount];
        }

        @Override
        public Class<Double> getTargetObjectClass() {
            return double.class;
        }

    }

}
