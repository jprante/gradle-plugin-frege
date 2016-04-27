package org.xbib.gradle;

import org.gradle.api.file.SourceDirectorySet

interface FregeSourceSet {
    SourceDirectorySet getFrege()
    SourceDirectorySet getAllFrege()
}
