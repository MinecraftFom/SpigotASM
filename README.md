# SpigotASM
A spigot plugin aim to collect asm data for plugins to use.

This plugin (or api) will cache every plugin's classes and associated it with a 
map which contains annotation info and class info, in order to faster and simplier
process annotations. Since the Java annotation are kinda hard to use, I chose
this in replace of the forge's asm data (since bukkit don't have these)

**Usages**
*In Game*
*Commands*
    - /sasm list -> list every classes cached, advanced usages: /sasm list [annotation | plugin] (annotation: will search for all classes annotated with the given annotation, plugin: every class in the plugin]
    - /sasm locate -> Locates the class according to its simple name
    - /sasm rescan -> rescans the asm data

*In Development*
*Java*

Since there could be compatibility problems (for e.g., if the plugin load sequence was wrong, the plugin is likely to not collect the data of the plugin), so,
for ensuration, you can call PluginClassUtil.rescanCache() to refresh and recollect asm data

Also, you can use PluginClassUtil.getAllMatch(Class<? extends Annotation>) in order to get every single class capitalled with the given annotation
Also, you can use PluginClassUtil.getASMData(String) in order to get the asm data for the specified plugin (same as /sasm list plugin)
