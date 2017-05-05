package com.tunnell.akkademy.messages;

import java.io.Serializable;

/**
 * Created by TunnellZhao on 2017/5/5.
 *
 * Command: {@link GetRequest}
 */
public class GetRequest implements Serializable {
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "GetRequest{" + "key=" + key + '}';
    }
}
