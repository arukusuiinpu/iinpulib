package norivensuu.iinpulib.dependencies;

import norivensuu.iinpulib.conditions.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Dependency {
    String value();
    Class<? extends DependencyCondition> ifCondition() default NullCondition.class;
    Class<? extends DependencyCondition> whenCondition() default NullCondition.class;
}
