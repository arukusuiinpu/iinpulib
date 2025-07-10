package norivensuu.iinpulib.bytebuddy;

import io.github.classgraph.ClassGraph;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class MethodScanner {

    public Class<? extends Annotation> aClass;
    public Logger logger;

    public MethodScanner(Class<? extends Annotation> aClass, Logger logger) {
        this.aClass = aClass;
        this.logger = logger;
    }

    public final Set<MethodIdentifier> annotatedMethods = new HashSet<>();

    public void scanAllAnnotatedMethods() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> allClasses = findAllClasses(cl);

        for (Class<?> clazz : allClasses) {
            try {
                for (Method method : getAnnotatedMethods(clazz)) {
                    annotatedMethods.add(MethodIdentifier.of(method));

                    break;
                }
            } catch (NoClassDefFoundError | ExceptionInInitializerError | SecurityException ignored) {
                // Ignore broken classes (common in mod environments)
            }
        }

        logger.info("Found @{} on {} methods", aClass.getSimpleName(), annotatedMethods.toArray().length);
    }

    public Set<Method> getAnnotatedMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();

        try {
            for (Method method : clazz.getDeclaredMethods()) {
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (annotation.annotationType().getName().equals(aClass.getName())) {
                        methods.add(method);

                        break;
                    }
                }
            }
        } catch (NoClassDefFoundError | ExceptionInInitializerError | SecurityException ignored) {
            // Ignore broken classes (common in mod environments)
        }

        return methods;
    }

    public static Set<Class<?>> findAllClasses(ClassLoader cl) {
        Set<Class<?>> classes = new HashSet<>();
        try (var scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("norivensuu.iinpulib")
                .scan()) {

            for (var classInfo : scanResult.getAllClasses()) {
                try {
                    classes.add(classInfo.loadClass(false));
                } catch (Throwable ignored) {}
            }
        }
        return classes;
    }

    public boolean isRequires(Method method) {
        return annotatedMethods.contains(MethodIdentifier.of(method));
    }

    public record MethodIdentifier(String owner, String name, String descriptor) {
        public static MethodIdentifier of(Method m) {
            return new MethodIdentifier(m.getDeclaringClass().getName(), m.getName(), getDescriptor(m));
        }

        public static String getDescriptor(Method m) {
            StringBuilder desc = new StringBuilder("(");
            for (Class<?> p : m.getParameterTypes()) {
                desc.append(toDescriptor(p));
            }
            desc.append(")");
            desc.append(toDescriptor(m.getReturnType()));
            return desc.toString();
        }

        private static String toDescriptor(Class<?> cls) {
            if (cls.isPrimitive()) {
                if (cls == void.class) return "V";
                else if (cls == boolean.class) return "Z";
                else if (cls == byte.class) return "B";
                else if (cls == char.class) return "C";
                else if (cls == short.class) return "S";
                else if (cls == int.class) return "I";
                else if (cls == long.class) return "J";
                else if (cls == float.class) return "F";
                else if (cls == double.class) return "D";
                else throw new IllegalArgumentException("Unknown primitive: " + cls);
            } else if (cls.isArray()) {
                return cls.getName().replace('.', '/');
            } else {
                return "L" + cls.getName().replace('.', '/') + ";";
            }
        }
    }
}