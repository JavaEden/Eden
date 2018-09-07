package com.eden.injection.annotations;

import com.eden.repositories.EdenRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EdenBible {
    Class<? extends EdenRepository> repository();

    String id() default "";

    boolean singleton() default true;
}
