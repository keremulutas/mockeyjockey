package org.keremulutas.mockeyjockey.core.generator;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WeightedGenerator<T> extends Generator<Void, T> {

    private Class<T> _objectClass;
    private Map<Supplier<T>, Supplier<Integer>> _weightsMap = new HashMap<>();
    // private List<Supplier<T>> _availableSuppliers = new ArrayList<>();
    private LinkedHashMap<Supplier<T>, Integer> _availableSuppliers = new LinkedHashMap<>();
    private boolean _sequential = false;

    public WeightedGenerator(Class<T> clz, Random randomizer) {
        super(randomizer);
        this._objectClass = clz;
    }

    public WeightedGenerator<T> add(T value, int weight) {
        return this.add(
            new ConstantGenerator<>(value, this._randomizer),
            new ConstantGenerator<>(weight, _randomizer)
        );
    }

    public WeightedGenerator<T> add(Supplier<T> supplier, int weight) {
        return this.add(
            supplier,
            new ConstantGenerator<>(weight, _randomizer)
        );
    }

    public WeightedGenerator<T> add(T value, Supplier<Integer> weightGenerator) {
        return this.add(
            new ConstantGenerator<>(value, this._randomizer),
            weightGenerator
        );
    }

    // TODO: addEach ve addN (random üreteçten n tane üretip her birini weight supplier dan gelen weight kadar ekleme)
    // public WeightedGenerator<T> addEach(List<T> valueList, int weight) {
    //     WeightedGenerator<T> self = this;
    //     valueList.forEach(new Consumer<T>() {
    //         @Override
    //         public void accept(T t) {
    //             self.add(t, weight);
    //         }
    //     });
    //     return this;
    // }

    public WeightedGenerator<T> add(Supplier<T> supplier, Supplier<Integer> weightGenerator) {
        this._weightsMap.put(supplier, weightGenerator);
        this.fillList(supplier, weightGenerator);
        return this;
    }

    public WeightedGenerator<T> sequential() {
        this._sequential = true;
        return this;
    }

    private void fillList() {
        for (Supplier<T> supplier : this._weightsMap.keySet()) {
            int x = this._weightsMap.get(supplier).get();
            this._availableSuppliers.put(supplier, x);
        }
    }

    private void fillList(Supplier<T> supplier, Supplier<Integer> weightGenerator) {
        int x = weightGenerator.get();
        this._availableSuppliers.put(supplier, x);
    }

    private int remainingCount() {
        return this._availableSuppliers.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T generate() {
        if (this._availableSuppliers.size() == 0) {
            this.fillList();
        }
        Supplier<T> supplier = null;
        Iterator<Supplier<T>> it = this._availableSuppliers.keySet().iterator();
        if(this._sequential) {
            supplier = it.next();
        } else {
            int x = this._randomizer.nextInt(this.remainingCount());
            do {
                supplier = it.next();
                x -= this._availableSuppliers.get(supplier);
            } while(x > 0);
        }

        Integer count = this._availableSuppliers.get(supplier);
        if(count == 1) {
            it.remove();
        } else {
            this._availableSuppliers.replace(supplier, count - 1);
        }

        return supplier.get();
    }

    // private void fillList() {
    //     for (Supplier<T> supplier : this._weightsMap.keySet()) {
    //         int x = this._weightsMap.get(supplier).get();
    //         for (int i = 0; i < x; i++) {
    //             this._availableSuppliers.add(supplier);
    //         }
    //     }
    // }
    //
    // private void fillList(Supplier<T> supplier, Supplier<Integer> weightGenerator) {
    //     int x = weightGenerator.get();
    //     for (int i = 0; i < x; i++) {
    //         this._availableSuppliers.add(supplier);
    //     }
    // }
    //
    // @Override
    // @SuppressWarnings("unchecked")
    // protected T generate() {
    //     if (this._availableSuppliers.size() == 0) {
    //         this.fillList();
    //     }
    //     int x = (this._sequential) ? 0 : this._randomizer.nextInt(this._availableSuppliers.size());
    //     return this._availableSuppliers.remove(x).get();
    // }

    @Override
    public Class<T> getTargetObjectClass() {
        return this._objectClass;
    }

}
