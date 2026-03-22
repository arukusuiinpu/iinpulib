package norivensuu.iinpulib.bytebuddy.wrapper;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static norivensuu.iinpulib.Iinpulib.LOGGER;
import static norivensuu.iinpulib.dependencies.DependencyHolder.EXECUTOR;

public abstract class RuntimeWrapper {

    public Method method;

    public AtomicReference<Boolean> shouldRun() {
        return new AtomicReference<Boolean>(true);
    }

    public RuntimeWrapper(Method method) {
        this.method = method;
    }

    public static void schedule(Method method, Object target, Object[] args, AtomicReference<Boolean> atomic) {
        EXECUTOR.submit(() -> {
            try {
                while (atomic.get() == null) {
                    Thread.sleep(100L);
                }
                method.setAccessible(true);
                method.invoke(target, args);
            } catch (Exception e) {
                // handle reflection errors
                e.printStackTrace();
            }
        });
    }
}