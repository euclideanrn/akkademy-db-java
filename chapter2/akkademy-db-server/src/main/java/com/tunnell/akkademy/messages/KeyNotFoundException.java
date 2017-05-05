package com.tunnell.akkademy.messages;

import java.io.Serializable;

/**
 * Created by TunnellZhao on 2017/5/5.
 *
 * Throws {@link KeyNotFoundException} if there's no value matched for input key from {@link GetRequest}
 */
public class KeyNotFoundException extends Exception implements Serializable {
    private final String key;

    public KeyNotFoundException(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}