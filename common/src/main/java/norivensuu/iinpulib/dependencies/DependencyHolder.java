package norivensuu.iinpulib.dependencies;

import norivensuu.iinpulib.conditions.DependencyCondition;

import java.lang.reflect.Constructor;

public class DependencyHolder {
    public String value;
    public Class<? extends DependencyCondition> condition;

    public DependencyHolder(Dependency dependency) {
        value = dependency.value();
        condition = dependency.condition();
    }

    public boolean Check() {
        DependencyCondition holder = instantiateCondition(condition);

        return holder.condition(value);
    }

    public static DependencyCondition instantiateCondition(Class<? extends DependencyCondition> clazz) {
        try {
            Constructor<? extends DependencyCondition> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate DependencyCondition: " + clazz.getName(), e);
        }
    }
}