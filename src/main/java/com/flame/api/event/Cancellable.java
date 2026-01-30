package com.flame.api.event;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public interface Cancellable {

    /**
     * чекает отменено ли событие
     * @return true
     */
    boolean isCancelled();

    /**
     * установка состояния отмены события
     * @param cancelled true
     */
    void setCancelled(boolean cancelled);
}
