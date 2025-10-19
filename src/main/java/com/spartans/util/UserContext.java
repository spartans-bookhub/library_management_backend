package com.spartans.util;

import java.util.HashMap;
import java.util.Map;

public class UserContext {
    private static final ThreadLocal<Map<String, Object>> currentUser = new ThreadLocal<>();

    public static Map<String, Object> getUser() {
        return currentUser.get();
    }

    public static void setUser(Map<String, Object> userMap) {
        currentUser.set(userMap);
    }

    public static Long getUserId() {
        Map<String, Object> map = currentUser.get();
        return (map != null) ? (Long)map.get("id") : 0;
    }

    public static String getRole() {
        Map<String, Object> map = currentUser.get();
        return (map != null) ? (String)map.get("role") : "";
    }

    public static void setUserId(Long id) {
        Map<String, Object> map = currentUser.get();
        if (map == null) {
            map = new HashMap<>();
            currentUser.set(map);
        }
        map.put("id", id);
    }

    public static void setRole(String role) {
        Map<String, Object> map = currentUser.get();
        if (map == null) {
            map = new HashMap<>();
            currentUser.set(map);
        }
        map.put("role", role);
    }

    public static String getEmail() {
        Map<String, Object> map = currentUser.get();
        return (map != null) ? (String)map.get("email") : "";
    }

    public static void clear() {
        currentUser.remove();

    }
}
