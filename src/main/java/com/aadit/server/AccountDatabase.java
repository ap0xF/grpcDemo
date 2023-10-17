package com.aadit.server;

import java.util.HashMap;
import java.util.Map;

public class AccountDatabase {

    /* this is DB
    * 1 => 10
    * 2 => 20
    * .
    * .
    * .
    * 9 => 90;
    * */

    public static final Map<Integer, Integer> Map;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            Integer v = i;
            if (map.put(v, 0) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Map = map;
    }

    public static int getBalanceFromDB(int accountId){
        return Map.get(accountId);
    }

    public static void addBalanceToDB(int accountId, int amount){
        Map.computeIfPresent(accountId, (k, v) -> v + amount);
    }

    public static void deductBalanceToDB(int accountId, int amount){
        Map.computeIfPresent(accountId, (k, v) -> v - amount);
    }
}
