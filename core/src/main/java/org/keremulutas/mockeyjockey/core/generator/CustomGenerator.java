package org.keremulutas.mockeyjockey.core.generator;

import java.util.Random;
import java.util.function.Supplier;

public class CustomGenerator<OT> extends Generator<Void, OT> {

    private Class<OT> _objectClass;
    private Supplier<OT> _supplier;

    public CustomGenerator(Class<OT> clz, Supplier<OT> supplier, Random randomizer) {
        super(randomizer);
        this._objectClass = clz;
        this._supplier = supplier;
    }

    @Override
    protected OT generate() {
        return this._supplier.get();
    }

    @Override
    public Class<OT> getTargetObjectClass() {
        return this._objectClass;
    }

}
