package norivensuu.iinpulib.bytebuddy.wrapper;

import norivensuu.iinpulib.conditions.InstrumentationCondition;
import norivensuu.iinpulib.dependencies.Dependency;
import norivensuu.iinpulib.dependencies.DependencyHolder;
import norivensuu.iinpulib.util.Premain;
import norivensuu.iinpulib.util.Requires;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static norivensuu.iinpulib.Iinpulib.dependenciesPremain;

public class PremainRuntimeWrapper extends RuntimeWrapper {
    public PremainRuntimeWrapper(Method method) {
        super(method);
    }

    @Override
    public AtomicReference<Boolean> shouldRun() {
        dependenciesPremain();

        var atomic = new AtomicReference<Boolean>(null);

        var annotation = method.getAnnotation(Premain.class);

        if (annotation == null) {
            atomic.set(true);
            return atomic;
        }

        DependencyHolder holder = new DependencyHolder(null, null, InstrumentationCondition.class);

        if (!holder.check()) {
            atomic.set(false);
            return atomic;
        }

        return holder.checkOrWait();
    }
}
