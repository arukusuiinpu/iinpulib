package norivensuu.iinpulib.conditions;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;

import static norivensuu.iinpulib.Iinpulib.LOGGER;

public class TestWaitCondition implements DependencyCondition {
    @Override
    public boolean condition(String value) {
        Minecraft mc = Minecraft.getInstance();
        // LOGGER.info("{} {}", mc.level, mc.player);

        return mc.level != null && mc.player != null;
    }
}