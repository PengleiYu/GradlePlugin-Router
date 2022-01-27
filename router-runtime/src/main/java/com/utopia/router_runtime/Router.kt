package com.utopia.router_runtime

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object Router {
  private const val TAG = "Router"
  private const val GENERATED_MAPPING = "com.utopia.mapping.generated.RouterMapping"

  private val mapping = mutableMapOf<String, String>()

  fun init() {
    Log.d(TAG, "init() called")
    try {
      val clz = Class.forName(GENERATED_MAPPING)
      val method = clz.getMethod("getMapping")
      val allMappings = method.invoke(null) as Map<String, String>
//      allMappings.onEach {
//        Log.d(TAG, "init: mapping = ${it.key} -> ${it.value}")
//      }
      mapping.putAll(allMappings)
    } catch (e: Exception) {
      Log.e(TAG, "init: fail", e)
    }
  }

  fun go(context: Context, url: String) {
    // 找到匹配页面
    val sourceUri = Uri.parse(url)
    val matchedUrl = mapping.keys.find {
      val targetUri = Uri.parse(it)
      return@find sourceUri.scheme.equals(targetUri.scheme)
          && sourceUri.authority.equals(targetUri.authority)
          && sourceUri.path.equals(targetUri.path)
    }
    val targetPage = mapping[matchedUrl]
    if (targetPage.isNullOrEmpty()) {
      return
    }
    // 解析url参数，封装到bundle
    val intent = Intent()
    sourceUri.queryParameterNames.onEach {
      intent.putExtra(it, sourceUri.getQueryParameter(it))
    }

    // 打开页面
    intent.setClassName(context.packageName, targetPage)
    if (context is Application) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }
}