package com.tunnell.akkademy.messages;

import java.io.Serializable;

/**
 * Created by TunnellZhao on 2017/5/8.
 * <br/>
 * Response: {@link SingleResponse}, this happens when return value is a single pair
 */
public class SingleResponse implements Serializable {
    private final String key;
    private final Object value;

    public SingleResponse() {
        this(null, null);
    }

    public SingleResponse(String key, Object value) {
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
        return "SingleResponse{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
