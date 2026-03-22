package norivensuu.iinpulib.conditions;

public class NullCondition implements DependencyCondition {
    // Should technically be AtomicReference<Boolean> for proper null implementation, but I suppose it's okay for now.
    @Override
    public boolean condition(String value) {
        return false;
    }
}