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

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.wx.ovalimageview.RoundImageView

/**
 * 形状与边框：圆形 / 椭圆切换，边框宽度、颜色实时调整，并生成对应的 Kotlin 代码
 *
 * 状态只有 [circle]、[widthDp]、[colorIndex] 三个，[render] 统一推送到图片、控件和代码预览。
 *
 * @author venshine
 */
class ShapeActivity : BaseActivity() {

    private lateinit var image: RoundImageView
    private lateinit var swatches: List<View>

    private var circle = false
    private var widthDp = 6
    private var colorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shape)
        setToolbar(R.string.demo_shape, "isCircle")

        image = findViewById(R.id.shape_image)
        findViewById<View>(R.id.shape_circle).setOnClickListener { circle = true; render() }
        findViewById<View>(R.id.shape_oval).setOnClickListener { circle = false; render() }

        findViewById<SeekBar>(R.id.border_width_seek).apply {
            progress = widthDp
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    widthDp = progress
                    render()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            })
        }

        swatches = SWATCH_IDS.map { findViewById<View>(it) }
        swatches.forEachIndexed { index, view ->
            view.setOnClickListener { colorIndex = index; render() }
        }
        render()
    }

    private fun render() {
        val color = BORDER_COLORS[colorIndex]
        image.isCircle = circle
        image.borderWidth = dp(widthDp.toFloat())
        image.borderColor = color

        findViewById<View>(R.id.shape_circle).isSelected = circle
        findViewById<View>(R.id.shape_oval).isSelected = !circle
        findViewById<TextView>(R.id.border_width_text).text = "$widthDp dp"
        swatches.forEachIndexed { index, view ->
            view.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(BORDER_COLORS[index])
                // 选中的色块外圈描一道文字色，浅深色下都清晰
                if (index == colorIndex) setStroke(dp(3f), colorOf(R.color.text))
            }
        }
        val code = "image.isCircle = $circle\nimage.borderWidth = ${widthDp}.dp\nimage.borderColor = 0xFF${hexOf(color).drop(1)}.toInt()"
        findViewById<TextView>(R.id.code_text).text =
            CodeHighlighter.highlight(this, code, listOf("true", "false"))
    }

    private companion object {
        val SWATCH_IDS = intArrayOf(R.id.color_0, R.id.color_1, R.id.color_2, R.id.color_3, R.id.color_4, R.id.color_5)
    }
}
