package com.utopia.router_processor;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.utopia.router_annotations.Destination;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
  private static final String TAG = "RouterProcessor";

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    log("process() called with: annotations = [" + annotations + "], roundEnv = [" + roundEnv + "]");
    if (roundEnv.processingOver()) {
      return false;
    }
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Destination.class);
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

      String url = annotation.url();
      String description = annotation.description();
      String realPath = typeElement.getQualifiedName().toString();

      log("url=" + url);
      log("description=" + description);
      log("realPath=" + realPath);

    }


    return false;
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