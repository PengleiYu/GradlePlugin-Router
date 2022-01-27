package com.utopia.module_biz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.utopia.router_annotations.Destination

@Destination(
  url = "router://biz-home",
  description = "商业主页"
)
class BizHomeActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_biz_home)
    title = javaClass.simpleName

    val tvDisplay = findViewById<TextView>(R.id.tv_display)

    val orderId = intent.getStringExtra("orderId")
    val orderType = intent.getStringExtra("orderType")

    tvDisplay.text = "orderId=$orderId, orderType=$orderType"
  }
}