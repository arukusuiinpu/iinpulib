package norivensuu.iinpulib.fabric;

import norivensuu.iinpulib.Iinpulib;

public final class IinpulibFabric implements net.fabricmc.api.ModInitializer {
    @Override
    public void onInitialize() {
        Iinpulib.init();
    }
}
