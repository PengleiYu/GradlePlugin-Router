package com.utopia.router.gradle

import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println "RouterPlugin: apply from ${project.name}"

        def rootProjectDir = project.rootProject.projectDir
        // 1. 自动帮用户传递路径参数到注解处理器中
        def kapt = project.extensions.findByName("kapt")
        if (kapt != null) {
            kapt.arguments {
                arg("root_project_dir", rootProjectDir.absolutePath)
            }
        }
        // 2. 实现旧的构建产物的自动清理
        project.clean.doFirst {
            def routerMappingDir = new File(rootProjectDir, "router_mapping")
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }

        project.extensions.create("router", RouterExtension)

        project.afterEvaluate {
            println "it.type=${it.class}"
            stepMarkdown(it)
        }
    }

    private static void stepMarkdown(Project project) {
        def rootProjectDir = project.rootProject.projectDir
        RouterExtension extension = project["router"]
        println "用户设置的wikiDir:${extension.wikiDir}"

        // 3. javac任务后，汇总生成文档
        project.tasks.findAll {
            it.name.startsWith("compile") && it.name.endsWith("JavaWithJavac")
        }.each {
            println "filtered task: $it.name"
            it.doLast {
                File routerMappingDir = new File(rootProjectDir, "router_mapping")
                if (!routerMappingDir.exists()) {
                    return
                }
                def childFiles = routerMappingDir.listFiles()
                if (childFiles.length < 1) {
                    return
                }

                StringBuilder markdownBuilder = new StringBuilder()
                markdownBuilder.append("# 页面文档\n\n")

                childFiles.each {
                    if (it.name.endsWith(".json")) {
                        JsonSlurper jsonSlurper = new JsonSlurper()
                        def content = jsonSlurper.parse(it)
                        content.each {
                            def url = it['url']
                            def description = it['description']
                            def realPath = it['realPath']
                            markdownBuilder.append("## $description\n")
                                    .append("- url:$url\n")
                                    .append("- realPath:$realPath\n")
                        }
                    }
                }

                def wikiDir = new File(extension.wikiDir)
                if (!wikiDir.exists()) {
                    wikiDir.mkdir()
                }
                def wikiFile = new File(wikiDir, "页面文档.md")
                if (wikiFile.exists()) {
                    wikiFile.delete()
                }

                println "生成页文档"
                wikiFile.write(markdownBuilder.toString())
            }
        }
    }
}