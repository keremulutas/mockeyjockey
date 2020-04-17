package org.keremulutas.mockeyjockey.core.generator;

import java.util.function.Function;

// OIT: original input type, IIT: intermediate input type
public class TransformerGenerator<OIT, IIT, OT> extends Generator<OIT, OT> {

    private Class<OT> _objectClass;
    private Generator<OIT, IIT> _sourceGenerator;
    private Function<IIT, OT> _fn;

    public TransformerGenerator(Class<OT> clz, Generator<OIT, IIT> sourceGenerator, Function<IIT, OT> fn) {
        super(sourceGenerator._randomizer);
        this._objectClass = clz;
        this._sourceGenerator = sourceGenerator;
        this._fn = fn;
    }

    @Override
    protected OT generate() {
        IIT value = this._sourceGenerator.get();
        return this._fn.apply(value);
    }

    @SuppressWarnings("unchecked")
    public Class<OT> getTargetObjectClass() {
        return this._objectClass;
    }

}
