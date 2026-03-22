package norivensuu.iinpulib.bytebuddy.advice;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

import static norivensuu.iinpulib.Iinpulib.LOGGER;

public class RegisterAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean enter(@Advice.This Object self) {
        LOGGER.info("RegisterAdvice onEnter");

        try {
            Field frozenField = self.getClass().getDeclaredField("frozen");
            frozenField.setAccessible(true);
            boolean frozen = (boolean) frozenField.get(self);
            if (frozen) {
                frozenField.set(self, false);
                return true;
            }
        } catch (Exception e) {
            // log
        }
        return false;
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.This Object self, @Advice.Enter boolean unfroze) {
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
