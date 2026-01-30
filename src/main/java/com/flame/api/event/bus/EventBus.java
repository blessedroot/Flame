package com.flame.api.event.bus;

import com.flame.api.event.Cancellable;
import com.flame.api.event.SubEvent;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public class EventBus {

    private static final Logger LOGGER = Logger.getLogger(EventBus.class.getName());

    private static final Map<Class<?>, List<Subscriber>> subscribers = new ConcurrentHashMap<>();
    private static final Map<Object, Set<Class<?>>> listenerRegistry = new ConcurrentHashMap<>();

    private static boolean debugMode = false;

    public static void register(Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        Set<Class<?>> registeredEvents = new HashSet<>();

        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubEvent.class)) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                LOGGER.warning(String.format(
                        "Method %s in %s has @SubEvent but wrong parameter count (expected 1, got %d)",
                        method.getName(), listener.getClass().getName(), method.getParameterCount()
                ));
                continue;
            }

            Class<?> eventType = method.getParameterTypes()[0];
            SubEvent annotation = method.getAnnotation(SubEvent.class);

            method.setAccessible(true);

            Subscriber subscriber = new Subscriber(listener, method, annotation.priority(), annotation.ignoreCancelled());

            subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(subscriber);
            registeredEvents.add(eventType);

            if (debugMode) {
                LOGGER.info(String.format(
                        "Registered handler: %s.%s for event %s (priority: %d, ignoreCancelled: %b)",
                        listener.getClass().getSimpleName(), method.getName(),
                        eventType.getSimpleName(), annotation.priority(), annotation.ignoreCancelled()
                ));
            }
        }

        if (!registeredEvents.isEmpty()) {
            listenerRegistry.put(listener, registeredEvents);

            // Сортируем подписчиков по приоритету после регистрации
            registeredEvents.forEach(eventType ->
                    subscribers.get(eventType).sort(Comparator.comparingInt(s -> -s.priority))
            );
        } else {
            LOGGER.warning(String.format(
                    "Listener %s registered but has no valid @SubEvent methods",
                    listener.getClass().getName()
            ));
        }
    }

    /**
     * отменяет регистрацию слушателя
     * @param listener объект слушателя
     * @return true если слушатель был зарегистрирован и успешно удалён
     */
    public static boolean unregister(Object listener) {
        if (listener == null) {
            return false;
        }

        Set<Class<?>> events = listenerRegistry.remove(listener);
        if (events == null) {
            return false;
        }

        for (Class<?> eventType : events) {
            List<Subscriber> subs = subscribers.get(eventType);
            if (subs != null) {
                subs.removeIf(sub -> sub.instance == listener);

                if (subs.isEmpty()) {
                    subscribers.remove(eventType);
                }
            }
        }

        if (debugMode) {
            LOGGER.info(String.format(
                    "Unregistered listener: %s (%d event types)",
                    listener.getClass().getSimpleName(), events.size()
            ));
        }

        return true;
    }

    /**
     * вызывает событие
     * @param event событие для вызова
     * @throws IllegalArgumentException если event равен nullnullnullnullnullnull
     */
    public static void fire(Object event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        Class<?> eventClass = event.getClass();
        List<Subscriber> handlers = subscribers.getOrDefault(eventClass, Collections.emptyList());

        if (debugMode) {
            LOGGER.info(String.format(
                    "Firing event: %s (%d handlers)",
                    eventClass.getSimpleName(), handlers.size()
            ));
        }

        boolean isCancellable = event instanceof Cancellable;

        for (Subscriber subscriber : handlers) {
            // Пропускаем обработчик если событие отменено и обработчик игнорирует отменённые
            if (isCancellable && subscriber.ignoreCancelled) {
                Cancellable cancellable = (Cancellable) event;
                if (cancellable.isCancelled()) {
                    continue;
                }
            }

            try {
                subscriber.method.invoke(subscriber.instance, event);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, String.format(
                        "Error while handling event %s in %s.%s",
                        eventClass.getSimpleName(),
                        subscriber.instance.getClass().getSimpleName(),
                        subscriber.method.getName()
                ), ex);
            }
        }
    }

    /**
     * чекает зареган ли слушатель
     * @param listener объект слушателя
     * @return true если слушатель зареган
     */
    public static boolean isRegistered(Object listener) {
        return listener != null && listenerRegistry.containsKey(listener);
    }

    /**
     * получает количество зарегистрированных обработчиков для типа события
     * @param eventType тип события
     * @return количество обработчиков
     */
    public static int getHandlerCount(Class<?> eventType) {
        List<Subscriber> handlers = subscribers.get(eventType);
        return handlers != null ? handlers.size() : 0;
    }

    /**
     * получает количество зарегистрированных типов событий
     * @return количество типов событий
     */
    public static int getEventTypeCount() {
        return subscribers.size();
    }

    /**
     * получает количество зарегистрированных слушателей
     * @return количество слушателей
     */
    public static int getListenerCount() {
        return listenerRegistry.size();
    }

    /**
     * очистка всех регистрации
     */
    public static void clearAll() {
        subscribers.clear();
        listenerRegistry.clear();

        if (debugMode) {
            LOGGER.info("Cleared all event registrations");
        }
    }

    /**
     * on/off debug
     * @param enabled true для включения
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        LOGGER.info("Debug mode " + (enabled ? "enabled" : "disabled"));
    }

    public static Set<Class<?>> getRegisteredEventTypes() {
        return Collections.unmodifiableSet(subscribers.keySet());
    }

    private static class Subscriber {
        final Object instance;
        final Method method;
        final int priority;
        final boolean ignoreCancelled;

        Subscriber(Object instance, Method method, int priority, boolean ignoreCancelled) {
            this.instance = instance;
            this.method = method;
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Subscriber)) return false;
            Subscriber other = (Subscriber) obj;
            return instance == other.instance && method.equals(other.method);
        }

        @Override
        public int hashCode() {
            return Objects.hash(instance, method);
        }
    }
}