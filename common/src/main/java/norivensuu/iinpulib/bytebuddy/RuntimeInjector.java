package norivensuu.iinpulib.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static norivensuu.iinpulib.Iinpulib.instrumentation;

public class RuntimeInjector {

    public Class<? extends Annotation> aClass;
    public MethodScanner methodScanner;
    public Class<?> advice;
    public Logger logger;

    public RuntimeInjector(Class<? extends Annotation> aClass, Class<?> advice, MethodScanner methodScanner, Logger logger) {
        this.aClass = aClass;
        this.methodScanner = methodScanner;
        this.advice = advice;
        this.logger = logger;
    }
    
    public List<String> successfulMethods = new ArrayList<>();
    public List<String> failedClasses = new ArrayList<>();

    public void inject() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        for (Class<?> clazz : MethodScanner.findAllClasses(contextClassLoader)) {
            try {
                var methods = methodScanner.getAnnotatedMethods(clazz);

                if (methods.isEmpty()) continue;

                // Check if class is loaded
                boolean loaded = Arrays.stream(instrumentation.getAllLoadedClasses())
                        .anyMatch(c -> c.equals(clazz));

                if (!loaded) {
                    continue;
                }

                List<Method> targetMethods = Arrays.stream(clazz.getDeclaredMethods())
                        .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                                .anyMatch(ann -> ann.annotationType().getName().equals(aClass.getName())))
                        .toList();

                ElementMatcher.Junction<NamedElement> methodMatcher = ElementMatchers.none();
                for (Method method : targetMethods) {
                    methodMatcher = methodMatcher.or(ElementMatchers.named(method.getName()));
                }

                byte[] modified = new ByteBuddy()
                        .redefine(clazz)
                        .visit(Advice.to(advice).on(methodMatcher))
                        .make()
                        .load(clazz.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent()).getBytes();

                successfulMethods.add(clazz.getName() + ": " + Arrays.toString(targetMethods.toArray()));

            } catch (Throwable ex) {
                failedClasses.add(clazz.getName());
                
                ex.printStackTrace();
            }
        }
        
        if (!successfulMethods.isEmpty())
            logger.info("Successfully transformed: {}", successfulMethods);
        if (!failedClasses.isEmpty()) logger.error("Failed to transform: {}", failedClasses);
    }
}