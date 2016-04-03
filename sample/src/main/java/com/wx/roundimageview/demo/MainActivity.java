/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.roundimageview.demo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wx.roundimageview.RoundImageView;

/**
 * Demo
 *
 * @author venshine
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // imageview1 用xml实现
        RoundImageView imageView1 = (RoundImageView) findViewById(R.id.round_imageview1);
//        imageView1.setImageResource(R.mipmap.ic_launcher, Color.BLACK, 5, true);

        RoundImageView imageView2 = (RoundImageView) findViewById(R.id.round_imageview2);
        imageView2.setImageResource(R.mipmap.android, Color.RED, 10, true);

        RoundImageView imageView3 = (RoundImageView) findViewById(R.id.round_imageview3);
        imageView3.setImageResource(R.mipmap.image, Color.parseColor("#ff2a99ff"), 20, false);

        RoundImageView imageView4 = (RoundImageView) findViewById(R.id.round_imageview4);
        imageView4.setImageResource(R.mipmap.image, Color.parseColor("#ff2a99ff"), 20, true);

    }
}
