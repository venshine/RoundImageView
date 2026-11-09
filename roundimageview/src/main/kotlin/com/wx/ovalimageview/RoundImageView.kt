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
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

/**
 * 圆角 ImageView：把图片裁成椭圆或圆形，可加边框。
 *
 * 裁剪发生在绘制阶段，图片本身交给系统 [ImageView] 处理，所以 `scaleType`、
 * 任意 [Drawable]（含 Glide / Coil 设置的 `TransitionDrawable`、矢量图）都可直接使用。
 *
 * @author venshine
 */
class RoundImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ImageView(context, attrs, defStyleAttr, defStyleRes) {

    /** 边框颜色，透明（alpha 为 0）时不画边框 */
    var borderColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    /** 边框宽度，单位 px；小于等于 0 时不画边框 */
    var borderWidth: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    /** 是否按圆形裁剪（取内容区较短边为直径）；false 时按内容区的椭圆裁剪 */
    var isCircle: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val clipPath = Path()
    private val shapeRect = RectF()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    init {
        // 默认 centerCrop：图片铺满裁剪形状，和 1.x 圆形模式的效果一致；
        // XML 或样式里显式写了 android:scaleType 时以显式设置为准
        val scaleTypeAttr = context.obtainStyledAttributes(
            attrs, intArrayOf(android.R.attr.scaleType), defStyleAttr, defStyleRes,
        )
        try {
            if (!scaleTypeAttr.hasValue(0)) scaleType = ScaleType.CENTER_CROP
        } finally {
            scaleTypeAttr.recycle()
        }
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, defStyleRes)
            try {
                borderColor = ta.getColor(R.styleable.RoundImageView_border_color, Color.TRANSPARENT)
                borderWidth = ta.getDimensionPixelSize(R.styleable.RoundImageView_border_width, 0)
                isCircle = ta.getBoolean(R.styleable.RoundImageView_circle, false)
            } finally {
                ta.recycle()
            }
        }
    }

    /** 设置边框颜色，形如 `#aarrggbb` */
    fun setBorderColor(borderColor: String) {
        this.borderColor = Color.parseColor(borderColor)
    }

    /** 设置图片资源，同时设置边框颜色、边框宽度、是否圆形 */
    fun setImageResource(resId: Int, borderColor: Int, borderWidth: Int, isCircle: Boolean) {
        applyStyle(borderColor, borderWidth, isCircle)
        setImageResource(resId)
    }

    /** 设置图片 Drawable，同时设置边框颜色、边框宽度、是否圆形 */
    fun setImageDrawable(drawable: Drawable?, borderColor: Int, borderWidth: Int, isCircle: Boolean) {
        applyStyle(borderColor, borderWidth, isCircle)
        setImageDrawable(drawable)
    }

    /** 设置图片 Bitmap，同时设置边框颜色、边框宽度、是否圆形 */
    fun setImageBitmap(bm: Bitmap?, borderColor: Int, borderWidth: Int, isCircle: Boolean) {
        applyStyle(borderColor, borderWidth, isCircle)
        setImageBitmap(bm)
    }

    private fun applyStyle(borderColor: Int, borderWidth: Int, isCircle: Boolean) {
        this.borderColor = borderColor
        this.borderWidth = borderWidth
        this.isCircle = isCircle
    }

    /** 内容区（去掉 padding）里的裁剪形状；圆形时取居中的正方形 */
    private fun updateShape() {
        shapeRect.set(
            paddingLeft.toFloat(), paddingTop.toFloat(),
            (width - paddingRight).toFloat(), (height - paddingBottom).toFloat(),
        )
        if (isCircle) {
            val side = minOf(shapeRect.width(), shapeRect.height())
            val cx = shapeRect.centerX()
            val cy = shapeRect.centerY()
            shapeRect.set(cx - side / 2, cy - side / 2, cx + side / 2, cy + side / 2)
        }
    }

    override fun onDraw(canvas: Canvas) {
        updateShape()
        if (shapeRect.isEmpty) return
        clipPath.rewind()
        clipPath.addOval(shapeRect, Path.Direction.CW)
        val saved = canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restoreToCount(saved)

        // 边框描在形状内侧：描边中线内缩半个边框宽度，外缘正好贴着裁剪边界
        if (borderWidth > 0 && Color.alpha(borderColor) != 0) {
            val half = borderWidth / 2f
            shapeRect.inset(half, half)
            if (!shapeRect.isEmpty) {
                borderPaint.color = borderColor
                borderPaint.strokeWidth = borderWidth.toFloat()
                canvas.drawOval(shapeRect, borderPaint)
            }
        }
    }
}
