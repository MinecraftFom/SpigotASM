package com.fomdev.sasm.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class PluginClassUtil {
    private static Map<Class<? extends Annotation>, List<Class<?>>> annotated;
    private static Map<String, List<Class<?>>> pluginfiles;

    public static void applyClasses() {
        for (Plugin p: Bukkit.getPluginManager().getPlugins()) {
            if (p instanceof JavaPlugin && p.isEnabled()) {
                List<Class<?>> classes = getClassesFromPlugin((JavaPlugin) p);
                for (Class<?> clazz: classes) {
                    if (!pluginfiles.containsKey(p.getName())) {
                        pluginfiles.put(p.getName(), new ArrayList<>());
                    }

                    pluginfiles.get(p.getName()).add(clazz);
                    Bukkit.getLogger().info(
                            "[SASM/ASMLoader] Found class @0, loaded into asm data. Info: (from=@1, of=@2)"
                                    .replace("@0", clazz.getSimpleName())
                                    .replace("@1", p.getName())
                                    .replace("@2", clazz.getName())
                    );
                }
                pluginfiles.get(p.getName()).sort(Comparator.comparing(Class::getName));
            }
        }
    }

    public static void buildAnnotations() {
        if (pluginfiles == null || pluginfiles.isEmpty()) {
            Bukkit.getLogger().log(Level.SEVERE, "Accepted an invalid initialization before application of classes");
            return;
        }

        for (String i: pluginfiles.keySet()) {
            List<Class<?>> classes = pluginfiles.get(i);
            for (Class<?> clazz: classes) {
                for (Annotation a: clazz.getDeclaredAnnotations()) {
                    if (!annotated.containsKey(a.annotationType())) {
                        annotated.put(a.annotationType(), new ArrayList<>());
                    }

                    annotated.get(a.annotationType()).add(clazz);
                    Bukkit.getLogger().info
                            (
                                "[SASM/AnnotationLoader] Found annotation @0 in class @1. Info: (from=@2, of=@3)"
                                        .replace("@0", a.annotationType().getName())
                                        .replace("@1", clazz.getSimpleName())
                                        .replace("@2", i)
                                        .replace("@3", clazz.getName())
                            );
                }
            }
        }
    }

    @Nullable
    public static Class<?> findClass(String klass) {
        for (String i: pluginfiles.keySet()) {
            List<Class<?>> classes = pluginfiles.get(i);
            for (Class<?> clazz: classes) {
                if (clazz.getName().endsWith("." + klass)) {
                    return clazz;
                }
            }
        }

        return null;
    }

    @Nullable
    public static String findPlugin(Class<?> clazz) {
        for (String k: pluginfiles.keySet()) {
            if (pluginfiles.get(k).contains(clazz)) {
                return k;
            }
        }

        return null;
    }

    public static List<Class<?>> getAllMatch(Class<? extends Annotation> anno) {
        if (!annotated.containsKey(anno)) {
            return Collections.emptyList();
        }

        return annotated.get(anno);
    }

    public static List<Class<?>> getASMData(String pluginid) {
        if (!pluginfiles.containsKey(pluginid)) {
            return Collections.emptyList();
        }

        return pluginfiles.get(pluginid);
    }

    private static List<Class<?>> getClassesFromPlugin(JavaPlugin plugin) {
        List<Class<?>> classes = new ArrayList<>();

        try (JarFile file = new JarFile(getFileFromPlugin(plugin))){
            Enumeration<JarEntry> entries = file.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }

                String cp = name
                        .replaceAll("/", ".")
                        .replaceFirst(".class", "");

                classes.add(Class.forName(cp));
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Caught an error while scanning jar file " + e.getMessage());
        }

        return classes;
    }

    private static File getFileFromPlugin(JavaPlugin plugin) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getfile = JavaPlugin.class.getDeclaredMethod("getFile");
        getfile.setAccessible(true);
        return (File) getfile.invoke(plugin);
    }

    public static Set<String> getPluginEntries() {
        return pluginfiles.keySet();
    }

    public static void rescanCache() {
        annotated   = new HashMap<>();
        pluginfiles = new HashMap<>();
        applyClasses();
        buildAnnotations();
        Bukkit.getLogger().info("Successfully scheduled recache and rescan");
    }

    static {
        annotated   = new HashMap<>();
        pluginfiles = new HashMap<>();
    }
}