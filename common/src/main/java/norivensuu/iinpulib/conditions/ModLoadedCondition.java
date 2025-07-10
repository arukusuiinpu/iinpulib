package norivensuu.iinpulib.conditions;

import dev.architectury.platform.Platform;

public class ModLoadedCondition implements DependencyCondition {
    @Override
    public boolean condition(String value) {
        return Platform.isModLoaded(value);
    }
}