package com.utopia.router.gradle


import java.util.jar.JarFile

class RouterMappingCollector {
    private static final String PACKAGE_NAME = "com/utopia/mapping"
    private static final String CLASS_NAME_PREFIX = "RouterMapping_"
    private static final String CLASS_FILE_SUFFIX = ".class"

    private final Set<String> mappingClzNames = new HashSet<>()

    Set<String> getMappingClzNames() {
        return mappingClzNames
    }

    void collectFile(File classFile) {
        if (classFile == null || !classFile.exists()) return


        if (classFile.isFile()) {
            def fileName = classFile.getName()
            if (classFile.absolutePath.contains(PACKAGE_NAME)
                    && fileName.startsWith(CLASS_NAME_PREFIX)
                    && fileName.endsWith(CLASS_FILE_SUFFIX)) {
                def clzName = fileName.replace(CLASS_FILE_SUFFIX, "")
                mappingClzNames.add(clzName)
            }
            return
        }

        def files = classFile.listFiles()
        files.each(this::collectFile)
    }

    void collectFromJar(File jarFile) {
        def jar = new JarFile(jarFile)
        def entries = jar.entries()
        while (entries.hasMoreElements()) {
            def element = entries.nextElement()
            def eleName = element.name
            // 格式校验可能不够准确
            if (eleName.contains(PACKAGE_NAME)
                    && eleName.contains(CLASS_NAME_PREFIX)
                    && eleName.endsWith(CLASS_FILE_SUFFIX)
            ) {
                println "jar: name=$element.name, realName=$element.realName "
                def clzName = eleName.replace(PACKAGE_NAME, "")
                        .replace("/", "")
                        .replace(CLASS_FILE_SUFFIX, "")
                mappingClzNames.add(clzName)
            }
        }
    }
}