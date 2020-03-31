package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataConvert {

    public static List<Object[]> listMapToObjectArray(List<Map<String, Object>> data, String[] keys) {
        List<Object[]> destList = new ArrayList();
        data.stream().forEach(map ->
                {
                    Object[] objects = new Object[keys.length];
                    for (int i = 0; i < objects.length; i++) {
                        objects[i] = map.containsKey(keys[i]) ? map.get(keys[i]) : null;
                    }
                    destList.add(objects);
                }
        );
        return destList;
    }

    public static List<Object[]> listMapToObjectArray(List<Map<String, Object>> data, String[] keys, String singleKey) {
        List<Object[]> destList = new ArrayList();
        data.stream().forEach(map ->
                {
                    Object[] objects = new Object[keys.length];
                    for (int i = 0; i < objects.length; i++) {
                        objects[i] = map.containsKey(keys[i]) ?
                                keys[i].equals(singleKey) ? map.get(keys[i]).toString().split("\\.")[0] : map.get(keys[i])
                                : null;
                    }
                    destList.add(objects);
                }
        );
        return destList;
    }
}
