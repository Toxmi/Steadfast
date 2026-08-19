package com.toxmi.steadfast.core.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked","unused"})
public class MenuSession {
    private final Map<String, Object> data = new ConcurrentHashMap<>();

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value == null) return null;
        if(!type.isInstance(value)) return null;
        return (T) value;
    }
    public Object get(String key) {
        return data.get(key);
    }

    public void clear() {
        data.clear();
    }
    public void remove(String key) {
        data.remove(key);
    }

}