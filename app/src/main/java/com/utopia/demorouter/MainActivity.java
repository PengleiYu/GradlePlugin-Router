package com.utopia.demorouter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.utopia.router_annotations.Destination;
import com.utopia.router_runtime.Router;

@Destination(
    url = "router://page-home",
    description = "个人主页"
)
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btn_go).setOnClickListener(v ->
        Router.INSTANCE.go(this, "router://biz-home?orderId=12345&orderType=book")
    );
  }
}