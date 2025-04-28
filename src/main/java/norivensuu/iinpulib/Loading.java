package norivensuu.iinpulib;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import norivensuu.iinpulib.util.ClassInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Loading implements ClassInitializer {
    @Override
    public void onInitialize() {

    }

    private static final ConcurrentMap<String, Boolean> completedChecks = new ConcurrentHashMap<>();

    public static void DoAfterModsLoad(List<String> ids, ModPredicate modPredicate, List<? extends Registry<?>> registries, Runnable method) {
        new Thread(() -> {
            String key = generateKey(ids, modPredicate, registries);

            if (completedChecks.getOrDefault(key, false)) {
                method.run();
                return;
            }

            for (String id : ids) {
                boolean allInCheck = false;

                while (!allInCheck) {
                    switch (modPredicate) {
                        case PRESENCE -> allInCheck = true;
                        case ANYITEM -> allInCheck = ContainsAnyModdedItem(id);
                        // case ANYTHING -> allInCheck = ContainsAnythingModded(id);
                        case REGISTRY -> allInCheck = ContainsModded(id, registries);
                    }

                    if (!allInCheck) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            if (ids.stream().allMatch(id -> FabricLoader.getInstance().getModContainer(id).isPresent()) || !modPredicate.equals(ModPredicate.PRESENCE)) {
                completedChecks.put(key, true);
                method.run();
            }
        }).start();
    }

    private static String generateKey(List<String> ids, ModPredicate modPredicate, List<? extends Registry<?>> registries) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(modPredicate.name()).append(":");
        ids.stream().sorted().forEach(id -> keyBuilder.append(id).append(","));
        registries.stream().map(Object::hashCode).sorted().forEach(hash -> keyBuilder.append(hash).append(","));
        return keyBuilder.toString();
    }

    public static void DoAfterModsLoad(List<String> ids, Runnable method) {
        DoAfterModsLoad(ids, ModPredicate.ANYITEM, List.of(), method);
    }

    public static void DoAfterModLoads(String id, ModPredicate modPredicate, List<? extends Registry<?>> registries, Runnable method) {
        DoAfterModsLoad(List.of(id), modPredicate, registries, method);
    }

    public static void DoAfterModLoads(String id, Runnable method) {
        DoAfterModLoads(id, ModPredicate.ANYITEM, List.of(), method);
    }

    public static boolean ContainsAnyModdedItem(String modId) {
        return ContainsModded(modId, List.of(Registries.ITEM));
    }

    public static boolean ContainsAnythingModded(String modId) {
        return ContainsModded(modId, Registries.REGISTRIES.stream().toList());
    }

    public static boolean ContainsModded(String modId, List<? extends Registry<?>> registries) {
        List<Object> loaded = new ArrayList<>();
        System.out.println(registries);
        for (Registry<?> registry : registries) {
            for (var raw : registry) {
                loaded.add(raw);
            }
            if (containsInRegistry(registry, loaded, modId)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean containsInRegistry(Registry<?> registry, List<?> loaded, String modId) {
        Class<T> clazz = (Class<T>) Objects.requireNonNull(registry.get(0)).getClass();

        Registry<T> regT = (Registry<T>) registry;

        for (Object raw : loaded) {
            Class<?> currentClass = clazz;
            Object casted = null;

            while (currentClass != null) {
                try {
                    casted = currentClass.cast(raw);
                    break; // success
                } catch (ClassCastException ignored) {
                    currentClass = currentClass.getSuperclass(); // Try parent
                }
            }

            if (casted == null) continue; // Could not cast at all

            @SuppressWarnings("unchecked")
            T typed = (T) casted;

            try {
                if (Objects.requireNonNull(regT.getId(typed)).getNamespace().equals(modId)) {
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public enum ModPredicate {
        PRESENCE,
        ANYITEM,
        REGISTRY
    }
}
