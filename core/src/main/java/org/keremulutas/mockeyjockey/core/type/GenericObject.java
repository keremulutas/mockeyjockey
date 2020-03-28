package org.keremulutas.mockeyjockey.core.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericObject extends HashMap<String, Object> {

    private void checkKeyExists(String key) {
        if (!this.containsKey(key)) {
            throw new RuntimeException("Field not found (key: " + key + ")");
        }
    }

    public String getString(String key) {
        this.checkKeyExists(key);
        return this.getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        Object result = get(key);
        if (result != null) {
            return result.toString();
        }
        return defaultValue;
    }

    public Integer getInteger(String key) {
        this.checkKeyExists(key);
        return getInteger(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Object obj = this.get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            return ((Number) obj).intValue();
        }
        return Integer.parseInt(obj.toString());
    }

    public Long getLong(String key) {
        this.checkKeyExists(key);
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            if (obj instanceof Long) {
                return (Long) obj;
            }
            return ((Number) obj).longValue();
        }
        return Long.parseLong(obj.toString());
    }

    public Float getFloat(String key) {
        this.checkKeyExists(key);
        return getFloat(key, null);
    }

    public Float getFloat(String key, Float defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            if (obj instanceof Float) {
                return (Float) obj;
            }
            return ((Number) obj).floatValue();
        }
        return Float.parseFloat(obj.toString());
    }

    public Double getDouble(String key) {
        this.checkKeyExists(key);
        return getDouble(key, null);
    }

    public Double getDouble(String key, Double defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            if (obj instanceof Double) {
                return (Double) obj;
            }
            return ((Number) obj).doubleValue();
        }
        return Double.parseDouble(obj.toString());
    }

    public Boolean getBoolean(String key) {
        this.checkKeyExists(key);
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object obj = get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return Boolean.parseBoolean(obj.toString());
    }

    public <T> Map<String, T> getMap(String key) {
        this.checkKeyExists(key);
        return getMap(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getMap(String key, Map<String, T> defaultValue) {
        Object obj = get(key);
        return obj != null ? (Map<String, T>) obj : defaultValue;
    }

    public <T> List<T> getList(String key) {
        this.checkKeyExists(key);
        return getList(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, List<T> defaultValue) {
        Object obj = get(key);
        return obj != null ? (List<T>) obj : defaultValue;
    }

}
