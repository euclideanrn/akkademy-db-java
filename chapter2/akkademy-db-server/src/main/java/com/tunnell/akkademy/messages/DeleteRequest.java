package com.tunnell.akkademy.messages;

import java.io.Serializable;

/**
 * Created by TunnellZhao on 2017/5/7.
 * <p>
 * Command: {@link DeleteRequest}
 */
public class DeleteRequest implements Serializable {
    private final String key;

    private final boolean deleteAndGet;

    public DeleteRequest(String key) {
        this.key = key;
        this.deleteAndGet = false;
    }

    public DeleteRequest(String key, boolean deleteAndGet) {
        this.key = key;
        this.deleteAndGet = deleteAndGet;
    }

    public String getKey() {
        return key;
    }

    public boolean deleteAndGet() {
        return deleteAndGet;
    }

    @Override
    public String toString() {
        return "DeleteRequest{" +
                "key='" + key + '\'' +
                ", deleteAndGet=" + deleteAndGet +
                '}';
    }
}
