package org.keremulutas.mockeyjockey;

import org.keremulutas.mockeyjockey.core.generator.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public class MockeyJockey {

    private final Random _randomizer;

    public MockeyJockey() {
        this._randomizer = ThreadLocalRandom.current();
    }

    public ConstantGenerator<Integer> constant(int value) {
        return new ConstantGenerator<>(value, _randomizer);
    }

    public ConstantGenerator<Long> constant(long value) {
        return new ConstantGenerator<>(value, _randomizer);
    }

    public ConstantGenerator<String> constant(String value) {
        return new ConstantGenerator<>(value, _randomizer);
    }

    public <T> ConstantGenerator<T> constant(T value) {
        return new ConstantGenerator<>(value, _randomizer);
    }

    public StringGenerator strings() {
        return new StringGenerator(_randomizer);
    }

    public ParameterizedStringGenerator parameterizedString(String format) {
        return new ParameterizedStringGenerator(format, _randomizer);
    }

    public FormattedStringGenerator formattedString(String format) {
        return new FormattedStringGenerator(format, _randomizer);
    }

    public IpAddressGenerator.Sequential ipAddressesSequential() {
        return new IpAddressGenerator.Sequential(_randomizer);
    }

    public IpAddressGenerator.Random ipAddressesRandom() {
        return new IpAddressGenerator.Random(_randomizer);
    }

    public DoubleGenerator doubles() {
        return new DoubleGenerator(_randomizer);
    }

    public PartitionGenerator.Doubles doublePartitions(double value, int parts, double maxDeviation) {
        return new PartitionGenerator.Doubles(value, parts, maxDeviation, _randomizer);
    }

    public NumericSequenceGenerator.Doubles doubleSequences() {
        return new NumericSequenceGenerator.Doubles(_randomizer);
    }

    public MultiplexerGenerator.Doubles doublesMultiplexer() {
        return new MultiplexerGenerator.Doubles(_randomizer);
    }

    public IntegerGenerator integers() {
        return new IntegerGenerator(_randomizer);
    }

    public PartitionGenerator.Integers integerPartitions(int value, int parts, int maxDeviation) {
        return new PartitionGenerator.Integers(value, parts, maxDeviation, _randomizer);
    }

    public NumericSequenceGenerator.Integers integerSequences() {
        return new NumericSequenceGenerator.Integers(_randomizer);
    }

    public MultiplexerGenerator.Integers integersMultiplexer() {
        return new MultiplexerGenerator.Integers(_randomizer);
    }

    public LongGenerator longs() {
        return new LongGenerator(_randomizer);
    }

    public PartitionGenerator.Longs longPartitions(long value, int parts, long maxDeviation) {
        return new PartitionGenerator.Longs(value, parts, maxDeviation, _randomizer);
    }

    public NumericSequenceGenerator.Longs longSequences() {
        return new NumericSequenceGenerator.Longs(_randomizer);
    }

    public MultiplexerGenerator.Longs longsMultiplexer() {
        return new MultiplexerGenerator.Longs(_randomizer);
    }

    public <T> ListGenerator<T> lists() {
        return new ListGenerator<>(_randomizer);
    }

    public GenericObjectGenerator genericObjects() {
        return new GenericObjectGenerator(_randomizer);
    }

    public ZonedDateTimeGenerator.WithFrequency zonedDateTimesWithFrequency() {
        return new ZonedDateTimeGenerator.WithFrequency(_randomizer);
    }

    public ZonedDateTimeGenerator.WithRatio zonedDateTimesWithRatio() {
        return new ZonedDateTimeGenerator.WithRatio(_randomizer);
    }

    public <T> ObjectGenerator.Reflection<T> objectsFromReflection(Class<T> clz) {
        return new ObjectGenerator.Reflection<>(clz, _randomizer);
    }

    public <T> ObjectGenerator.Constructor<T> objectsFromConstructor(Class<T> clz) {
        return new ObjectGenerator.Constructor<>(clz, _randomizer);
    }

    public <T> WeightedGenerator<T> weighted(Class<T> clz) {
        return new WeightedGenerator<>(clz, _randomizer);
    }

    public <T> SelectionGenerator.Sequential<T> sequentialSelection(Class<T> clz) {
        return new SelectionGenerator.Sequential<>(clz, _randomizer);
    }

    public <T> ConcurrentSelectionGenerator.Sequential<T> sequentialConcurrentSelection(Class<T> clz) {
        return new ConcurrentSelectionGenerator.Sequential<>(clz, _randomizer);
    }

    public <T> SelectionGenerator.Randomized<T> randomSelection(Class<T> clz) {
        return new SelectionGenerator.Randomized<>(clz, _randomizer);
    }

    public <T> ConcurrentSelectionGenerator.RandomizedFromVector<T> randomConcurrentSelection(Class<T> clz) {
        return new ConcurrentSelectionGenerator.RandomizedFromVector<>(clz, _randomizer);
    }

    public <TKey, T> ConcurrentSelectionGenerator.RandomizedFromMap<TKey, T> randomConcurrentSelection(Class<T> clz, Class<TKey> keyClz) {
        return new ConcurrentSelectionGenerator.RandomizedFromMap<>(clz, keyClz, _randomizer);
    }

    public <OIT, IIT, OT> Generator<OIT, OT> transform(Class<OT> clz, Generator<OIT, IIT> sourceGenerator, Function<IIT, OT> input) {
        return new TransformerGenerator<>(clz, sourceGenerator, input);
    }

    public <OT> CustomGenerator<OT> custom(Class<OT> clz, Supplier<OT> supplier) {
        return new CustomGenerator<>(clz, supplier, _randomizer);
    }

}
