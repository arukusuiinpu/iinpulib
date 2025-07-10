package norivensuu.iinpulib.bytebuddy.wrapper;

import norivensuu.iinpulib.dependencies.Dependency;
import norivensuu.iinpulib.dependencies.DependencyHolder;
import norivensuu.iinpulib.util.Requires;

import java.lang.reflect.Method;

public class RequiredRuntimeWrapper extends RuntimeWrapper {
    public RequiredRuntimeWrapper(Method method) {
        super(method);
    }

    @Override
    public boolean shouldRun() {
        Requires requires = method.getAnnotation(Requires.class);

        if (requires == null) return true;

        Dependency[] dependencies = requires.value();

        for (Dependency dep : dependencies) {
            if (!new DependencyHolder(dep).Check()) {
                return false;
            }
        }

        return true;
    }
}
