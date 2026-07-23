package norivensuu.iinpulib.conditions;

import dev.architectury.platform.Platform;
import norivensuu.iinpulib.Iinpulib;

public class InstrumentationCondition implements DependencyCondition {
    @Override
    public boolean condition(String value) {
        return Iinpulib.instrumentation != null;
    }
}