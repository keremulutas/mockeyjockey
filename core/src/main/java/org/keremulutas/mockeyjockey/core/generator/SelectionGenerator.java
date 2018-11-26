package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.util.*;
import java.util.function.Supplier;

public abstract class SelectionGenerator<T> extends Generator<Void, T> {

    Supplier<List<T>> _sourceGenerator;
    List<T> _currentList;
    boolean _isCircular = false;
    private Class<T> _objectClass;

    SelectionGenerator(Class<T> clz, Random randomizer) {
        super(randomizer);
        this._objectClass = clz;
    }

    public SelectionGenerator<T> isCircular(boolean isCircular) {
        this._isCircular = isCircular;
        return this;
    }

    public SelectionGenerator<T> source(Supplier<List<T>> sourceGenerator) {
        this._sourceGenerator = sourceGenerator;
        return this;
    }

    public SelectionGenerator<T> withElements(T... elements) {
        return this.withElements(Arrays.asList(elements));
    }

    public SelectionGenerator<T> withElements(List<T> elements) {
        ArrayList<T> list = new ArrayList<>(elements);
        this._sourceGenerator = new ConstantGenerator<>(list, this._randomizer);
        this._isCircular = true;
        return this;
    }

    public SelectionGenerator<T> withElements(Set<T> elements) {
        ArrayList<T> list = new ArrayList<>(elements);
        this._sourceGenerator = new ConstantGenerator<>(list, this._randomizer);
        this._isCircular = true;
        return this;
    }

    public static class Randomized<T> extends SelectionGenerator<T> {

        public Randomized(Class<T> clz, Random randomizer) {
            super(clz, randomizer);
        }

        @Override
        public SelectionGenerator.Randomized<T> isCircular(boolean isCircular) {
            return (SelectionGenerator.Randomized<T>) super.isCircular(isCircular);
        }

        @Override
        public SelectionGenerator.Randomized<T> source(Supplier<List<T>> sourceGenerator) {
            return (SelectionGenerator.Randomized<T>) super.source(sourceGenerator);
        }

        @Override
        public SelectionGenerator.Randomized<T> withElements(T... elements) {
            return (SelectionGenerator.Randomized<T>) super.withElements(elements);
        }

        @Override
        public SelectionGenerator.Randomized<T> withElements(List<T> elements) {
            return (SelectionGenerator.Randomized<T>) super.withElements(elements);
        }

        @Override
        protected T generate() {
            if (this._sourceGenerator == null) {
                throw new MockeyJockeyException("Source generator must be supplied", this);
            }

            if (this._currentList == null || this._currentList.size() == 0) {
                this._currentList = new ArrayList<>(this._sourceGenerator.get());
            }

            T value;

            int index = this._randomizer.nextInt(this._currentList.size());
            if (this._isCircular) {
                value = this._currentList.remove(index);
            } else {
                value = this._currentList.get(index);
            }

            return value;
        }

    }

    public static class Sequential<T> extends SelectionGenerator<T> {

        private int _currentIndex = 0;

        public Sequential(Class<T> clz, Random randomizer) {
            super(clz, randomizer);
        }

        @Override
        public SelectionGenerator.Sequential<T> isCircular(boolean isCircular) {
            return (SelectionGenerator.Sequential<T>) super.isCircular(isCircular);
        }

        @Override
        public SelectionGenerator.Sequential<T> source(Supplier<List<T>> sourceGenerator) {
            return (SelectionGenerator.Sequential<T>) super.source(sourceGenerator);
        }

        @Override
        public SelectionGenerator.Sequential<T> withElements(T... elements) {
            return (SelectionGenerator.Sequential<T>) super.withElements(elements);
        }

        @Override
        public SelectionGenerator.Sequential<T> withElements(List<T> elements) {
            return (SelectionGenerator.Sequential<T>) super.withElements(elements);
        }

        @Override
        protected T generate() {
            if (this._sourceGenerator == null) {
                throw new MockeyJockeyException("Source generator or elements must be supplied", this);
            }

            if (this._currentList == null || this._currentList.size() == 0 || ( !this._isCircular && this._currentIndex % this._currentList.size() == 0 )) {
                this._currentList = this._sourceGenerator.get();
                if (this._currentList.size() == 0) {
                    throw new MockeyJockeyException("Source generator generated a list with 0 elements", this);
                }
            }

            T value = this._currentList.get(this._currentIndex % this._currentList.size());
            this._currentIndex++;

            return value;
        }

    }

    @Override
    public Class<T> getTargetObjectClass() {
        return this._objectClass;
    }

}
