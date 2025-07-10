package norivensuu.iinpulib.bytebuddy.wrapper;

import norivensuu.iinpulib.dependencies.Dependency;
import norivensuu.iinpulib.dependencies.DependencyHolder;
import norivensuu.iinpulib.dependencies.DependencyRedirect;

import java.lang.reflect.Method;

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
    public boolean shouldRun() {
        DependencyRedirect requires = method.getAnnotation(DependencyRedirect.class);

        if (requires == null) return true;

        Dependency dependency = requires.value();

        if (!new DependencyHolder(dependency).Check()) {
            return false;
        }

        return true;
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
