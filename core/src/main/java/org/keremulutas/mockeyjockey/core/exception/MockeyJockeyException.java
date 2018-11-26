package org.keremulutas.mockeyjockey.core.exception;

import org.keremulutas.mockeyjockey.core.generator.Generator;

public class MockeyJockeyException extends RuntimeException {

    private String _component;
    private String _tag;

    public MockeyJockeyException(String message, String component, String tag) {
        super(message);
        this._component = component;
        this._tag = tag;
    }

    public MockeyJockeyException(String message, Throwable cause, String component, String tag) {
        super(message, cause);
        this._component = component;
        this._tag = tag;
    }

    public MockeyJockeyException(Throwable cause, String component, String tag) {
        super(cause);
        this._component = component;
        this._tag = tag;
    }

    public MockeyJockeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String component, String tag) {
        super(message, cause, enableSuppression, writableStackTrace);
        this._component = component;
        this._tag = tag;
    }

    public MockeyJockeyException(String message, Generator<?, ?> component) {
        super(message);
        this._component = component.getClass().getSimpleName();
        this._tag = component.getTag();
    }

    public MockeyJockeyException(String message, Throwable cause, Generator<?, ?> component) {
        super(message, cause);
        this._component = component.getClass().getSimpleName();
        this._tag = component.getTag();
    }

    public MockeyJockeyException(Throwable cause, Generator<?, ?> component) {
        super(cause);
        this._component = component.getClass().getSimpleName();
        this._tag = component.getTag();
    }

    public MockeyJockeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Generator<?, ?> component) {
        super(message, cause, enableSuppression, writableStackTrace);
        this._component = component.getClass().getSimpleName();
        this._tag = component.getTag();
    }

    public String getComponent() {
        return _component;
    }

    public String getTag() {
        return _tag;
    }

    @Override
    public String toString() {
        return "MockeyJockeyException{" +
            "component='" + _component + '\'' +
            ", tag='" + _tag + '\'' +
            ", msg='" + this.getMessage() + '\'' +
            '}';
    }

}
