package norivensuu.iinpulib.dependencies;

import norivensuu.iinpulib.conditions.DependencyCondition;
import norivensuu.iinpulib.conditions.NullCondition;

import java.lang.reflect.Constructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DependencyHolder {
    public String value;
    public Class<? extends DependencyCondition> ifCondition;
    public Class<? extends DependencyCondition> whenCondition;

    public static final ExecutorService EXECUTOR =
            Executors.newCachedThreadPool();

    public DependencyHolder(Dependency dependency) {
        value = dependency.value();
        ifCondition = !dependency.ifCondition().equals(NullCondition.class) ? dependency.ifCondition() : null;
        whenCondition = !dependency.whenCondition().equals(NullCondition.class) ? dependency.whenCondition() : null;
    }

    public AtomicReference<Boolean> checkOrWait(long pollIntervalMillis) {
        var atomic = new AtomicReference<Boolean>(null);
        if (ifCondition != null) {
            atomic.set(check());
        }
        else if (whenCondition != null) {
            return whenAsync(pollIntervalMillis);
        }

        return atomic;
    }

    public AtomicReference<Boolean> checkOrWait() {
        return checkOrWait(200L);
    }

    public boolean check() {
        if (ifCondition != null) {
            DependencyCondition holder = instantiateCondition(ifCondition);
            return holder.condition(value);
        }
        else return true;
    }

    public AtomicReference<Boolean> whenAsync(long pollIntervalMillis) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        var atomic = new AtomicReference<Boolean>(null);

        if (whenCondition == null
                || whenCondition == DependencyCondition.class) {
            atomic.set(true);
            return atomic;
        }

        final DependencyCondition condition = instantiateCondition(whenCondition);
        final AtomicBoolean fired = new AtomicBoolean(false);

        EXECUTOR.submit(() -> {
            try {
                while (!fired.get() && !future.isCancelled()) {
                    if (condition.condition(value)) {
                        if (fired.compareAndSet(false, true)) {
                            future.complete(true);
                            atomic.set(true);
                        }
                        break;
                    }

                    Thread.sleep(pollIntervalMillis);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                future.completeExceptionally(e);
            } catch (Throwable t) {
                future.completeExceptionally(
                        new IllegalStateException(
                                "Error while evaluating whenCondition " + whenCondition.getName(), t));
            }
        });

        return atomic;
    }

    public AtomicReference<Boolean> whenAsync() {
        return whenAsync(200L);
    }

    public static DependencyCondition instantiateCondition(Class<? extends DependencyCondition> clazz) {
        try {
            Constructor<? extends DependencyCondition> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate DependencyCondition: " + clazz.getName(), e);
        }
    }
}
