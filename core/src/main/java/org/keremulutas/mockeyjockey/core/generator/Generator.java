package org.keremulutas.mockeyjockey.core.generator;

import com.google.common.reflect.TypeToken;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Generator<IT, OT> implements Consumer<IT>, Supplier<OT> {

    protected TypeToken<OT> _type = new TypeToken<OT>(getClass()) { static final long serialVersionUID = 1L; };

    protected Random _randomizer;
    protected OT _output;
    protected boolean _explicitReset = false;

    protected String _tag = "tag not set";

    public Generator(Random randomizer) {
        this._randomizer = randomizer;
    }

    public void reset() {
        this._output = null;
    }

    public Generator<IT, OT> withExplicitReset(boolean explicitReset) {
        this._explicitReset = explicitReset;
        return this;
    }

    public Generator<IT, OT> withTag(String tag) {
        this._tag = tag;
        return this;
    }

    public void accept(IT t) {

    }

    protected abstract OT generate();

    @Override
    public OT get() {
        if (this._explicitReset) {
            if(this._output == null) {
                this._output = this.generate();
            }
            return this._output;
        }

        this._output = this.generate();
        return this._output;
    }

    public OT getLastGeneratedValue() {
        return this._output;
    }

    public ListGenerator<OT> list(int length) {
        return new ListGenerator<OT>(this._randomizer)
            .length(new ConstantGenerator<>(length, this._randomizer))
            .source(this);
    }

    public ListGenerator<OT> list(Supplier<Integer> lengthGenerator) {
        return new ListGenerator<OT>(_randomizer)
            .length(lengthGenerator)
            .source(this);
    }

    public <T> Generator<IT, T> transform(Class<T> clz, Function<OT, T> fn) {
        return new TransformerGenerator<>(clz, this, fn);
    }

    @SuppressWarnings("unchecked")
    public Class<OT> getTargetObjectClass() {
        return (Class<OT>) this._type.getRawType();
    }

    public String getTag() {
        return _tag;
    }

}
