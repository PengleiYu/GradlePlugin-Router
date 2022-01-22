package com.utopia.router_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Destination {
  /**
   * 当前页面URL，不能为空
   *
   * @return 页面URL
   */
  String url();

  /**
   * 当前页面的中文描述
   */
  String description();
}