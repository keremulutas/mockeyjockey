package org.keremulutas.mockeyjockey.core.generator;

import java.util.function.Function;

public class TransformerGenerator<IT, OT> extends Generator<IT, OT> {

    private Class<OT> _objectClass;
    private Generator<?, IT> _sourceGenerator;
    private Function<IT, OT> _fn;

    public TransformerGenerator(Class<OT> clz, Generator<?, IT> sourceGenerator, Function<IT, OT> fn) {
        super(sourceGenerator._randomizer);
        this._objectClass = clz;
        this._sourceGenerator = sourceGenerator;
        this._fn = fn;
    }

    @Override
    protected OT generate() {
        this._input = this._sourceGenerator.get();
        return this._fn.apply(this._input);
    }

    @SuppressWarnings("unchecked")
    public Class<OT> getTargetObjectClass() {
        return this._objectClass;
    }

}
