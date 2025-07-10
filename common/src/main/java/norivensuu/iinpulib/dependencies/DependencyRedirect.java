package norivensuu.iinpulib.dependencies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DependencyRedirect {
    Dependency value();
    // Expecting format: package.ClassName.methodName
    String redirectTo();
}