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

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.wx.ovalimageview.RoundImageView

/**
 * 首页：头像边框颜色循环预览 + 示例入口 + 接入方式
 *
 * @author venshine
 */
class MainActivity : BaseActivity() {

    private val evaluator = ArgbEvaluator()
    private var tick: Runnable? = null

    /** 示例 App 与库同版本发布，版本号只在 sample/build.gradle 的 versionName 维护一处 */
    private val version: String by lazy {
        packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.source_button).setOnClickListener { openUrl(SOURCE_URL) }
        findViewById<TextView>(R.id.version_chip).text = "v$version"
        initPreview()
        initCards()
        initInstall()
    }

    override fun onDestroy() {
        tick?.let { findViewById<View>(R.id.preview_avatar).removeCallbacks(it) }
        super.onDestroy()
    }

    /** 边框颜色在调色板里循环渐变（每 [FRAME_MS] 按时间算一次），同时显示当前颜色值 */
    private fun initPreview() {
        val avatar = findViewById<RoundImageView>(R.id.preview_avatar)
        val label = findViewById<TextView>(R.id.preview_color_text)
        val start = SystemClock.uptimeMillis()
        val runnable = object : Runnable {
            override fun run() {
                val position = (SystemClock.uptimeMillis() - start) % CYCLE_MS / CYCLE_MS.toFloat() * BORDER_COLORS.size
                val from = position.toInt()
                val color = evaluator.evaluate(position - from, BORDER_COLORS[from], BORDER_COLORS[(from + 1) % BORDER_COLORS.size]) as Int
                avatar.borderColor = color
                label.text = hexOf(color)
                avatar.postDelayed(this, FRAME_MS)
            }
        }
        tick = runnable
        runnable.run()
    }

    private fun initCards() {
        // 第一张强调色大卡的内容已在布局里写好，只绑定点击
        findViewById<View>(R.id.card_shape).setOnClickListener { open(ShapeActivity::class.java) }
        bindCard(R.id.card_scale, R.string.demo_scale, R.string.demo_scale_desc, R.drawable.ic_scale,
            ScaleActivity::class.java)
        bindCard(R.id.card_loader, R.string.demo_loader, R.string.demo_loader_desc, R.drawable.ic_loader,
            LoaderActivity::class.java)
    }

    private fun bindCard(@IdRes id: Int, @StringRes title: Int, @StringRes desc: Int, @DrawableRes icon: Int,
                         target: Class<out Activity>) {
        val card = findViewById<View>(id)
        card.findViewById<ImageView>(R.id.card_icon).setImageResource(icon)
        card.findViewById<TextView>(R.id.card_title).setText(title)
        card.findViewById<TextView>(R.id.card_desc).setText(desc)
        card.setOnClickListener { open(target) }
    }

    private fun initInstall() {
        val dependency = "com.github.venshine:RoundImageView:$version"
        val code = "// settings.gradle\nmaven { url = 'https://jitpack.io' }\n\n// build.gradle\nimplementation '$dependency'"
        findViewById<TextView>(R.id.install_code).text =
            CodeHighlighter.highlight(this, code, listOf("implementation", "maven"))
        findViewById<TextView>(R.id.copy_dependency).apply {
            setStartIcon(tintedIcon(R.drawable.ic_copy, colorOf(R.color.text), 16f))
            setOnClickListener {
                copyToClipboard("implementation '$dependency'")
                flashCopied(R.string.home_copied_dependency, R.string.home_copy_dependency)
            }
        }
    }

    private fun open(target: Class<out Activity>) = startActivity(Intent(this, target))

    private companion object {
        const val SOURCE_URL = "https://github.com/venshine/RoundImageView"
        const val CYCLE_MS = 7000L
        const val FRAME_MS = 33L
    }
}
