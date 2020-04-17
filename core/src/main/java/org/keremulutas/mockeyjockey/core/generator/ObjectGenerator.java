package org.keremulutas.mockeyjockey.core.generator;

import com.google.common.collect.HashBiMap;
import org.keremulutas.mockeyjockey.core.exception.MockeyJockeyException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.function.Function;

public abstract class ObjectGenerator<T> extends Generator<Void, T> {

    static MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    Class<T> _objectClass;
    MethodHandle _constructorHandle;

    ObjectGenerator(Class<T> clz, Random randomizer) {
        super(randomizer);
        this._objectClass = clz;
    }

    public static class Reflection<T> extends ObjectGenerator<T> {

        private static final HashBiMap<Class<?>, Class<?>> primitiveTypesMap = HashBiMap.create();

        static {
            primitiveTypesMap.put(Boolean.class, boolean.class);
            primitiveTypesMap.put(Byte.class, byte.class);
            primitiveTypesMap.put(Character.class, char.class);
            primitiveTypesMap.put(Double.class, double.class);
            primitiveTypesMap.put(Float.class, float.class);
            primitiveTypesMap.put(Integer.class, int.class);
            primitiveTypesMap.put(Long.class, long.class);
            primitiveTypesMap.put(Short.class, short.class);
        }

        private final Map<String, MethodHandle> _fieldSetters = new LinkedHashMap<>();
        private Map<String, Generator<?, ?>> _fieldGenerators = new LinkedHashMap<>();
        private List<Function<T, T>> _objectMutators = new ArrayList<>();

        public Reflection(Class<T> clz, Random randomizer) {
            super(clz, randomizer);
            try {
                this._constructorHandle = lookup.findConstructor(this._objectClass, MethodType.methodType(void.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        private MethodHandle findSetter(String fieldName, Class<?> clz) {
            MethodHandle result = null;
            Class<?> primitiveClass = null;
            if(primitiveTypesMap.containsKey(clz) || primitiveTypesMap.containsValue(clz)) {
                primitiveClass = (primitiveTypesMap.containsKey(clz)) ? primitiveTypesMap.get(clz) : primitiveTypesMap.inverse().get(clz);
            }

            try {
                result = lookup.findSetter(this._objectClass, fieldName, clz);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {

            }

            if (result == null && primitiveClass != null) {
                try {
                    result = lookup.findSetter(this._objectClass, fieldName, primitiveClass);
                } catch (IllegalAccessException | NoSuchFieldException ignored) {

                }
            }

            if(result == null) {
                String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                try {
                    result = lookup.findVirtual(this._objectClass, setterName, MethodType.methodType(void.class, clz));
                } catch (IllegalAccessException | NoSuchMethodException ignored) {

                }

                if (result == null && primitiveClass != null) {
                    try {
                        result = lookup.findVirtual(this._objectClass, setterName, MethodType.methodType(void.class, primitiveClass));
                    } catch (IllegalAccessException | NoSuchMethodException ignored) {

                    }
                }
            }

            return result;
        }

        public ObjectGenerator.Reflection<T> field(String fieldName, Generator<?, ?> fieldGenerator) {
            this._fieldGenerators.put(fieldName, fieldGenerator);
            MethodHandle fieldSetter = this.findSetter(fieldName, fieldGenerator.getTargetObjectClass());
            if (fieldSetter == null) {
                throw new NullPointerException("Could not find setter for field: " + fieldName);
            }
            this._fieldSetters.put(fieldName, fieldSetter);
            return this;
        }

        public ObjectGenerator.Reflection<T> mutate(Function<T, T> objectMutator) {
            this._objectMutators.add(objectMutator);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T generate() throws MockeyJockeyException {
            T objInstance;
            try {
                objInstance = (T) this._constructorHandle.invoke();
                // objInstance = this._objectClass.newInstance();
                // for (Field f : this._objectClass.getFields()) {
                //     f.set(objInstance, this._fieldGenerators.get(f.getName()).generate().get());
                // }
                for (String s : this._fieldGenerators.keySet()) {
                    this._fieldSetters.get(s).invoke(
                        objInstance,
                        this._fieldGenerators.get(s).get()
                    );
                }
                for (Function<T, T> mutator : this._objectMutators) {
                    objInstance = mutator.apply(objInstance);
                }
            } catch (MockeyJockeyException e) {
                throw e;
            } catch (Throwable throwable) {
                throw new MockeyJockeyException(throwable, this.getClass().getName(), this._tag);
            }
            return objInstance;
        }

    }

    public static class Constructor<T> extends ObjectGenerator<T> {

        private List<Generator<?, ?>> _constructorParams = new ArrayList<>();
        private List<Function<T, T>> _objectMutators = new ArrayList<>();

        public Constructor(Class<T> clz, Random randomizer) {
            super(clz, randomizer);
        }

        public ObjectGenerator.Constructor<T> constructorParams(List<Generator<?, ?>> params) {
            if (this._constructorHandle == null) {
                this._constructorParams.addAll(params);
                List<Class<?>> constructorParamTypes = this.getConstructorParamTypes();
                try {
                    this._constructorHandle = lookup.findConstructor(
                        this._objectClass,
                        MethodType.methodType(void.class, constructorParamTypes)
                    );
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public ObjectGenerator.Constructor<T> mutate(Function<T, T> objectMutator) {
            this._objectMutators.add(objectMutator);
            return this;
        }

        private List<Class<?>> getConstructorParamTypes() {
            List<Class<?>> result = new ArrayList<>();
            for (Generator<?, ?> g : this._constructorParams) {
                result.add(g.getTargetObjectClass());
            }
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T generate() {
            T objInstance = null;
            try {
                int paramsCount = this._constructorParams.size();
                Object[] params = new Object[paramsCount];
                for (int i = 0; i < paramsCount; i++) {
                    params[i] = this._constructorParams.get(i).get();
                }
                objInstance = (T) this._constructorHandle.invokeWithArguments(params);
                for (Function<T, T> mutator : this._objectMutators) {
                    objInstance = mutator.apply(objInstance);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return objInstance;
        }

    }

    @Override
    public Class<T> getTargetObjectClass() {
        return this._objectClass;
    }

}
