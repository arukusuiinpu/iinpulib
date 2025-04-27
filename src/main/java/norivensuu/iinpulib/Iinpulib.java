package norivensuu.iinpulib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import norivensuu.iinpulib.util.ClassInitializer;

import java.util.ArrayList;
import java.util.List;

public class Iinpulib implements ModInitializer {
    @Override
    public void onInitialize() {
        ClassInitializer.Initialize(Loading.class);
    }
}
