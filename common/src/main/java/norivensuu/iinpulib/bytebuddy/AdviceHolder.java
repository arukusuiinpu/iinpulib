package norivensuu.iinpulib.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

import static norivensuu.iinpulib.Iinpulib.MOD_ID;
import static norivensuu.iinpulib.Iinpulib.adviceList;

public class AdviceHolder {
    public MethodScanner methodScanner;
    public RuntimeInjector runtimeInjector;

    public Logger logger;

    public AdviceHolder(Class<? extends Annotation> annotation, Class<?> advice) {
        logger = LoggerFactory.getLogger(MOD_ID + " @" + annotation.getSimpleName());

        adviceList.add(Advice.to(advice).on(
                ElementMatchers.isAnnotatedWith(annotation)
        ));

        methodScanner = new MethodScanner(annotation, logger);
        runtimeInjector = new RuntimeInjector(annotation, advice, methodScanner, logger);
    }

    public void Initialize() {
        methodScanner.scanAllAnnotatedMethods();
        runtimeInjector.inject();
    }
}
