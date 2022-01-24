package com.utopia.module_biz

import androidx.appcompat.app.AppCompatActivity
import com.utopia.router_annotations.Destination

@Destination(
  url = "router://biz-home",
  description = "商业主页"
)
class BizHomeActivity : AppCompatActivity() {
}