package org.xbib.gradle;

import org.gradle.api.file.SourceDirectorySet
import org.gradle.util.ConfigureUtil

class DefaultFregeSourceSet implements FregeSourceSet {
    private final SourceDirectorySet frege
    private final SourceDirectorySet allFrege

    DefaultFregeSourceSet(String displayName, FregeSourceSetDirectoryFactory sourceSetFactory) {
        this.frege = sourceSetFactory.newSourceSetDirectory(String.format("%s Frege source", displayName))
        this.frege.getFilter().include("**/*.fr")
        this.allFrege = sourceSetFactory.newSourceSetDirectory(String.format("%s Frege source", displayName))
        this.allFrege.source(this.frege)
        this.allFrege.getFilter().include("**/*.fr")
    }

    @Override
    SourceDirectorySet getFrege() {
        this.frege
    }

    FregeSourceSet frege(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, this.getFrege())
        this
    }

    @Override
    public SourceDirectorySet getAllFrege() {
        this.allFrege
    }
}
