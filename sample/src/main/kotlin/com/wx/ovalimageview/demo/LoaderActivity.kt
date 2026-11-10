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
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.wx.ovalimageview.RoundImageView

/**
 * 图片加载：模拟 Glide / Coil 先放占位图、再用 TransitionDrawable 渐变设图，
 * 与普通 ImageView 并排对比。每次点击换下一张图，可重复触发。
 *
 * @author venshine
 */
class LoaderActivity : BaseActivity() {

    private lateinit var plain: ImageView
    private lateinit var round: RoundImageView
    private lateinit var status: TextView
    private lateinit var button: TextView

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)
        setToolbar(R.string.demo_loader, "setImageDrawable()")

        plain = findViewById(R.id.image_plain)
        round = findViewById(R.id.image_loader)
        status = findViewById(R.id.load_status)
        button = findViewById(R.id.load_button)
        listOf(plain, round).forEach { it.setImageResource(IMAGES[0]) }
        status.setText(R.string.loader_idle)
        button.setOnClickListener { load() }
    }

    /** 1) 占位图 400ms 2) TransitionDrawable 渐变 600ms 3) 完成，按钮恢复 */
    private fun load() {
        button.isEnabled = false
        button.alpha = 0.5f
        index = (index + 1) % IMAGES.size
        listOf(plain, round).forEach { it.setImageDrawable(placeholder()) }
        showStatus(R.string.loader_placeholder)
        button.postDelayed({
            listOf(plain, round).forEach { view ->
                val fade = TransitionDrawable(arrayOf(placeholder(), ContextCompat.getDrawable(this, IMAGES[index])!!))
                view.setImageDrawable(fade)
                fade.startTransition(FADE_MS)
            }
            showStatus(R.string.loader_fading)
            button.postDelayed({
                showStatus(R.string.loader_done)
                button.isEnabled = true
                button.alpha = 1f
            }, FADE_MS + 50L)
        }, PLACEHOLDER_MS)
    }

    private fun placeholder() = ColorDrawable(colorOf(R.color.line))

    private fun showStatus(@StringRes text: Int) = status.setText(text)

    private companion object {
        const val PLACEHOLDER_MS = 400L
        const val FADE_MS = 600

        @DrawableRes
        val IMAGES = intArrayOf(R.drawable.demo_landscape, R.drawable.demo_portrait, R.drawable.demo_avatar)
    }
}
