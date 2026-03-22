package norivensuu.iinpulib.bytebuddy.wrapper;

import norivensuu.iinpulib.dependencies.*;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static norivensuu.iinpulib.dependencies.DependencyHolder.EXECUTOR;

public class DependencyRedirectRuntimeWrapper extends RuntimeWrapper {
    public DependencyRedirectRuntimeWrapper(Method method) {
        super(method);
    }

    public String targetName() {
        DependencyRedirect requires = method.getAnnotation(DependencyRedirect.class);

        if (requires == null) return null;

        return requires.redirectTo();
    }

    @Override
    public AtomicReference<Boolean> shouldRun() {
        var atomic = new AtomicReference<Boolean>(null);
        DependencyRedirect requires = method.getAnnotation(DependencyRedirect.class);

        if (requires == null) {
            atomic.set(true);
            return atomic;
        }

        Dependency dependency = requires.value();

        DependencyHolder holder = new DependencyHolder(dependency);

        return holder.checkOrWait();
    }

    public Object invokeRedirect(String targetName, Object[] args) {
        try {
            Method target = resolveMethod(targetName); // resolve based on annotation
            return target.invoke(null, args); // static method expected
        } catch (Exception e) {
            throw new RuntimeException("Redirect failed", e);
        }
    }

    private Method resolveMethod(String fullName) {
        // Expecting format: package.ClassName.methodName
        int lastDot = fullName.lastIndexOf('.');
        if (lastDot == -1) throw new IllegalArgumentException("Invalid method name: " + fullName);

        String className = fullName.substring(0, lastDot);
        String methodName = fullName.substring(lastDot + 1);

        try {
            Class<?> cls = Class.forName(className);
            // You can improve this to match exact parameters
            for (Method m : cls.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) {
                    m.setAccessible(true);
                    return m;
                }
            }
            throw new NoSuchMethodException("Method " + methodName + " not found in " + className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve redirect method: " + fullName, e);
        }
    }
}
