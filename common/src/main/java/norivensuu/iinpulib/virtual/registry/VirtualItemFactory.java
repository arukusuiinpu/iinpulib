package norivensuu.iinpulib.virtual.registry;

import net.bytebuddy.ByteBuddy;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.bytebuddy.matcher.ElementMatchers.*;

public final class VirtualItemFactory {

    private static final Map<String, Class<? extends Item>> CACHE = new ConcurrentHashMap<>();

    private VirtualItemFactory() {}

    public static Class<? extends Item> getOrCreateItemClass(String internalName) {
        return CACHE.computeIfAbsent(internalName, VirtualItemFactory::createItemClass);
    }

    private static Class<? extends Item> createItemClass(String internalName) {
        try {
            return new ByteBuddy()
                    .subclass(Item.class)
                    .name("norivensuu.iinpulib.virtual." + internalName)
                    // no explicit constructor definition needed in most MC versions
                    .make()
                    .load(VirtualItemFactory.class.getClassLoader())
                    .getLoaded();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate dynamic item class", e);
        }
    }

    public static Item newInstance(String internalName, Item.Properties props) {
        try {
            Class<? extends Item> clazz = getOrCreateItemClass(internalName);
            return clazz.getConstructor(Item.Properties.class).newInstance(props);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create dynamic item instance " + internalName, e);
        }
    }
}
