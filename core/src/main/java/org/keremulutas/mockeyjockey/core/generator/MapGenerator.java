package org.keremulutas.mockeyjockey.core.generator;

import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.util.*;
import java.util.function.Function;

public class MapGenerator extends Generator<Void, Map<String, Object>> {

    private Map<String, Generator<?, ?>> _fieldGenerators = new HashMap<>();
    private List<Function<Map<String, Object>, Map<String, Object>>> _objectMutators = new ArrayList<>();

    public MapGenerator(Random randomizer) {
        super(randomizer);
    }

    public MapGenerator field(String fieldName, Generator<?, ?> fieldGenerator) {
        this._fieldGenerators.put(fieldName, fieldGenerator);
        return this;
    }

    public MapGenerator mutate(Function<Map<String, Object>, Map<String, Object>> objectMutator) {
        this._objectMutators.add(objectMutator);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> generate() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            for (String s : this._fieldGenerators.keySet()) {
                result.put(s, this._fieldGenerators.get(s).get());
            }
            for (Function<Map<String, Object>, Map<String, Object>> mutator : this._objectMutators) {
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
    @SuppressWarnings("unchecked")
    public Class<Map<String, Object>> getTargetObjectClass() {
        try {
            return (Class<Map<String, Object>>) Class.forName("java.util.LinkedHashMap");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
