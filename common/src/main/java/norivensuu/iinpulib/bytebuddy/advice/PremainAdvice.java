package norivensuu.iinpulib.bytebuddy.advice;

import net.bytebuddy.asm.Advice;
import norivensuu.iinpulib.bytebuddy.wrapper.PremainRuntimeWrapper;
import norivensuu.iinpulib.bytebuddy.wrapper.RequiredRuntimeWrapper;
import norivensuu.iinpulib.bytebuddy.wrapper.RuntimeWrapper;

import java.lang.reflect.Method;

public class PremainAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean enter(@Advice.Origin Method method,
                                @Advice.This(optional = true) Object thisObj,
                                @Advice.AllArguments Object[] args) {
        var atomic = new PremainRuntimeWrapper(method).shouldRun();

        if (atomic.get() == null) {
            RuntimeWrapper.schedule(method, thisObj, args, atomic);
            return true;
        }

        return !atomic.get();
    }

    @Advice.OnMethodExit
    public static void exit() {
        // noop — only needed to complete the Advice structure
    }
}
