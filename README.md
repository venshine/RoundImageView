# RoundImageView   

[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)

圆角图片控件，支持圆形边框和椭圆边框

ScreenShot
--
![](https://github.com/venshine/RoundImageView/blob/master/screenshot/screenshot.png)

Usage
--
##### Gradle:
```groovy
compile 'com.wx.ovalimageview:roundimageview:1.1.0'
```

##### Maven:
```xml
<dependency>
  <groupId>com.wx.ovalimageview</groupId>
  <artifactId>roundimageview</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```

##### 导入Eclipse(只能使用Java代码设置参数):
[下载jar包](https://github.com/venshine/RoundImageView/blob/master/roundimageview/roundimageview.jar)


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
        xmlns:app="http://schemas.android.com/apk/res/com.wx.roundimageview.demo"
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

##### Attributes:
| attr 属性          | description 描述 |
|:---				 |:---|
| border_color  	     | 边框颜色 |
| border_width  	     |边框宽度 |
| circle	 	 | 是否圆形展示 |


##### Methods:
| method 方法          | description 描述 |
|:---				 |:---|
| void setBorderColor(String borderColor)  	     | 设置边框颜色，形如'#aarrggbb' |
| void setBorderColor(int borderColor) 	     |设置边框颜色，形如{@link android.graphics.Color} |
| int getBorderColor()	 	 | 获取边框颜色 |
| void setBorderWidth(int borderWidth)  	     | 设置边框宽度 |
| int getBorderWidth() 	     |获取边框宽度 |
| void setCircle(boolean isCircle)	 	 | 设置圆形处理方式，默认按椭圆处理 |
| boolean isCircle()  	     |是否设置圆形处理 |
| void setImageResource(int resId, int borderColor, int borderWidth, boolean isCircle)	 	 | 设置图片资源，包括边框颜色、边框宽度、是否圆形处理 |
| void setImageDrawable(Drawable drawable, int borderColor, int borderWidth, boolean isCircle) 	     |设置图片Drawable，包括边框颜色、边框宽度、是否圆形处理 |
| void setImageBitmap(Bitmap bm, int borderColor, int borderWidth, boolean isCircle)	 	 | 设置图片bitmap，包括边框颜色、边框宽度、是否圆形处理 |


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

