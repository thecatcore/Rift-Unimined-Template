package xyz.wagyourtail.unimined.minecraft.patch.rift

import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import xyz.wagyourtail.unimined.api.minecraft.patch.ataw.AccessTransformerPatcher
import xyz.wagyourtail.unimined.api.runs.RunConfig
import xyz.wagyourtail.unimined.internal.minecraft.MinecraftProvider
import xyz.wagyourtail.unimined.internal.minecraft.patch.AbstractMinecraftTransformer
import xyz.wagyourtail.unimined.internal.minecraft.patch.access.transformer.AccessTransformerMinecraftTransformer
import xyz.wagyourtail.unimined.util.FinalizeOnRead
import xyz.wagyourtail.unimined.util.LazyMutable

open class RiftPatch(
    project: Project,
    provider: MinecraftProvider,
) : AbstractMinecraftTransformer(project, provider, "Rift") {
    private val accessTransformerMinecraftTransformer: AccessTransformerMinecraftTransformer = AccessTransformerMinecraftTransformer(project, provider, "Rift")

    open var loader by FinalizeOnRead(LazyMutable {
        project.files("libs/Rift-FINAL.jar")
    });

    override fun apply() {
        createRiftDependency("org.spongepowered:mixin:0.7.11-SNAPSHOT")
        createRiftDependency("net.minecraft:launchwrapper:1.12")
        createRiftDependency("org.ow2.asm:asm:6.2", true)
        createRiftDependency("org.ow2.asm:asm-commons:6.2", true)

        val rift = project.dependencies.create(loader)
        provider.minecraftLibraries.dependencies.add(rift)

        super.apply()
    }

    override fun applyClientRunTransform(config: RunConfig) {
        config.mainClass = "net.minecraft.launchwrapper.Launch"

        config.args.addAll(arrayOf("--tweakClass", "org.dimdev.riftloader.launch.RiftLoaderClientTweaker").toList())
    }

    override fun applyServerRunTransform(config: RunConfig) {
        config.mainClass = "net.minecraft.launchwrapper.Launch"

        config.args.addAll(arrayOf("--tweakClass", "org.dimdev.riftloader.launch.RiftLoaderServerTweaker").toList())
    }

    private fun createRiftDependency(name: String, transitive: Boolean = false) {
        val dep = project.dependencies.create(name) as ModuleDependency

        dep.setTransitive(transitive)

        provider.minecraftLibraries.dependencies.add(dep)
    }
}