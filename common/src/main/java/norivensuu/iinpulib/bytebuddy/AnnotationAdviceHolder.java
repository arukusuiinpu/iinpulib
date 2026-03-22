package norivensuu.iinpulib.bytebuddy;

import io.netty.util.internal.UnstableApi;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

import static norivensuu.iinpulib.Iinpulib.MOD_ID;
import static norivensuu.iinpulib.Iinpulib.adviceList;

public class AnnotationAdviceHolder {
    public Class<? extends Annotation> annotation;
    public Class<?> advice;

    public MethodScanner methodScanner;
    public RuntimeInjector runtimeInjector;

    public Logger logger;

    public AnnotationAdviceHolder(Class<? extends Annotation> annotation, Class<?> advice) {
        this.annotation = annotation;
        this.advice = advice;

        logger = LoggerFactory.getLogger(MOD_ID + " @" + annotation.getSimpleName());

        adviceList.add(Advice.to(advice).on(
                ElementMatchers.isAnnotatedWith(annotation)
        ));

        methodScanner = new MethodScanner(annotation, logger);
        runtimeInjector = new RuntimeInjector(annotation, advice, methodScanner, logger);
    }

    // DON'T USE THIS METHOD YET, the constructor is enough for now
    @UnstableApi
    public void Initialize() {
        methodScanner.scanAllAnnotatedMethods();
        runtimeInjector.inject();
    }
}
