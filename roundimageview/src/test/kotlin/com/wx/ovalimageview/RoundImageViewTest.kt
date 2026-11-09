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
package com.wx.ovalimageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.widget.ImageView
import org.robolectric.RuntimeEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * RoundImageView 测试：用真实绘制出的像素校验裁剪、边框、scaleType 和第三方加载库的设图方式
 *
 * @author venshine
 */
// 需要真实像素，使用原生图形模式
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
class RoundImageViewTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    private fun solid(w: Int, h: Int, color: Int): Bitmap =
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }

    /** 左半红、右半蓝 */
    private fun redBlue(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        canvas.drawColor(Color.BLUE)
        canvas.clipRect(0, 0, w / 2, h)
        canvas.drawColor(Color.RED)
        return bm
    }

    private fun newView(w: Int = 100, h: Int = 100): RoundImageView =
        RoundImageView(context).also { it.layoutAs(w, h) }

    private fun View.layoutAs(w: Int, h: Int) {
        measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
        layout(0, 0, w, h)
    }

    private fun render(view: View): Bitmap {
        view.layoutAs(view.width, view.height)
        val out = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(out))
        return out
    }

    @Test
    fun ovalClipsCornersButKeepsCenter() {
        val view = newView(200, 100)
        view.setImageBitmap(solid(200, 100, Color.RED))
        val out = render(view)
        assertEquals(Color.TRANSPARENT, out.getPixel(3, 3))
        assertEquals(Color.TRANSPARENT, out.getPixel(196, 96))
        assertEquals(Color.RED, out.getPixel(100, 50))
        assertEquals(Color.RED, out.getPixel(10, 50))
    }

    @Test
    fun circleUsesShorterSideAsDiameter() {
        val view = newView(200, 100)
        view.isCircle = true
        view.setImageBitmap(solid(200, 100, Color.RED))
        val out = render(view)
        // 圆心 (100,50)、半径 50：左右两端 x<50 或 x>150 都在圆外
        assertEquals(Color.TRANSPARENT, out.getPixel(20, 50))
        assertEquals(Color.TRANSPARENT, out.getPixel(180, 50))
        assertEquals(Color.RED, out.getPixel(100, 50))
        assertEquals(Color.RED, out.getPixel(70, 50))
    }

    @Test
    fun borderIsDrawnInsideTheShape() {
        val view = newView()
        view.isCircle = true
        view.borderWidth = 10
        view.borderColor = Color.BLUE
        view.setImageBitmap(solid(100, 100, Color.RED))
        val out = render(view)
        assertEquals(Color.BLUE, out.getPixel(50, 4))
        assertEquals(Color.BLUE, out.getPixel(4, 50))
        assertEquals(Color.RED, out.getPixel(50, 20))
        assertEquals(Color.RED, out.getPixel(50, 50))
    }

    @Test
    fun borderSettersTakeEffectOnTheCurrentImage() {
        val view = newView()
        view.setImageBitmap(solid(100, 100, Color.RED))
        assertEquals(Color.RED, render(view).getPixel(50, 4))
        view.setBorderColor("#ff0000ff")
        view.borderWidth = 10
        assertEquals(Color.BLUE, render(view).getPixel(50, 4))
        assertEquals(Color.BLUE, view.borderColor)
    }

    // 回归 #1：scaleType 必须生效，旧实现把图片按固定方式缩放
    @Test
    fun centerCropFillsTheViewAndCropsTheImage() {
        val view = newView()
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.setImageBitmap(redBlue(200, 100))
        val out = render(view)
        // 取中间 100x100：视图左半红、右半蓝，上下边缘也有图
        assertEquals(Color.RED, out.getPixel(30, 50))
        assertEquals(Color.BLUE, out.getPixel(70, 50))
        assertEquals(Color.RED, out.getPixel(30, 25))
        assertEquals(Color.BLUE, out.getPixel(70, 75))
    }

    @Test
    fun fitCenterKeepsTheWholeImageWithLetterbox() {
        val view = newView()
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        view.setImageBitmap(redBlue(200, 100))
        val out = render(view)
        // 缩放到 100x50，垂直居中 y 25..75
        assertEquals(Color.TRANSPARENT, out.getPixel(50, 15))
        assertEquals(Color.RED, out.getPixel(30, 50))
        assertEquals(Color.BLUE, out.getPixel(70, 50))
    }

    // 回归 #1：圆形模式下也要遵守 scaleType，旧实现固定把图片居中裁成正方形（等同 centerCrop）
    @Test
    fun circleModeHonorsFitCenter() {
        val fit = newView().apply { isCircle = true; scaleType = ImageView.ScaleType.FIT_CENTER }
        fit.setImageBitmap(redBlue(200, 100))
        val fitOut = render(fit)
        // 图片只占 y25..75，圆内 (50,20) 是留白
        assertEquals(Color.TRANSPARENT, fitOut.getPixel(50, 20))
        assertEquals(Color.RED, fitOut.getPixel(30, 50))

        val crop = newView().apply { isCircle = true; scaleType = ImageView.ScaleType.CENTER_CROP }
        crop.setImageBitmap(redBlue(200, 100))
        assertTrue(render(crop).getPixel(50, 20) != Color.TRANSPARENT)
    }

    // 没有显式设置 scaleType 时默认 centerCrop，保持 1.x 圆形模式铺满的效果
    @Test
    fun defaultScaleTypeIsCenterCrop() {
        val view = newView().apply { isCircle = true }
        assertEquals(ImageView.ScaleType.CENTER_CROP, view.scaleType)
        view.setImageBitmap(redBlue(200, 100))
        assertTrue(render(view).getPixel(50, 20) != Color.TRANSPARENT)
    }

    // XML 里显式写了 android:scaleType 时以 XML 为准
    @Test
    fun scaleTypeFromXmlWinsOverTheDefault() {
        val attrs = Robolectric.buildAttributeSet()
            .addAttribute(android.R.attr.scaleType, "fitCenter")
            .build()
        val view = RoundImageView(context, attrs)
        assertEquals(ImageView.ScaleType.FIT_CENTER, view.scaleType)
    }

    @Test
    fun centerInsideDoesNotUpscaleSmallImages() {
        val inside = newView().apply { scaleType = ImageView.ScaleType.CENTER_INSIDE }
        inside.setImageBitmap(solid(40, 20, Color.RED))
        // 40x20 居中 x30..70, y40..60
        assertEquals(Color.RED, render(inside).getPixel(35, 50))
        assertEquals(Color.TRANSPARENT, render(inside).getPixel(20, 50))

        val fit = newView().apply { scaleType = ImageView.ScaleType.FIT_CENTER }
        fit.setImageBitmap(solid(40, 20, Color.RED))
        assertEquals(Color.RED, render(fit).getPixel(20, 50))
    }

    // 回归 #2：Glide / Coil 通过 setImageDrawable 设图，旧实现强转 BitmapDrawable，TransitionDrawable 会崩溃
    @Test
    fun bitmapDrawableFromImageLoaderIsRounded() {
        val view = newView()
        val drawable = BitmapDrawable(context.resources, solid(100, 100, Color.RED))
        view.setImageDrawable(drawable)
        assertSame(drawable, view.drawable)
        val out = render(view)
        assertEquals(Color.TRANSPARENT, out.getPixel(3, 3))
        assertEquals(Color.RED, out.getPixel(50, 50))
    }

    @Test
    fun transitionDrawableFromImageLoaderIsRoundedAndBordered() {
        val view = newView()
        view.borderWidth = 10
        view.borderColor = Color.BLUE
        val loaded = BitmapDrawable(context.resources, solid(100, 100, Color.RED))
        val transition = TransitionDrawable(arrayOf(ColorDrawable(Color.GRAY), loaded))
        view.setImageDrawable(transition)
        transition.startTransition(100)
        assertSame(transition, view.drawable)
        // 渐变开始时显示占位色，形状和边框同样生效
        val start = render(view)
        assertEquals(Color.TRANSPARENT, start.getPixel(3, 3))
        assertEquals(Color.BLUE, start.getPixel(50, 4))
        assertEquals(Color.GRAY, start.getPixel(50, 50))
        // 渐变结束后显示加载好的图片
        ShadowSystemClock.advanceBy(Duration.ofMillis(300))
        val end = render(view)
        assertEquals(Color.TRANSPARENT, end.getPixel(3, 3))
        assertEquals(Color.RED, end.getPixel(50, 50))
    }

    @Test
    fun colorDrawableAndNullAreAccepted() {
        val view = newView()
        view.setImageDrawable(ColorDrawable(Color.GREEN))
        assertEquals(Color.GREEN, render(view).getPixel(50, 50))
        view.setImageDrawable(null)
        assertEquals(Color.TRANSPARENT, render(view).getPixel(50, 50))
    }

    @Test
    fun styleOverloadsApplyBorderAndShape() {
        val view = newView()
        view.setImageBitmap(solid(100, 100, Color.RED), Color.BLUE, 10, true)
        assertEquals(Color.BLUE, view.borderColor)
        assertEquals(10, view.borderWidth)
        assertTrue(view.isCircle)
        assertEquals(Color.BLUE, render(view).getPixel(50, 4))

        view.setImageDrawable(ColorDrawable(Color.GREEN), Color.RED, 4, false)
        assertEquals(Color.RED, view.borderColor)
        assertEquals(4, view.borderWidth)
        assertTrue(!view.isCircle)

        view.setImageResource(android.R.drawable.ic_menu_add, Color.YELLOW, 6, true)
        assertEquals(Color.YELLOW, view.borderColor)
        assertEquals(6, view.borderWidth)
        assertTrue(view.isCircle)
    }

    @Test
    fun xmlAttributesAreRead() {
        val attrs = Robolectric.buildAttributeSet()
            .addAttribute(R.attr.border_color, "#ff0000")
            .addAttribute(R.attr.border_width, "3px")
            .addAttribute(R.attr.circle, "true")
            .build()
        val view = RoundImageView(context, attrs)
        assertEquals(Color.RED, view.borderColor)
        assertEquals(3, view.borderWidth)
        assertTrue(view.isCircle)
    }

    @Test
    fun javaCallersGetAllConstructors() {
        val types = RoundImageView::class.java.constructors.filter { !it.isSynthetic }.map { it.parameterTypes.size }.toSet()
        assertEquals(setOf(1, 2, 3, 4), types)
    }
}
