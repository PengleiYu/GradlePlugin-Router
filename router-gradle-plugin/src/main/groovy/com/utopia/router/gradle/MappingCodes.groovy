package com.utopia.router.gradle


import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MappingCodes implements Opcodes {


    public static final String CLZ_NAME = "com/utopia/mapping/generated/RouterMapping"

    static byte[] getBytes(Set<String> clzNames) throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, CLZ_NAME, null, "java/lang/Object", null);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "getMapping", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", null);
            methodVisitor.visitCode();
            methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            methodVisitor.visitVarInsn(ASTORE, 0);

            clzNames.each {
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/utopia/mapping/$it", "getMapping",
                        "()Ljava/util/Map;", false);
                methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "putAll", "(Ljava/util/Map;)V", true);
            }

            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
