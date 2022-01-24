package com.utopia.router.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

class RouterMappingTransform extends Transform {
    @Override
    String getName() {
        return "RouterMappingTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println "transformInvocation = $transformInvocation"
        // 1, 遍历输入
        // 2, 对输入进行处理
        // 3, 将输入拷贝到目标目录

        transformInvocation.inputs.each {
            it.directoryInputs.each {
                def destDir = transformInvocation.outputProvider
                        .getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, destDir)
            }
            it.jarInputs.each {
                def destJar = transformInvocation.outputProvider
                        .getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.file, destJar)
            }
        }
    }
}