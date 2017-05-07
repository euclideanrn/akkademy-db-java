package com.tunnell.akkademy.messages;

import java.io.Serializable;
import java.util.*;

/**
 * Created by TunnellZhao on 2017/5/7.
 * <br/>
 * Response: {@link BatchResponse}, this happens when return value contain multiple pairs
 */
public class BatchResponse implements Serializable {

    private final Map<String, Object> results;

    public BatchResponse() {
        results = null;
    }

    public BatchResponse(String key) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, null);
        results = Collections.unmodifiableMap(map);
    }

    public BatchResponse(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        results = Collections.unmodifiableMap(map);
    }

    public BatchResponse(Map<String, Object> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    public Object get(String key) {
        return results == null
                ? null
                : results.get(key);
    }

    public Set<String> getKeys() {
        return results == null
                ? Collections.emptySet()
                : results.keySet();
    }

    public Collection<Object> getValues() {
        return results == null
                ? Collections.emptyList()
                : Collections.unmodifiableCollection(results.values());
    }

    public Map<String, Object> getResults() {
        return results == null
                ? Collections.emptyMap()
                : results;
    }
}