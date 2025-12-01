package norivensuu.iinpulib.dependencies;

import norivensuu.iinpulib.conditions.DependencyCondition;

import java.lang.reflect.Constructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DependencyHolder {
    public String value;
    public Class<? extends DependencyCondition> ifCondition;
    public Class<? extends DependencyCondition> whenCondition;

    // Simple shared executor for whenCondition polling
    private static final ExecutorService WHEN_EXECUTOR =
            Executors.newCachedThreadPool();

    public DependencyHolder(Dependency dependency) {
        value = dependency.value();
        ifCondition = dependency.ifCondition();
        whenCondition = dependency.whenCondition();
    }

    /**
     * Synchronous "if" check – unchanged.
     */
    public boolean Check() {
        DependencyCondition holder = instantiateCondition(ifCondition);
        return holder.condition(value);
    }

    /**
     * Asynchronous "when" – runs `callback` once,
     * the first time whenCondition(value) becomes true.
     *
     * If whenCondition is DependencyCondition.class (or some sentinel),
     * or null, this is a no-op.
     */
    public void whenAsync(Consumer<DependencyHolder> callback,
                          long pollIntervalMillis) {

        if (whenCondition == null
                || whenCondition == DependencyCondition.class) {
            // No when-condition specified – nothing to do
            return;
        }

        final DependencyCondition condition = instantiateCondition(whenCondition);
        final AtomicBoolean fired = new AtomicBoolean(false);

        WHEN_EXECUTOR.submit(() -> {
            try {
                while (!fired.get()) {
                    // evaluate condition
                    if (condition.condition(value)) {
                        if (fired.compareAndSet(false, true)) {
                            // fire exactly once
                            callback.accept(this);
                        }
                        break;
                    }

                    // sleep before next check
                    Thread.sleep(pollIntervalMillis);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Optional: log interruption
            } catch (Throwable t) {
                // Optional: log unexpected errors
                throw new IllegalStateException(
                        "Error while evaluating whenCondition " + whenCondition.getName(), t);
            }
        });
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
