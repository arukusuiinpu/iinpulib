package norivensuu.iinpulib;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;
import norivensuu.iinpulib.bytebuddy.AdviceHolder;
import norivensuu.iinpulib.bytebuddy.advice.DependencyRedirectAdvice;
import norivensuu.iinpulib.bytebuddy.advice.RequiresAdvice;
import norivensuu.iinpulib.dependencies.DependencyRedirect;
import norivensuu.iinpulib.util.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public final class Iinpulib {
    public static final String MOD_ID = "iinpulib";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Instrumentation instrumentation;

    public static List<AsmVisitorWrapper.ForDeclaredMethods> adviceList = new ArrayList<>();

    public static AdviceHolder requiresHolder = new AdviceHolder(Requires.class, RequiresAdvice.class);
    public static AdviceHolder dependencyRedirectHolder = new AdviceHolder(DependencyRedirect.class, DependencyRedirectAdvice.class);

    public static void init() {
        instrumentation = ByteBuddyAgent.install();
        premain(null, instrumentation);
        LOGGER.info("Installing ByteBuddy Agent...");
    }

    public static void premain(String options, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.nameStartsWith("norivensuu.iinpulib")
                        .or(ElementMatchers.nameContains("iinpulib"))
                )
                .transform((builder, td, cl, m, d) ->
                        {
                            for (var advice : adviceList) builder = builder.visit(advice);
                            return builder;
                        }
                ).installOn(instrumentation);
    }
}
