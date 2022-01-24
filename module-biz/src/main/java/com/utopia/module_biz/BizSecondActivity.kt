package com.utopia.module_biz

import androidx.appcompat.app.AppCompatActivity
import com.utopia.router_annotations.Destination

@Destination(
  url = "router://biz-secondPage",
  description = "商业次级页"
)
class BizSecondActivity : AppCompatActivity() {
}