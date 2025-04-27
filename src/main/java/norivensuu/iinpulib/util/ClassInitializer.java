package norivensuu.iinpulib.util;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static norivensuu.iinpulib.Iinpulib.DoAfterModsLoad;

public interface ClassInitializer {
    void onInitialize();

    static ClassInitializer Initialize(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ClassTag.class)) {
            AtomicReference<ClassInitializer> instance = new AtomicReference<>();
            DoAfterModsLoad(Arrays.stream(clazz.getAnnotation(ClassTag.class).dependencies()).toList(), () -> {
                ClassInitializer localInstance = (ClassInitializer) createInstance(clazz);
                localInstance.onInitialize();
                instance.set(localInstance);
            });
            return instance.get();
        }
        else {
            ClassInitializer localInstance = (ClassInitializer) createInstance(clazz);
            localInstance.onInitialize();
            return localInstance;
        }
    }

    private static Object createInstance(Class<?> clazz) {
        if (!ClassInitializer.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class must implement ClassInitializer!");
        }

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}
