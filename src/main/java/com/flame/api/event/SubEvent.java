package com.flame.api.event;

import java.lang.annotation.*;

/**
 * author : s0ckett
 * date : 30.01.26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubEvent {

    int priority() default 0;

    boolean ignoreCancelled() default false;
}
