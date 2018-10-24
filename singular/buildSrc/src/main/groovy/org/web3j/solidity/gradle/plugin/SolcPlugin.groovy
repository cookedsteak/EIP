package org.web3j.solidity.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

import javax.inject.Inject

import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize
import static org.web3j.solidity.gradle.plugin.SoliditySourceSet.NAME

/**
 * Gradle plugin for Solidity compile automation.
 */
class SolcPlugin implements Plugin<Project> {

    private final SourceDirectorySetFactory sourceFactory

    @Inject
    SolcPlugin(final SourceDirectorySetFactory sourceFactory) {
        this.sourceFactory = sourceFactory
    }

    @Override
    void apply(final Project target) {
        target.pluginManager.apply(JavaPlugin.class)
        target.extensions.create(
                SolidityExtension.NAME,
                SolidityExtension,
                target
        )

        final SourceSetContainer sourceSets = target.convention
                .getPlugin(JavaPluginConvention.class).sourceSets

        sourceSets.all { SourceSet sourceSet ->
            configureSourceSet(target, sourceSet)
        }

        target.afterEvaluate {
            sourceSets.all { SourceSet sourceSet ->
                configureTask(target, sourceSet)
            }
        }
    }

    /**
     * Add default source set for Solidity.
     */
    private void configureSourceSet(final Project project, final SourceSet sourceSet) {

        def srcSetName = capitalize((CharSequence) sourceSet.name)
        def soliditySourceSet = new DefaultSoliditySourceSet(srcSetName, sourceFactory)

        sourceSet.convention.plugins.put(NAME, soliditySourceSet)

        def defaultSrcDir = new File(project.projectDir, "src/$sourceSet.name/$NAME")
        def defaultOutputDir = new File(project.buildDir, "resources/$sourceSet.name/$NAME")

        soliditySourceSet.solidity.srcDir(defaultSrcDir)
        soliditySourceSet.solidity.outputDir = defaultOutputDir

        sourceSet.allJava.source(soliditySourceSet.solidity)
        sourceSet.allSource.source(soliditySourceSet.solidity)
    }

    /**
     * Configures code compilation tasks for the Solidity source sets defined in the project
     * (e.g. main, test).
     * <p>
     * By default the generated task name for the <code>main</code> source set
     * is <code>compileSolidity</code> and for <code>test</code>
     * <code>compileTestSolidity</code>.
     */
    private static void configureTask(final Project project, final SourceSet sourceSet) {

//        def srcSetName = sourceSet.name == 'main' ?
//                '' : capitalize((CharSequence) sourceSet.name)
        // here is the new task brought by this plugin: compileXxxSolidity
        def PLUGIN = "solc" + capitalize((CharSequence) sourceSet.name)
        println("task: $PLUGIN")
        def compileTask = project.getTasks().create(PLUGIN, SolidityCompile)

        def soliditySourceSet = sourceSet.convention.plugins[NAME] as SoliditySourceSet

        // Set the sources for the generation task
        compileTask.setSource(soliditySourceSet.solidity)
        compileTask.outputs.dir(soliditySourceSet.solidity.outputDir)
        compileTask.description = "Compiles Solidity contracts " +
                "for $sourceSet.name source set."

//        // bran: project.solidity? how is solidity attached to the project?
        compileTask.outputComponents = project.solidity.outputComponents
        compileTask.overwrite = project.solidity.overwrite
        compileTask.optimize = project.solidity.optimize
        compileTask.optimizeRuns = project.solidity.optimizeRuns
        compileTask.prettyJson = project.solidity.prettyJson
        compileTask.srcMaps=project.solidity.srcMaps;
        compileTask.generatedFilesBaseDir =project.solidity.generatedFilesBaseDir;
        compileTask.generatedJavaPackageName =project.solidity.generatedJavaPackageName;
        compileTask.excludedContracts =project.solidity.excludedContracts;
        compileTask.useNativeJavaTypes =project.solidity.useNativeJavaTypes;


        project.getTasks().getByName('build') dependsOn(compileTask)
    }

}
