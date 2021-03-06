package org.xbib.gradle.plugin
import org.xbib.gradle.task.FregeDoc
import org.xbib.gradle.task.FregeNativeGen
import org.xbib.gradle.task.FregeQuickCheck
import org.xbib.gradle.task.FregeRepl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

class FregePlugin implements Plugin<Project> {

    Project project

    void apply(Project project) {
        this.project = project
        project.plugins.apply(FregeBasePlugin)
        project.plugins.apply("java")
        def replTask = project.task('fregeRepl', type: FregeRepl, group: 'frege', dependsOn: 'compileFrege')
        replTask.outputs.upToDateWhen { false }
        def checkTask = project.task('fregeQuickCheck', type: FregeQuickCheck, group: 'frege', dependsOn: 'testClasses')
        checkTask.outputs.upToDateWhen { false }
        project.tasks.test.dependsOn("fregeQuickCheck")
        configureFregeDoc()
        project.task('fregeNativeGen', type: FregeNativeGen, group: 'frege')
    }

    def configureFregeDoc() {
        FregeDoc fregeDoc = project.tasks.create('fregeDoc', FregeDoc)
        fregeDoc.group = 'frege'
        fregeDoc.dependsOn "compileFrege"
        SourceSet mainSourceSet = project.sourceSets.main
        fregeDoc.module = mainSourceSet.output.classesDir.absolutePath
        fregeDoc.classpath = mainSourceSet.runtimeClasspath
    }

}
