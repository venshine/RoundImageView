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

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.wx.ovalimageview.RoundImageView

/**
 * scaleType 对比：同一张图用 centerCrop / fitCenter / centerInside 并排显示，可切换图片比例和圆形 / 椭圆
 *
 * 浅色底板是控件自身的范围（背景不会被裁剪），方便看出图片在控件里实际占多大。
 *
 * @author venshine
 */
class ScaleActivity : BaseActivity() {

    private lateinit var images: List<RoundImageView>

    private var portrait = true
    private var circle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scale)
        setToolbar(R.string.demo_scale, "scaleType")

        images = listOf(R.id.image_center_crop, R.id.image_fit_center, R.id.image_center_inside)
            .map { findViewById(it) }
        findViewById<View>(R.id.source_portrait).setOnClickListener { portrait = true; render() }
        findViewById<View>(R.id.source_landscape).setOnClickListener { portrait = false; render() }
        findViewById<SwitchCompat>(R.id.circle_switch).apply {
            isChecked = circle
            setOnCheckedChangeListener { _, checked -> circle = checked; render() }
        }
        render()
    }

    private fun render() {
        findViewById<View>(R.id.source_portrait).isSelected = portrait
        findViewById<View>(R.id.source_landscape).isSelected = !portrait
        images.forEach {
            it.setImageResource(if (portrait) R.drawable.demo_portrait else R.drawable.demo_landscape)
            it.isCircle = circle
        }
    }
}
