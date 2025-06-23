package com.flame.api.event.bus;

import com.flame.api.event.SubEvent;

import java.lang.reflect.Method;
import java.util.*;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class EventBus {
    private static final Map<Class<?>, List<Subscriber>> subscribers = new HashMap<>();

    public static void register(Object listener) {
        for (Method m : listener.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(SubEvent.class) && m.getParameterCount() == 1) {
                Class<?> evtType = m.getParameterTypes()[0];
                subscribers
                        .computeIfAbsent(evtType, k -> new ArrayList<>())
                        .add(new Subscriber(listener, m, m.getAnnotation(SubEvent.class).priority()));
            }
        }
        subscribers.values().forEach(list -> list.sort(Comparator.comparingInt(s -> -s.priority)));
    }

    public static void fire(Object event) {
        List<Subscriber> list = subscribers.getOrDefault(event.getClass(), Collections.emptyList());
        for (Subscriber s : list) {
            try { s.method.invoke(s.instance, event); }
            catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private static class Subscriber {
        final Object instance;
        final Method method;
        final int priority;
        Subscriber(Object i, Method m, int p) { instance=i; method=m; priority=p; }
    }
}