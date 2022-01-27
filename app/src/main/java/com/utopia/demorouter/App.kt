package com.utopia.demorouter

import android.app.Application
import com.utopia.router_runtime.Router

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    Router.init()
  }
}