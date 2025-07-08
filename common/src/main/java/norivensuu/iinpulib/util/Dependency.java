package norivensuu.iinpulib.util;

import norivensuu.iinpulib.conditions.*;

public @interface Dependency {
    String value();
    Class<? extends DependencyCondition> condition() default ModLoadedCondition.class;
}
