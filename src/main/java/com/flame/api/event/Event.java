package com.flame.api.event;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public abstract class Event {

    private final long timestamp;

    public Event() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * время создания события
     * @return timestamp в мс
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * имя события
     * @return имя класса
     */
    public String getEventName() {
        return getClass().getSimpleName();
    }
}
