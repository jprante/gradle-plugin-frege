package org.xbib.gradle.task

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaExecSpec

class FregeQuickCheck extends DefaultTask {

    // more options to consider:
/*
     Looks up quick check predicates in the given modules and tests them.

    [Usage:] java -cp fregec.jar frege.tools.Quick [ option ... ] modulespec ...

    Options:

    -    -v      print a line for each pedicate that passed
    -    -n num  run _num_ tests per predicate, default is 100
    -    -p pred1,pred2,... only test the given predicates
    -    -x pred1,pred2,... do not test the given predicates
    -    -l  just print the names of the predicates available.

    Ways to specify modules:

    - module  the module name (e.g. my.great.Module), will be lookup up in
              the current class path.
    - dir/    A directory path. The directory is searched for class files,
              and for each class files an attempt is made to load it as if
              the given directory was in the class path. The directory must
              be the root of the classes contained therein, otherwise the
              classes won't get loaded.
    - path-to.jar A jar or zip file is searched for class files, and for each
              class file found an attempt is made to load it as if the
              jar was in the class path.

     The number of passed/failed tests is reported. If any test failed or other
     errors occured, the exit code will be non zero.

     The code will try to heat up your CPU by running tests on all available cores.
     This should be faster on multi-core computers than running the tests
     sequentially. It makes it feasable to run more tests per predicate.

     */

    Boolean verbose = true
    Boolean listAvailable = false
    Boolean help = false
    List<String> classpathDirectories = ["$project.buildDir/classes/main", "$project.buildDir/classes/test"]
    String moduleDir = "$project.buildDir/classes/test"

    @TaskAction
    void runQuickCheck() {
        def args = []
        File moduleDirFile = new File(moduleDir)
        if (!help) {
            if (verbose) {
                args << "-v"
            }
            if (listAvailable) {
                args << "-l"
            }
            if (moduleDirFile.exists()) {
                args = args + [moduleDir]
            }
        }
        if (moduleDirFile.exists()) {
            logger.info("Calling quickcheck with args = {}", args)
            project.javaexec(new Action<JavaExecSpec>() {
                @Override
                void execute(JavaExecSpec javaExecSpec) {
                    javaExecSpec.main = 'frege.tools.Quick'
                    javaExecSpec.args = args
                    javaExecSpec.classpath = project.files(project.configurations.compile)
                            .plus(project.files(project.configurations.testRuntime))
                            .plus(project.files(classpathDirectories.collect { s -> new File(s) }))
                    javaExecSpec.jvmArgs = []
                    javaExecSpec.errorOutput = System.err
                    javaExecSpec.standardOutput = System.out
                }
            })
        }
    }
}