package norivensuu.iinpulib.virtual.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VirtualItemFactory {

    public static Class<? extends net.minecraft.world.item.Item> createItemClass(String internalName) {
        try {
            return new net.bytebuddy.ByteBuddy()
                    .subclass(net.minecraft.world.item.Item.class)
                    .name("norivensuu.iinpulib.generated." + internalName)
                    .defineConstructor(net.bytebuddy.description.modifier.Visibility.PUBLIC)
                    .withParameters(net.minecraft.world.item.Item.Properties.class)
                    .intercept(net.bytebuddy.implementation.SuperMethodCall.INSTANCE)
                    // Example: override use() procedurally later via Advice or Implementation
                    .make()
                    .load(VirtualItemFactory.class.getClassLoader())
                    .getLoaded();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate dynamic item class", e);
        }
    }
}
