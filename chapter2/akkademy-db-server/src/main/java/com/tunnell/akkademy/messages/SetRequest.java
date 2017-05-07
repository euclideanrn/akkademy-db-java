package com.tunnell.akkademy.messages;

import java.io.Serializable;

/**
 * Created by TunnellZhao on 2017/4/28.
 *
 * Command: {@link SetRequest}
 */
public class SetRequest implements Serializable {
    private final String key;
    private final Object value;

    private final boolean setAndGet;

    private final boolean setIfNotExists;

    public SetRequest(String key, Object value) {
        this.key = key;
        this.value = value;
        this.setAndGet = false;
        this.setIfNotExists = false;
    }

    public SetRequest(String key, Object value, boolean setAndGet, boolean setIfNotExists) {
        this.key = key;
        this.value = value;
        this.setAndGet = setAndGet;
        this.setIfNotExists = setIfNotExists;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public boolean setAndGet() {
        return setAndGet;
    }

    public boolean setIfNotExists() {
        return setIfNotExists;
    }

    @Override
    public String toString() {
        return "SetRequest{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", setAndGet=" + setAndGet +
                ", setIfNotExists=" + setIfNotExists +
                '}';
    }
}
