package norivensuu.iinpulib.bytebuddy.advice;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

import static norivensuu.iinpulib.Iinpulib.LOGGER;

public class CreateIntrusiveHolderAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean enter(
            @Advice.This Object self,
            @Advice.AllArguments Object[] args
    ) {

        LOGGER.info("[Agent] createIntrusiveHolder intercepted. args={}", args[0]);
        // Here, we bypass the "no intrusive holders when frozen" check.
        // Option 1: let original code run, but force `frozen = false` temporarily.
        try {
            Field frozenField = self.getClass().getDeclaredField("frozen");
            frozenField.setAccessible(true);

            boolean frozen = (boolean) frozenField.get(self);
            if (frozen) {
                frozenField.set(self, false);
                // Signal to exit advice that we changed it.
                return true; // any non-default value
            }
        } catch (Exception e) {
            // your logging here
        }
        return false; // default → run original code as normal
    }

    @Advice.OnMethodExit
    public static void exit(
            @Advice.This Object self,
            @Advice.Enter boolean unfroze
    ) {
        if (unfroze) {
            try {
                Field frozenField = self.getClass().getDeclaredField("frozen");
                frozenField.setAccessible(true);
                frozenField.set(self, true);
            } catch (Exception e) {
                // log
            }
        }
    }
}