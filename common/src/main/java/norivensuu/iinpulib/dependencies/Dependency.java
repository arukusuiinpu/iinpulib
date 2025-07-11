package norivensuu.iinpulib.dependencies;

import norivensuu.iinpulib.conditions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface StringDependency {
    String value();
    Class<? extends DependencyCondition> condition() default ModLoadedCondition.class;
}

