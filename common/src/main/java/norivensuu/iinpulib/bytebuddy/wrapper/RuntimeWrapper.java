package norivensuu.iinpulib.bytebuddy.wrapper;

import java.lang.reflect.Method;

public abstract class RuntimeWrapper {

    public Method method;

    public abstract boolean shouldRun();

    public RuntimeWrapper(Method method) {
        this.method = method;
    }
}