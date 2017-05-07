package com.tunnell.akkademy.messages;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Created by TunnellZhao on 2017/5/5.
 *
 * Command: {@link GetRequest}
 */
public class GetRequest implements Serializable {
    private final String key;

    private final Pattern pattern;

    public GetRequest(String key) {
        this.key = key;
        this.pattern = null;
    }

    public GetRequest(String key, boolean isRegex) {
        if (isRegex) {
            this.key = null;
            this.pattern = Pattern.compile(key);
        } else {
            this.key = key;
            this.pattern = null;
        }
    }

    public String getKey() {
        return key;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isRegex() {
        return pattern != null;
    }

    @Override
    public String toString() {
        return "GetRequest{" +
                "key='" + key + '\'' +
                ", pattern=" + pattern +
                '}';
    }
}