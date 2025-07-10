package norivensuu.iinpulib.bytebuddy.advice;

import net.bytebuddy.asm.Advice;
import norivensuu.iinpulib.bytebuddy.wrapper.RequiredRuntimeWrapper;
import norivensuu.iinpulib.bytebuddy.wrapper.RuntimeWrapper;

import java.lang.reflect.Method;

public class RequiresAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean enter(@Advice.Origin Method method) {
        boolean result = new RequiredRuntimeWrapper(method).shouldRun();
        return !result;
    }

    @Advice.OnMethodExit
    public static void exit() {
        // noop — only needed to complete the Advice structure
    }
}
