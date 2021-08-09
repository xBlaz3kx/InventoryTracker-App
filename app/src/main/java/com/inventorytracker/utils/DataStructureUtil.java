package com.inventorytracker.utils;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataStructureUtil {

    public static <T> void insertIntoArray(ArrayList<T> list, T someObject) {
        if (!list.contains(someObject)) {
            list.add(someObject);
        }
    }

    public static <T> void insertOrDeleteArrayList(ArrayList<T> list, T someObject) {
        if (list.contains(someObject)) {
            list.remove(someObject);
        } else {
            list.add(someObject);
        }
    }

    public static <T, E> void insertOrReplace(Map<T, E> map, T key, E value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        } else {
            map.replace(key, value);
        }
    }

    public static void insertOrAdd(HashMap<String, Integer> hashMap, String key, Integer value) {
        if (hashMap.containsKey(key)) {
            try {
                hashMap.replace(key, hashMap.get(key) + value);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        } else {
            hashMap.put(key, value);
        }
    }

    public static <T, E> void insertOrReplaceHashMap(HashMap<T, E> map, T key, E value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        } else {
            map.replace(key, value);
        }
    }
}
