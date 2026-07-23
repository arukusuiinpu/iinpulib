package norivensuu.iinpulib.fabric;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import norivensuu.iinpulib.Iinpulib;

public final class IinpulibFabric implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        Iinpulib.init();
    }
}
