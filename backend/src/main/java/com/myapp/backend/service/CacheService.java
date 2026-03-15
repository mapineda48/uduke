package com.myapp.backend.service;

import java.util.List;
import java.util.Map;

public interface CacheService {

    void set(String key, String value);

    String get(String key);

    void delete(String key);

    void hashPut(String key, String hashKey, String value);

    String hashGet(String key, String hashKey);

    Map<String, String> hashGetAll(String key);

    void hashDelete(String key, String hashKey);

    void listRightPush(String key, String value);

    List<String> listRange(String key, long start, long end);

    void listTrim(String key, long start, long end);
}
