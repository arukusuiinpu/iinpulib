package norivensuu.iinpulib.util;

import norivensuu.iinpulib.dependencies.Dependency;
import norivensuu.iinpulib.dependencies.DependencyHolder;

public interface ConditionalInitializer {

    void onInitialize();

    static ConditionalInitializer Initialize(Class<? extends ConditionalInitializer> clazz) {
        if (clazz.isAnnotationPresent(Requires.class)) {
            var requirements = clazz.getAnnotation(Requires.class);

            for (Dependency dependency : requirements.value()) {
                DependencyHolder holder = new DependencyHolder(dependency);
                if (!holder.check()) return null;
            }
        }

        // At this point it's safe to load the class and run its static initializers
        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader()); // Triggers <clinit> only now
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // Now instantiate
        ConditionalInitializer localInstance = createInitializer(clazz);
        localInstance.onInitialize();
        return localInstance;
    }

    private static ConditionalInitializer createInitializer(Class<? extends ConditionalInitializer> clazz) {
        ConditionalInitializer instance = createInstance(clazz);

        if (!(instance instanceof ConditionalInitializer)) {
            throw new IllegalArgumentException(clazz.getName() + " does not implement ConditionalInitializer");
        }

        return instance;
    }

    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
