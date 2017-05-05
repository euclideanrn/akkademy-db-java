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

    public SetRequest(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SetRequest{" + "key=" + key + ", value=" + value + '}';
    }
}
