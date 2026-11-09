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
package com.wx.ovalimageview.demo

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wx.ovalimageview.RoundImageView

/**
 * Demo：圆形 / 椭圆、scaleType、模拟图片加载库的渐变设置
 *
 * @author venshine
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loaderImage = findViewById<RoundImageView>(R.id.image_loader)
        findViewById<Button>(R.id.button_load).setOnClickListener {
            // Glide / Coil 的渐变效果最终都是 setImageDrawable(TransitionDrawable)
            val placeholder = ColorDrawable(ContextCompat.getColor(this, R.color.text_muted))
            val loaded = ContextCompat.getDrawable(this, R.drawable.demo_landscape)!!
            loaderImage.setImageDrawable(TransitionDrawable(arrayOf(placeholder, loaded)).apply {
                startTransition(400)
            })
        }
    }
}
