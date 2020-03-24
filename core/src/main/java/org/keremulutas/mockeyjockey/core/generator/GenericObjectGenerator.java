package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;
import org.keremulutas.mockeyjockey.core.type.GenericObject;

import java.util.*;
import java.util.function.Function;

public class GenericObjectGenerator extends Generator<Void, GenericObject> {

    private Map<String, Generator<?, ?>> _fieldGenerators = new HashMap<>();
    private List<Function<GenericObject, GenericObject>> _objectMutators = new ArrayList<>();

    public GenericObjectGenerator(Random randomizer) {
        super(randomizer);
    }

    public GenericObjectGenerator field(String fieldName, Generator<?, ?> fieldGenerator) {
        this._fieldGenerators.put(fieldName, fieldGenerator);
        return this;
    }

    public GenericObjectGenerator mutate(Function<GenericObject, GenericObject> objectMutator) {
        this._objectMutators.add(objectMutator);
        return this;
    }

    @Override
    protected GenericObject generate() {
        GenericObject result = new GenericObject();
        try {
            for (String s : this._fieldGenerators.keySet()) {
                result.put(s, this._fieldGenerators.get(s).get());
            }
            for (Function<GenericObject, GenericObject> mutator : this._objectMutators) {
                result = mutator.apply(result);
            }
        } catch (MockeyJockeyException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new MockeyJockeyException(throwable, this);
        }
        return result;
    }

    @Override
    public Class<GenericObject> getTargetObjectClass() {
        return GenericObject.class;
    }

}
