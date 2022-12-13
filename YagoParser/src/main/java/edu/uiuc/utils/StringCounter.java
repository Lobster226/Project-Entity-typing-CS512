package edu.uiuc.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringCounter {
    private final HashMap<String, int[]> mCounter = new HashMap<>();

    public void addOne(String key) {
        int[] valueWrapper = this.mCounter.get(key);
        if (valueWrapper == null) {
            this.mCounter.put(key, new int[]{1});
        } else {
            valueWrapper[0]++;
        }
    }

    public Set<Map.Entry<String, int[]>> getEntries(){
        return this.mCounter.entrySet();
    }

    public HashSet<String> filterByMinCount(int minCount) {
        HashSet<String> set = new HashSet<>();
        for (HashMap.Entry<String, int[]> entry : this.mCounter.entrySet()) {
            if (entry.getValue()[0] >= minCount) {
                set.add(entry.getKey());
            }
        }
        return set;
    }
}
