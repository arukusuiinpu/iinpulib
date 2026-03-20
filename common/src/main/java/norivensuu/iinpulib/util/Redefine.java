package norivensuu.iinpulib.util;

import norivensuu.iinpulib.dependencies.Dependency;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Requires {
    Dependency[] value() default {};
}

