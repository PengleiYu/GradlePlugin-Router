package com.utopia.router.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println "RouterPlugin: apply from ${project.name}"
    }
}