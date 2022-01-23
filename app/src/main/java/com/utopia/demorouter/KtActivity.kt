package com.utopia.demorouter

import androidx.appcompat.app.AppCompatActivity
import com.utopia.router_annotations.Destination

@Destination(
  url = "content://ktActivity",
  description = "Kotlin Activity"
)
class KtActivity : AppCompatActivity()