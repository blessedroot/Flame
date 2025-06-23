package com.flame.api.event;

import java.lang.annotation.*;

/**
 * author : s0ckett
 * date : 23.06.25
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubEvent {
    int priority() default 0;
}