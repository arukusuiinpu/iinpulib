package norivensuu.iinpulib.util;

import net.minecraft.registry.Registry;
import norivensuu.iinpulib.Loading;
import static norivensuu.iinpulib.Loading.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClassTag {
    String[] dependencies();
    ModPredicate predicate() default ModPredicate.PRESENCE;
    Class<? extends Registry<?>>[] registries() default {};
}