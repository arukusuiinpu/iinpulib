package norivensuu.iinpulib;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import norivensuu.iinpulib.bytebuddy.AnnotationAdviceHolder;
import norivensuu.iinpulib.bytebuddy.advice.CreateIntrusiveHolderAdvice;
import norivensuu.iinpulib.bytebuddy.advice.DependencyRedirectAdvice;
import norivensuu.iinpulib.bytebuddy.advice.RegisterAdvice;
import norivensuu.iinpulib.bytebuddy.advice.RequiresAdvice;
import norivensuu.iinpulib.conditions.*;
import norivensuu.iinpulib.dependencies.Dependency;
import norivensuu.iinpulib.dependencies.DependencyRedirect;
import norivensuu.iinpulib.util.Redefine;
import norivensuu.iinpulib.util.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.*;

public final class Iinpulib {
    public static final String MOD_ID = "iinpulib";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Instrumentation instrumentation;

    // For those who are wondering, adviceList is populated by the AdviceHolder constructor
    public static List<AsmVisitorWrapper.ForDeclaredMethods> adviceList = new ArrayList<>();

    // So these two are adding the advice to the list on initialization, they may be unused, but I think exposing them might be helpful
    public static AnnotationAdviceHolder requiresHolder = new AnnotationAdviceHolder(Requires.class, RequiresAdvice.class);
    public static AnnotationAdviceHolder dependencyRedirectHolder = new AnnotationAdviceHolder(DependencyRedirect.class, DependencyRedirectAdvice.class);

    static List<String> dependentModIds = FabricLoader.getInstance()
            .getAllMods().stream()
            .filter(mod -> mod.getMetadata().getDependencies().stream()
                    .anyMatch(dep -> dep.getModId().equals(Iinpulib.MOD_ID)) ||
                    mod.getMetadata().getId().equals(MOD_ID))
            .map(mod -> mod.getMetadata().getId()) // extract just the mod ID
            .toList();

    public static void init() {
        ByteBuddyAgent.install();
//
//        LOGGER.info("ByteBuddy agent installed dynamically");
//
        instrumentation = ByteBuddyAgent.getInstrumentation();
        agentmain(null, instrumentation);
//
//        TestC();
    }

//    @Requires(@Dependency(value = "", whenCondition = TestWaitCondition.class))
//    public static void TestC() {
//        LOGGER.info("TestC log");
//
//        // Cast the item registry to MappedRegistry
//        MappedRegistry<Item> itemRegistry = (MappedRegistry<Item>) BuiltInRegistries.ITEM;
//
//        ResourceKey<Item> itemKey = ResourceKey.create(
//                BuiltInRegistries.ITEM.key(),  // ResourceKey<? extends Registry<Item>>
//                Objects.requireNonNull(ResourceLocation.tryBuild(Iinpulib.MOD_ID, "star"))
//        );
//
//        Item starItem = new Item(new Item.Properties().setId(itemKey));
//
//        Holder<Item> holder = itemRegistry.createIntrusiveHolder(
//                starItem
//        );
//
//        Registry.register(BuiltInRegistries.ITEM, itemKey, starItem);
//
//        // Now the item has a proper holder and ResourceLocation
//        LOGGER.info("Dynamic item registered: {}", holder.unwrapKey().get().location());
//    }


    public static void premain(String options, Instrumentation instrumentation) {

        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(named("net.minecraft.core.MappedRegistry"))
                .transform((builder, type, classLoader, module, protectionDomain) -> {
                            builder
                                    .visit(Advice.to(CreateIntrusiveHolderAdvice.class)
                                            .on(nameContainsIgnoreCase("intrusive")))
                                    .visit(Advice.to(RegisterAdvice.class)
                                            .on(nameContainsIgnoreCase("registerAdvice")));

                            builder.visit(new AsmVisitorWrapper.ForDeclaredMethods());
                            return builder;
                        }
                ).installOn(instrumentation);

        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type((type, loader, module, clazz, domain) -> {
                    // Check if the class belongs to a package of a dependent mod
                    String name = type.getName();

                    if (dependentModIds.stream().anyMatch(name::contains)) {
                        // LOGGER.info(name);
                    }

                    return dependentModIds.stream().anyMatch(name::contains);
                })
                .transform((builder, type, cl, m, d) ->
                        {
                            for (var advice : adviceList) {
                                builder = builder.visit(advice);
                            }
                            return builder;
                        }
                ).installOn(instrumentation);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }
}

