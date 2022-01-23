package com.utopia.router_processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.utopia.router_annotations.Destination;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
  private static final String TAG = "RouterProcessor";

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    log("process() called with: annotations = [" + annotations + "], roundEnv = [" + roundEnv + "]");
    if (roundEnv.processingOver()) {
      log("处理流程已完成");
      return false;
    }
    Set<String> annotationSet = annotations.stream().map(it -> it.getQualifiedName().toString()).collect(Collectors.toSet());
    String annotationFullName = Destination.class.getCanonicalName();
    if (!annotationSet.contains(annotationFullName)) {
      log("注解列表中没有" + annotationFullName);
      return false;
    }

    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Destination.class);
    if (elements.isEmpty()) {
      log("没有被" + annotationFullName + "注解的类");
      return false;
    }

    String rootProjectDir = processingEnv.getOptions().get("root_project_dir");
    log("rootProjectDir=" + rootProjectDir);

    log(">>>> APT开始处理");

    String pkg = "com.utopia.mapping";
    String clzName = "RouterMapping_" + System.currentTimeMillis();
    String fullName = pkg + "." + clzName;

    StringBuilder builder = new StringBuilder();
    builder.append("package ").append(pkg).append(";\n ")
        .append("\n")
        .append("import java.util.HashMap;\n")
        .append("import java.util.Map;\n")
        .append("\n")
        .append("public class ").append(clzName).append(" {\n")
        .append("  public static Map<String, String> getMapping() {\n")
        .append("    Map<String, String> mapping = new HashMap<>();\n");

    JsonArray jsonArray = new JsonArray();

    for (Element elem : elements) {
      if (!(elem instanceof TypeElement)) {
        error(elem.getKind().name() + " element is not TypeElement");
        continue;
      }

      TypeElement typeElement = (TypeElement) elem;
      Destination annotation = typeElement.getAnnotation(Destination.class);
      if (annotation == null) {
        error("annotation " + Destination.class.getName() + " got null");
        continue;
      }

      // 获取注解信息
      String url = annotation.url();
      String description = annotation.description();
      String realPath = typeElement.getQualifiedName().toString();

      String line = "    mapping.put(\"" + url + "\", \"" + realPath + "\");\n";
      builder.append(line);

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("url", url);
      jsonObject.addProperty("description", description);
      jsonObject.addProperty("realPath", realPath);
      jsonArray.add(jsonObject);
    }
    builder.append("    return mapping;\n")
        .append("  }\n")
        .append("}");


    try (OutputStream outputStream = processingEnv.getFiler().createSourceFile(fullName).openOutputStream();
         Writer writer = new OutputStreamWriter(outputStream)) {
      writer.write(builder.toString());
      log("写入java文件");
    } catch (IOException e) {
      e.printStackTrace();
    }

    File rootDir = new File(rootProjectDir);
    if (!rootDir.exists()) {
      throw new RuntimeException("rootDir不存在");
    }
    File routerFileDir = new File(rootDir, "router_mapping");
    if (!routerFileDir.exists()) {
      routerFileDir.mkdir();
    }
    File mappingFile = new File(routerFileDir, "mapping_" + System.currentTimeMillis() + ".json");
    try (
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mappingFile)))
    ) {
      Gson gson = new GsonBuilder()
          .setPrettyPrinting()
          .create();
      writer.write(gson.toJson(jsonArray));
      log("写入json文件");
    } catch (IOException e) {
      e.printStackTrace();
    }

    log(">>>> 处理结束");

    return true;
  }

  private void log(String msg) {
    System.out.println(TAG + " : " + msg);
  }

  private void error(String msg) {
    System.err.println(TAG + " : " + msg);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(Destination.class.getCanonicalName());
  }
}