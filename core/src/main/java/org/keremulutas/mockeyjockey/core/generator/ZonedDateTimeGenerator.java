package org.keremulutas.mockeyjockey.core.generator;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class ZonedDateTimeGenerator extends Generator<Void, ZonedDateTime> {

    protected ZoneId _zoneId;

    public ZonedDateTimeGenerator(Random randomizer) {
        super(randomizer);
        this._zoneId = ZoneId.systemDefault();
    }

    public static class WithFrequency extends ZonedDateTimeGenerator {

        private ZonedDateTime _start;
        private Supplier<Long> _countGenerator;
        private int _timeAmount;
        private ChronoUnit _timeUnit;
        private long _currentCount;
        private long _period;
        private int _counter = 0;
        private ZonedDateTime _nextResult;
        private ZonedDateTime _lastPeriod;
        private long _offset;

        public WithFrequency(Random randomizer) {
            super(randomizer);
            this._start = ZonedDateTime.now();
            this._lastPeriod = this._start;
            this._nextResult = this._start;
            this._countGenerator = new ConstantGenerator<>(1L, randomizer);
            this._currentCount = this._countGenerator.get();
            this._timeAmount = 1;
            this._timeUnit = ChronoUnit.SECONDS;
            this._period = this._timeAmount * this._timeUnit.getDuration().toNanos();
            this._offset = Math.floorDiv(this._period, this._currentCount);
        }

        public ZonedDateTimeGenerator.WithFrequency withZoneId(ZoneId zoneId) {
            this._zoneId = zoneId;
            return this;
        }

        public WithFrequency start(ZonedDateTime zonedDateTime) {
            this._start = zonedDateTime;
            this._nextResult = this._start;
            this._lastPeriod = this._start;
            return this;
        }

        public WithFrequency frequency(long count, int timeAmount, ChronoUnit chronoUnit) {
            return this.frequency(new ConstantGenerator<>(count, this._randomizer), timeAmount, chronoUnit);
        }

        public WithFrequency frequency(Supplier<Long> countGenerator, int timeAmount, ChronoUnit chronoUnit) {
            this._countGenerator = countGenerator;
            this._currentCount = this._countGenerator.get();
            this._timeAmount = timeAmount;
            this._timeUnit = chronoUnit;
            this._period = this._timeAmount * this._timeUnit.getDuration().toNanos();
            this._offset = Math.floorDiv(this._period, this._currentCount);
            return this;
        }

        @Override
        protected ZonedDateTime generate() {
            ZonedDateTime result = ZonedDateTime.from(this._nextResult.withZoneSameInstant(this._zoneId));
            this._counter++;
            if (this._counter == this._currentCount) {
                this._counter = 0;
                this._currentCount = this._countGenerator.get();
                this._offset = Math.floorDiv(this._period, this._currentCount);
                this._lastPeriod = this._lastPeriod.plus(this._timeAmount, this._timeUnit);
                this._nextResult = this._lastPeriod;
            } else {
                this._nextResult = this._nextResult.plusNanos(this._offset);
            }
            return result;
        }

    }

    public static class WithRatio extends ZonedDateTimeGenerator {

        private ZonedDateTime _start;
        private Supplier<List<Double>> _ratioGenerator;
        private Supplier<Long> _countGenerator;
        private int _timeAmount;
        private ChronoUnit _timeUnit;
        private long _currentCount;
        private long _currentRatioAppliedCount;
        private List<Long> _currentRatioAppliedList = new ArrayList<>();
        private long _period;
        private int _counter = 0;
        private ZonedDateTime _nextResult;
        private long _offset;

        public WithRatio(Random randomizer) {
            super(randomizer);
            this._start = ZonedDateTime.now();
            this._nextResult = this._start;
            this._countGenerator = new ConstantGenerator<>(1L, randomizer);
            this._timeAmount = 1;
            this._timeUnit = ChronoUnit.SECONDS;
            this._period = this._timeAmount * this._timeUnit.getDuration().toNanos();
        }

        public ZonedDateTimeGenerator.WithRatio withZoneId(ZoneId zoneId) {
            this._zoneId = zoneId;
            return this;
        }

        public WithRatio start(ZonedDateTime zonedDateTime) {
            this._start = zonedDateTime;
            this._nextResult = this._start;
            return this;
        }

        public WithRatio ratios(List<Double> ratio, int timeAmount, ChronoUnit chronoUnit) {
            return this.ratios(
                new ConstantGenerator<>(ratio, this._randomizer),
                timeAmount,
                chronoUnit
            );
        }

        public WithRatio ratios(Supplier<List<Double>> ratioGenerator, int timeAmount, ChronoUnit chronoUnit) {
            this._ratioGenerator = ratioGenerator;
            this._timeAmount = timeAmount;
            this._timeUnit = chronoUnit;
            this._period = this._timeAmount * this._timeUnit.getDuration().toNanos();
            return this;
        }

        public WithRatio counts(Supplier<Long> countGenerator) {
            this._countGenerator = countGenerator;
            return this;
        }

        public WithRatio counts(Collection<Long> counts) {
            return this.counts(
                new SelectionGenerator.Sequential<>(Long.class, this._randomizer).withElements(counts)
            );
        }

        @Override
        protected ZonedDateTime generate() {
            if(this._currentRatioAppliedList.size() == 0) {
                this._currentCount = this._countGenerator.get();
                List<Double> ratios = this._ratioGenerator.get();
                Long total = 0L;
                for(int i = 0, j = ratios.size() - 1; i < j; i++) {
                    Double ratio = ratios.get(i);
                    Long nextValue = Math.round(ratio * this._currentCount);
                    this._currentRatioAppliedList.add(nextValue);
                    total += nextValue;
                }
                this._currentRatioAppliedList.add(this._currentCount - total);
            }
            ZonedDateTime result = ZonedDateTime.from(this._nextResult.withZoneSameInstant(this._zoneId));
            if(this._currentRatioAppliedCount == 0) {
                this._currentRatioAppliedCount = this._currentRatioAppliedList.remove(0);
                this._offset = Math.floorDiv(this._period, this._currentRatioAppliedCount);
            }
            this._currentRatioAppliedCount--;
            if(this._currentRatioAppliedCount == 0) {
                this._nextResult = this._nextResult
                    .truncatedTo(this._timeUnit)
                    .plus(1, this._timeUnit);
            } else {
                this._nextResult = this._nextResult.plusNanos(this._offset);
            }
            return result;
        }

    }

    @Override
    public Class<ZonedDateTime> getTargetObjectClass() {
        return ZonedDateTime.class;
    }

}
