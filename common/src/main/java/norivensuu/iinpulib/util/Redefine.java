package norivensuu.iinpulib.util;

import io.netty.util.internal.UnstableApi;
import norivensuu.iinpulib.conditions.DependencyCondition;
import norivensuu.iinpulib.conditions.NullCondition;
import norivensuu.iinpulib.dependencies.Dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UnstableApi
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Redefine {
    Class<?> value();
    Class<? extends DependencyCondition> ifCondition() default NullCondition.class;
    Class<? extends DependencyCondition> whenCondition() default NullCondition.class;
}

