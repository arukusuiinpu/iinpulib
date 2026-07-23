package norivensuu.iinpulib.bytebuddy.advice;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static norivensuu.iinpulib.Iinpulib.LOGGER;
import static norivensuu.iinpulib.util.Checksums.sha256;

public class DynamicAdvice {

    public DynamicAdvice(String id) {
        enterAdvices.put(sha256(id), this::dynamicEnter);
        exitAdvices.put(sha256(id), this::dynamicExit);
    }

    public boolean dynamicEnter(Object self) {
        return enterAdvices.get(sha256(self)).apply(self);
    }

    public void dynamicExit(Object self, boolean unfroze) {
        exitAdvices.get(sha256(self)).accept(self, unfroze);
    }


    public static Map<String, Function<Object, Boolean>> enterAdvices = new HashMap<>();
    public static Map<String, BiConsumer<Object, Boolean>> exitAdvices = new HashMap<>();

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean enter(@Advice.This Object self) {
        LOGGER.info(self.toString());
        LOGGER.info(sha256(self));
        try {
            return enterAdvices.get(sha256(self)).apply(self);
        }
        catch (Exception e) {
            return true;
        }
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.This Object self, @Advice.Enter boolean unfroze) {
        try {
            exitAdvices.get(sha256(self)).accept(self, unfroze);
        }
        catch (Exception ignored) {
        }
    }
}
