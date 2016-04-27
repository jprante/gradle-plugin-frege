package org.xbib.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.util.GradleVersion

class FregeSourceSetDirectoryFactory {
    private final FileResolver fileResolver
    private final ProjectInternal project

    FregeSourceSetDirectoryFactory(ProjectInternal project, FileResolver fileResolver) {
        this.fileResolver = fileResolver
        this.project = project
    }

    SourceDirectorySet newSourceSetDirectory(String displayName) {
        if (GradleVersion.current().compareTo(GradleVersion.version("2.12")) >= 0) {
            return project.getServices().get(SourceDirectorySetFactory).create(displayName)
        } else {
            return new DefaultSourceDirectorySet(displayName, fileResolver)
        }
    }
}
