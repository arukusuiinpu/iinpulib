package norivensuu.iinpulib.bytebuddy.wrapper;

import norivensuu.iinpulib.dependencies.Dependency;
import norivensuu.iinpulib.dependencies.DependencyHolder;
import norivensuu.iinpulib.util.Requires;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static norivensuu.iinpulib.Iinpulib.LOGGER;

public class RequiredRuntimeWrapper extends RuntimeWrapper {
    public RequiredRuntimeWrapper(Method method) {
        super(method);
    }

    @Override
    public AtomicReference<Boolean> shouldRun() {
        var atomic = new AtomicReference<Boolean>(null);

        Requires requires = method.getAnnotation(Requires.class);

        if (requires == null) {
            atomic.set(true);
            return atomic;
        }

        Dependency[] dependencies = requires.value();

        for (var dependency : dependencies) {
            DependencyHolder holder = new DependencyHolder(dependency);

            if (!holder.check()) {
                atomic.set(false);
                return atomic;
            }

            atomic = holder.checkOrWait();
        }

        return atomic;
    }
}
