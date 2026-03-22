package norivensuu.iinpulib.conditions;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemRegistryFrozenCondition implements DependencyCondition {
    @Override
    public boolean condition(String value) {
        return ((MappedRegistry<Item>) BuiltInRegistries.ITEM).frozen;
    }
}