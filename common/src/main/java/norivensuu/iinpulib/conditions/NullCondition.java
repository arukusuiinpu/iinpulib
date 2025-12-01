package norivensuu.iinpulib.conditions;

public class AlwaysTrueCondition implements DependencyCondition {
    @Override
    public boolean condition(String value) {
        return true;
    }
}