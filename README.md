# RoundImageView
圆角图片控件，支持圆形边框和椭圆边框

ScreenShot
--
![](https://github.com/venshine/RoundImageView/blob/master/screenshot/screenshot.gif)

Demo
--
Use the RoundImageView as a View, Java and XML are both supported.

##### Java:
```Java
    public class MainActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main)

            RoundImageView imageView = (RoundImageView) findViewById(R.id.round_imageview);
            imageView.setImageResource(R.mipmap.android, Color.RED, 10, true);

        }
    }
```

##### XML:
```xml
    <com.wx.roundimageview.RoundImageView
        android:id="@+id/round_imageview1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        android:src="@mipmap/ic_launcher"
        app:border_color="#ff0000"
        app:border_width="2dp"
        app:circle="true"
        />
```

About
--
* Email：venshine.cn@gmail.com

License
--
    Copyright (C) 2016 venshine.cn@gmail.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

