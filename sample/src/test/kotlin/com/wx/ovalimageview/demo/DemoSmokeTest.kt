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

import android.app.Activity
import android.content.ClipboardManager
import android.graphics.drawable.TransitionDrawable
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.wx.ovalimageview.RoundImageView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import java.time.Duration

/**
 * 示例页冒烟测试：每个页面在浅色、深色下都能启动，关键交互的结果符合预期
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class DemoSmokeTest {

    private val controllers = mutableListOf<ActivityController<*>>()

    @After
    fun tearDown() {
        // 销毁启动过的 Activity，停掉首页的无限循环动画
        controllers.forEach { it.destroy() }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    /** 往前推进主线程时间，让动画、延时任务执行 */
    private fun idle(millis: Long = 1000) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(millis))
    }

    private fun <T : Activity> launch(type: Class<T>): T {
        val controller = Robolectric.buildActivity(type)
        controllers += controller
        val activity = controller.setup().get()
        idle()
        return activity
    }

    private val screens = listOf(
        MainActivity::class.java, ShapeActivity::class.java, ScaleActivity::class.java, LoaderActivity::class.java,
    )

    @Test
    fun allScreensLaunchInLightMode() {
        screens.forEach { type ->
            assertFalse("${type.simpleName} 应为浅色", DemoTheme.isNight(launch(type)))
        }
    }

    @Test
    fun allScreensLaunchInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        screens.forEach { type ->
            assertTrue("${type.simpleName} 应为深色", DemoTheme.isNight(launch(type)))
        }
    }

    @Test
    fun themeToggleSwitchesToDark() {
        val activity = launch(MainActivity::class.java)
        activity.findViewById<View>(R.id.theme_toggle).performClick()
        idle()
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    fun homeShowsVersionAndCyclesBorderColor() {
        val activity = launch(MainActivity::class.java)
        assertEquals("v2.0.0", activity.findViewById<TextView>(R.id.version_chip).text.toString())
        val avatar = activity.findViewById<RoundImageView>(R.id.preview_avatar)
        val label = activity.findViewById<TextView>(R.id.preview_color_text)
        val before = avatar.borderColor
        idle(2500)
        assertNotEquals(before, avatar.borderColor)
        assertEquals(hexOf(avatar.borderColor), label.text.toString())
    }

    @Test
    fun homeCopiesDependency() {
        val activity = launch(MainActivity::class.java)
        activity.findViewById<View>(R.id.copy_dependency).performClick()
        val clip = activity.getSystemService(ClipboardManager::class.java).primaryClip!!
        assertEquals("implementation 'com.github.venshine:RoundImageView:2.0.0'", clip.getItemAt(0).text.toString())
    }

    @Test
    fun homeCardsOpenDemoPages() {
        val activity = launch(MainActivity::class.java)
        val expected = mapOf(
            R.id.card_shape to ShapeActivity::class.java,
            R.id.card_scale to ScaleActivity::class.java,
            R.id.card_loader to LoaderActivity::class.java,
        )
        expected.forEach { (id, target) ->
            activity.findViewById<View>(id).performClick()
            assertEquals(target.name, shadowOf(activity).nextStartedActivity.component!!.className)
        }
    }

    @Test
    fun shapeControlsDriveTheImage() {
        val activity = launch(ShapeActivity::class.java)
        val image = activity.findViewById<RoundImageView>(R.id.shape_image)
        val density = activity.resources.displayMetrics.density

        activity.findViewById<View>(R.id.shape_circle).performClick()
        assertTrue(image.isCircle)
        assertTrue(activity.findViewById<TextView>(R.id.code_text).text.contains("isCircle = true"))
        activity.findViewById<View>(R.id.shape_oval).performClick()
        assertFalse(image.isCircle)

        activity.findViewById<SeekBar>(R.id.border_width_seek).progress = 12
        assertEquals((12 * density + 0.5f).toInt(), image.borderWidth)
        assertEquals("12 dp", activity.findViewById<TextView>(R.id.border_width_text).text.toString())

        activity.findViewById<View>(R.id.color_3).performClick()
        assertEquals(BORDER_COLORS[3], image.borderColor)
        assertTrue(activity.findViewById<TextView>(R.id.code_text).text.contains(hexOf(BORDER_COLORS[3]).drop(1)))
    }

    @Test
    fun scaleSwitchesImageAndShape() {
        val activity = launch(ScaleActivity::class.java)
        val images = listOf(R.id.image_center_crop, R.id.image_fit_center, R.id.image_center_inside)
            .map { activity.findViewById<RoundImageView>(it) }
        assertEquals(
            listOf(ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.CENTER_INSIDE),
            images.map { it.scaleType },
        )
        assertTrue(images.all { it.isCircle })

        activity.findViewById<SwitchCompat>(R.id.circle_switch).performClick()
        assertTrue(images.none { it.isCircle })

        val portrait = images[0].drawable
        activity.findViewById<View>(R.id.source_landscape).performClick()
        assertNotEquals(portrait.constantState, images[0].drawable.constantState)
        assertTrue(activity.findViewById<View>(R.id.source_landscape).isSelected)
    }

    @Test
    fun loaderFadesInAndCanRepeat() {
        val activity = launch(LoaderActivity::class.java)
        val round = activity.findViewById<RoundImageView>(R.id.image_loader)
        val plain = activity.findViewById<ImageView>(R.id.image_plain)
        val button = activity.findViewById<View>(R.id.load_button)
        val status = activity.findViewById<TextView>(R.id.load_status)

        repeat(2) {
            button.performClick()
            assertFalse("加载中按钮应禁用", button.isEnabled)
            idle(500)
            assertTrue(round.drawable is TransitionDrawable)
            assertTrue(plain.drawable is TransitionDrawable)
            assertEquals(activity.getString(R.string.loader_fading), status.text.toString())
            idle(1000)
            assertTrue(button.isEnabled)
            assertEquals(activity.getString(R.string.loader_done), status.text.toString())
        }
    }
}
