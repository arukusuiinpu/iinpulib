package norivensuu.iinpulib.conditions;

import dev.architectury.platform.Platform;
import norivensuu.iinpulib.Iinpulib;

public class DependenciesResolvedCondition implements DependencyCondition {
    @Override
    public boolean condition(String value) {
        return Iinpulib.dependentModIds.stream().allMatch(Platform::isModLoaded);
    }
}