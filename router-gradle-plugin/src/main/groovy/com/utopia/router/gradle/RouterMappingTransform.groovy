package com.utopia.router.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class RouterMappingTransform extends Transform {
    private RouterMappingCollector mCollector = new RouterMappingCollector()

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

                mCollector.collectFile(it.file)
            }
            it.jarInputs.each {
                def destJar = transformInvocation.outputProvider
                        .getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.file, destJar)

                mCollector.collectFromJar(it.file)
            }
        }

        println "collector = ${mCollector.getMappingClzNames()}"
        // 准备字节码文件
        def mappingFile = transformInvocation.outputProvider.getContentLocation(
                "router_mapping",
                getOutputTypes(),
                getScopes(),
                Format.JAR)

        println "mappingFile = $mappingFile"
        if (!mappingFile.getParentFile().exists()) {
            mappingFile.getParentFile().mkdirs()
        }
        if (mappingFile.exists()) {
            mappingFile.delete()
        }
        // 写入字节码
        try (def jarOutputStream = new JarOutputStream(new FileOutputStream(mappingFile))) {
            def entry = new ZipEntry(MappingCodes.CLZ_NAME + ".class")
            jarOutputStream.putNextEntry(entry)
            jarOutputStream.write(MappingCodes.getBytes(mCollector.getMappingClzNames()))
            jarOutputStream.closeEntry()
        }
    }
}