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

import android.graphics.drawable.TransitionDrawable
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import com.wx.ovalimageview.RoundImageView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * 示例页冒烟测试：浅色、深色下都能启动，XML 属性和加载按钮生效
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class DemoSmokeTest {

    @After
    fun tearDown() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun launchesInLightAndNightMode() {
        listOf(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES).forEach { mode ->
            AppCompatDelegate.setDefaultNightMode(mode)
            val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
            assertTrue(activity.findViewById<RoundImageView>(R.id.image_circle).isCircle)
            activity.finish()
        }
    }

    @Test
    fun xmlAttributesAndScaleTypesAreApplied() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val circle = activity.findViewById<RoundImageView>(R.id.image_circle)
        assertTrue(circle.borderWidth > 0)
        assertTrue(!activity.findViewById<RoundImageView>(R.id.image_oval).isCircle)
        assertEquals(ImageView.ScaleType.CENTER_CROP, circle.scaleType)
        assertEquals(ImageView.ScaleType.FIT_CENTER, activity.findViewById<RoundImageView>(R.id.image_fit_center).scaleType)
        assertEquals(ImageView.ScaleType.CENTER_INSIDE, activity.findViewById<RoundImageView>(R.id.image_center_inside).scaleType)
    }

    @Test
    fun loadButtonSetsTransitionDrawable() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val image = activity.findViewById<RoundImageView>(R.id.image_loader)
        activity.findViewById<Button>(R.id.button_load).performClick()
        assertTrue(image.drawable is TransitionDrawable)
    }
}
