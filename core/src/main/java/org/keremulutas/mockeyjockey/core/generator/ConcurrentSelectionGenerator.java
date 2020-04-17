package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConcurrentSelectionGenerator<T> extends Generator<Void, T> {

    boolean _isCircular = false;
    private Class<T> _objectClass;

    ConcurrentSelectionGenerator(Class<T> clz, Random randomizer) {
        super(randomizer);
        this._objectClass = clz;
    }

    public ConcurrentSelectionGenerator<T> isCircular(boolean isCircular) {
        this._isCircular = isCircular;
        return this;
    }

    public static class RandomizedFromVector<T> extends ConcurrentSelectionGenerator<T> {

        Vector<T> _source;

        public RandomizedFromVector(Class<T> clz, Random randomizer) {
            super(clz, randomizer);
        }

        public ConcurrentSelectionGenerator<T> source(Vector<T> source) {
            this._source = source;
            return this;
        }

        @Override
        protected T generate() {
            if (this._source == null) {
                throw new MockeyJockeyException("Source must be supplied", this.getClass().getName(), this._tag);
            }

            if (this._source.size() == 0) {
                throw new MockeyJockeyException("Source generator supplied zero length list", this.getClass().getName(), this._tag);
            }

            int index = this._randomizer.nextInt(this._source.size());

            return this._isCircular ? this._source.get(index) : this._source.remove(index);
        }

    }

    public static class RandomizedFromMap<TKey, T> extends ConcurrentSelectionGenerator<T> {

        ConcurrentHashMap<TKey, T> _source;
        private Class<TKey> _keyObjectClass;

        public RandomizedFromMap(Class<T> clz, Class<TKey> keyClz, Random randomizer) {
            super(clz, randomizer);
            this._keyObjectClass = keyClz;
        }

        public ConcurrentSelectionGenerator<T> source(ConcurrentHashMap<TKey, T> source) {
            this._source = source;
            return this;
        }

        public Class<TKey> getKeyObjectClass() {
            return _keyObjectClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected T generate() {
            if (this._source == null) {
                throw new MockeyJockeyException("Source must be supplied", this.getClass().getName(), this._tag);
            }

            if (this._source.size() == 0) {
                throw new MockeyJockeyException("Source is empty", this.getClass().getName(), this._tag);
            }

            int index = this._randomizer.nextInt(this._source.size());
            Iterator it = this._source.entrySet().iterator();
            Map.Entry<TKey, T> pair = null;
            for (int i = 0; i <= index; i++) {
                pair = (Map.Entry<TKey, T>) it.next();
            }
            T value = Objects.requireNonNull(pair).getValue();
            if (!this._isCircular) {
                it.remove();
            }

            return value;
        }

    }

    public static class Sequential<T> extends ConcurrentSelectionGenerator<T> {

        Vector<T> _source;
        int _lastIndex = 0;

        public Sequential(Class<T> clz, Random randomizer) {
            super(clz, randomizer);
        }

        public ConcurrentSelectionGenerator<T> source(Vector<T> source) {
            this._source = source;
            return this;
        }

        @Override
        protected T generate() {
            if (this._source == null) {
                throw new MockeyJockeyException("Source must be supplied", this.getClass().getName(), this._tag);
            }

            if (this._source.size() == 0) {
                throw new MockeyJockeyException("Source generator supplied zero length list", this.getClass().getName(), this._tag);
            }

            T value;
            if (this._isCircular) {
                this._lastIndex++;
                value = this._source.get(this._lastIndex % this._source.size());
            } else {
                value = this._source.remove(0);
            }

            return value;
        }

    }

    @Override
    public Class<T> getTargetObjectClass() {
        return this._objectClass;
    }

}
