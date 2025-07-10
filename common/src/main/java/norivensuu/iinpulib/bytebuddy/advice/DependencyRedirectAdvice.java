package norivensuu.iinpulib.bytebuddy.advice;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import norivensuu.iinpulib.bytebuddy.wrapper.DependencyRedirectRuntimeWrapper;

import java.lang.reflect.Method;

import static norivensuu.iinpulib.Iinpulib.LOGGER;

public class DependencyRedirectAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean enter(
            @Advice.Origin Method method,
            @Advice.AllArguments Object[] args,
            @Advice.Local("redirectResult") Object result
    ) {

        DependencyRedirectRuntimeWrapper wrapper = new DependencyRedirectRuntimeWrapper(method);

        if (!wrapper.shouldRun())
            return false;

        if (method.getReturnType().equals(void.class)) {
            wrapper.invokeRedirect(wrapper.targetName(), args); // no result
        } else {
            result = wrapper.invokeRedirect(wrapper.targetName(), args); // capture result
        }

        return true; // skip original
    }

    @Advice.OnMethodExit
    public static void exit(
            @Advice.Origin Method method,
            @Advice.Local("redirectResult") Object result,
            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object ret
    ) {
        if (!method.getReturnType().equals(void.class)) {
            ret = result;
        }
    }
}
