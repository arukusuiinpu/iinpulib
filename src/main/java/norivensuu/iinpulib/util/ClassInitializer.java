package norivensuu.iinpulib.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import norivensuu.iinpulib.Loading;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static norivensuu.iinpulib.Loading.*;

public interface ClassInitializer {
    Map<String, Loading.ModPredicate> MOD_PREDICATE_MAP = new HashMap<>();

    void onInitialize();

    static ClassInitializer Initialize(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ClassTag.class)) {
            AtomicReference<ClassInitializer> instance = new AtomicReference<>();

            List<Object> registries = new ArrayList<>();
            for (Class<? extends Registry<?>> registryClass : Arrays.stream(clazz.getAnnotation(ClassTag.class).registries()).toList()) {
                Registry<?> temp;
                try {
                    temp = registryClass
                            .getDeclaredConstructor()
                            .newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Cannot instantiate " + registryClass, e);
                }

                registries.add(Registries.REGISTRIES.get(temp.getKey().getValue()));
            }

            for (String dependency : clazz.getAnnotation(ClassTag.class).dependencies()) {

                Loading.ModPredicate predicate = MOD_PREDICATE_MAP.containsKey(dependency) ? MOD_PREDICATE_MAP.get(dependency) : clazz.getAnnotation(ClassTag.class).predicate();

                DoAfterModLoads(dependency, predicate, uncheckedToRegistries(registries), () -> {
                    ClassInitializer localInstance = (ClassInitializer) createInstance(clazz);
                    if (localInstance != null) {
                        localInstance.onInitialize();
                    }
                    instance.set(localInstance);
                });

            }
            return instance.get();
        }
        else {
            ClassInitializer localInstance = (ClassInitializer) createInstance(clazz);
            if (localInstance != null) {
                localInstance.onInitialize();
            }
            return localInstance;
        }
    }
    static ClassInitializer Initialize(Class<?> clazz, Loading.ModPredicate predicate) {
        AddDefaultPredicate(clazz, predicate);
        return Initialize(clazz);
    }

    @SuppressWarnings("unchecked")
    private static List<? extends Registry<?>> uncheckedToRegistries(List<Object> objects) {
        return (List<? extends Registry<?>>)(List<?>)objects;
    }

    static void AddDefaultPredicate(Class<?> clazz, Loading.ModPredicate predicate) {
        if (clazz.isAnnotationPresent(ClassTag.class)) {
            for (String dependency : clazz.getAnnotation(ClassTag.class).dependencies()) {
                if (dependency != null) MOD_PREDICATE_MAP.put(dependency, predicate);
            }
        }
    }

    private static Object createInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }
}
