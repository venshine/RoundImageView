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

/**
 * 示例里用到的边框颜色：首页循环、形状页色块共用同一份
 */
val BORDER_COLORS = intArrayOf(
    0xFFFF7A3D.toInt(), // 橙
    0xFFEC5C8C.toInt(), // 樱粉
    0xFF7C6CF0.toInt(), // 鸢尾
    0xFF3B82F6.toInt(), // 钴蓝
    0xFF1FB88A.toInt(), // 薄荷
    0xFFF2B01E.toInt(), // 琥珀
)

/** 颜色转成 `#RRGGBB` 文本 */
fun hexOf(color: Int): String = String.format("#%06X", color and 0xFFFFFF)
