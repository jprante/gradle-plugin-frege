package org.xbib.gradle.plugin

import org.xbib.gradle.DefaultFregeSourceSet
import org.xbib.gradle.FregeSourceSetDirectoryFactory
import org.xbib.gradle.task.FregeCompile
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject
import java.util.concurrent.Callable

class FregeBasePlugin implements Plugin<Project> {
    private final FileResolver fileResolver
    private static String EXTENSION_NAME = "frege"
    private FregePluginExtension fregePluginExtension
    private Project project;

    @Inject
    FregeBasePlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    @Override
    public void apply(final Project project) {
        // Workaround to build proper jars on Windows, see https://github.com/Frege/frege-gradle-plugin/issues/9
        this.project = project
        System.setProperty("file.encoding", "UTF-8")
        project.getPluginManager().apply(JavaBasePlugin)
        fregePluginExtension = project.getExtensions().create(EXTENSION_NAME, FregePluginExtension)
        JavaBasePlugin javaBasePlugin = project.getPlugins().getPlugin(JavaBasePlugin)
        configureSourceSetDefaults(javaBasePlugin)
    }


    private void configureSourceSetDefaults(final JavaBasePlugin javaBasePlugin) {
        project.getConvention().getPlugin(JavaPluginConvention).getSourceSets().all(new Action<SourceSet>() {
            public void execute(final SourceSet sourceSet) {
                FregeSourceSetDirectoryFactory factory = new FregeSourceSetDirectoryFactory((ProjectInternal) project, fileResolver)
                final DefaultFregeSourceSet fregeSourceSet = new DefaultFregeSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), factory)
                new DslObject(sourceSet).getConvention().getPlugins().put("frege", fregeSourceSet)
                final String defaultSourcePath = String.format("src/%s/frege", sourceSet.getName())
                fregeSourceSet.getFrege().srcDir(defaultSourcePath)
                sourceSet.getResources().getFilter().exclude(new Spec<FileTreeElement>() {
                    public boolean isSatisfiedBy(FileTreeElement element) {
                        return fregeSourceSet.getFrege().contains(element.getFile())
                    }
                })
                sourceSet.getAllJava().source(fregeSourceSet.getFrege())
                sourceSet.getAllSource().source(fregeSourceSet.getFrege())
                String compileTaskName = sourceSet.getCompileTaskName("frege")
                FregeCompile compile = project.getTasks().create(compileTaskName, FregeCompile.class)
                compile.setModule(project.file(defaultSourcePath).getAbsolutePath())
                javaBasePlugin.configureForSourceSet(sourceSet, compile)
                compile.getConventionMapping().map("fregepath", new Callable() {
                    public Object call() throws Exception {
                        return sourceSet.getCompileClasspath()
                    }
                })
                compile.dependsOn(sourceSet.getCompileJavaTaskName())
                compile.setDescription(String.format("Compiles the %s Frege source.", sourceSet.getName()))
                compile.setSource(fregeSourceSet.getFrege())
                project.getTasks().getByName(sourceSet.getClassesTaskName()).dependsOn(compileTaskName)
                sourceSet.compiledBy(compile)
            }
        })
    }
}
