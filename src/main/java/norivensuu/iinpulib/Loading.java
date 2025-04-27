package norivensuu.iinpulib;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import norivensuu.iinpulib.util.ClassInitializer;

import java.util.ArrayList;
import java.util.List;

public class Loading implements ClassInitializer {
    @Override
    public void onInitialize() {

    }

    public static void DoAfterModsLoad(List<String> ids, String modPredicate, Runnable method) {
        new Thread(() -> {
            for (String id : ids) {
                List<Item> loaded_items = new ArrayList<>();
                while ((FabricLoader.getInstance().getModContainer(id).isPresent() && !ContainsAnyModdedItem(loaded_items, id)) && modPredicate.equals(ModPredicate.ANYITEM)) {

                    loaded_items.clear();
                    for (Item item : Registries.ITEM) {
                        loaded_items.add(item);
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (ids.stream().allMatch(id -> FabricLoader.getInstance().getModContainer(id).isPresent()) || !modPredicate.equals(ModPredicate.PRESENCE)) method.run();
        }).start();
    }

    public static void DoAfterModsLoad(List<String> ids, Runnable method) {
        DoAfterModsLoad(ids, ModPredicate.ANYITEM, method);
    }

    public static void DoAfterModLoads(String id, String modPredicate, Runnable method) {
        DoAfterModsLoad(List.of(id), modPredicate, method);
    }

    public static void DoAfterModLoads(String id, Runnable method) {
        DoAfterModLoads(id, ModPredicate.ANYITEM, method);
    }

    public static boolean ContainsAnyModdedItem(List<Item> loaded_items, String mod_id) {
        for (Item item : loaded_items) {
            if (Registries.ITEM.getId(item).getNamespace().equals(mod_id)) {
                return true;
            }
        }
        return false;
    }

    public static class ModPredicate {
        public static String ANYITEM = "anyitem";
        public static String PRESENCE = "presence";
    }
}
