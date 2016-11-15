/*
 * Copyright (C) 2013 The Android Open Source Project
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
package com.example.android.basicmultitouch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View


object TouchDisplayView {

  private[basicmultitouch] object TouchHistory {
    val HISTORY_COUNT: Int = 20
    private val MAX_POOL_SIZE: Int = 10
    private val sPool: Pools.SimplePool[TouchHistory] = new Pools.SimplePool[TouchHistory](MAX_POOL_SIZE)

    def obtain(x: Float, y: Float, pressure: Float): TouchHistory = {
      var data: TouchHistory = sPool.acquire
      if (data == null) {
        data = new TouchHistory
      }
      data.setTouch(x, y, pressure)
      data
    }
  }

  class TouchHistory {
    var i: Int = 0
    var x: Float = 0.0f
    var y: Float = 0.0f
    var pressure: Float = 0f
    var label: String = null
    var historyIndex: Int = 0
    var historyCount: Int = 0
    var history: Array[PointF] = Array.fill(TouchHistory.HISTORY_COUNT)(new PointF)

    def setTouch(x: Float, y: Float, pressure: Float) {
      this.x = x
      this.y = y
      this.pressure = pressure
    }

    def recycle(): Unit = {
      this.historyIndex = 0
      this.historyCount = 0
      TouchHistory.sPool.release(this)
    }

    def addHistory(x: Float, y: Float) {
      val p: PointF = history(historyIndex)
      p.x = x
      p.y = y
      historyIndex = (historyIndex + 1) % history.length
      if (historyCount < TouchHistory.HISTORY_COUNT) {
        historyCount += 1
      }
    }
  }

  private val CIRCLE_RADIUS_DP: Float = 75f
  private val CIRCLE_HISTORICAL_RADIUS_DP: Float = 7f
  private val BACKGROUND_ACTIVE: Int = Color.WHITE
  private val INACTIVE_BORDER_DP: Float = 15f
  private val INACTIVE_BORDER_COLOR: Int = 0xFFffd060
}

class TouchDisplayView(val context: Context, val attrs: AttributeSet) extends View(context, attrs) {

  import TouchDisplayView.TouchHistory

  private var mCircleRadius: Float = 0.0f
  private var mCircleHistoricalRadius: Float = 0.0f
  private val mCirclePaint: Paint = new Paint
  private val mTextPaint: Paint = new Paint
  private val mBorderPaint: Paint = new Paint
  private var mBorderWidth: Float = 0.0f
  val COLORS: Array[Int] = Array(0xFF33B5E5, 0xFFAA66CC, 0xFF99CC00, 0xFFFFBB33, 0xFFFF4444, 0xFF0099CC, 0xFF9933CC, 0xFF669900, 0xFFFF8800, 0xFFCC0000)

  private val mTouches: SparseArray[TouchHistory] = new SparseArray[TouchHistory](10)
  private var mHasTouch: Boolean = false

  initialisePaint()

  override def onTouchEvent(event: MotionEvent): Boolean = {
    val action: Int = event.getAction
    action & MotionEvent.ACTION_MASK match {
      case MotionEvent.ACTION_DOWN =>
        val id: Int = event.getPointerId(0)
        val data: TouchHistory = TouchHistory.obtain(event.getX(0), event.getY(0), event.getPressure(0))
        data.label = "id: " + 0
        mTouches.put(id, data)
        mHasTouch = true
      case MotionEvent.ACTION_POINTER_DOWN =>
        val index: Int = event.getActionIndex
        val id: Int = event.getPointerId(index)
        val data: TouchHistory = TouchHistory.obtain(event.getX(index), event.getY(index), event.getPressure(index))
        data.label = "id: " + id
        mTouches.put(id, data)
      case MotionEvent.ACTION_UP =>
        val id: Int = event.getPointerId(0)
        val data: TouchHistory = mTouches.get(id)
        mTouches.remove(id)
        data.recycle()
        mHasTouch = false
      case MotionEvent.ACTION_POINTER_UP =>
        val index: Int = event.getActionIndex
        val id: Int = event.getPointerId(index)
        val data: TouchHistory = mTouches.get(id)
        mTouches.remove(id)
        data.recycle()
      case MotionEvent.ACTION_MOVE =>
        for (index <- 0 until event.getPointerCount) {
          val id: Int = event.getPointerId(index)
          val data: TouchHistory = mTouches.get(id)
          data.addHistory(data.x, data.y)
          data.setTouch(event.getX(index), event.getY(index), event.getPressure(index))
        }
    }
    this.postInvalidate()
    true
  }

  override protected def onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (mHasTouch) {
      canvas.drawColor(TouchDisplayView.BACKGROUND_ACTIVE)
    } else {
      canvas.drawRect(mBorderWidth, mBorderWidth, getWidth - mBorderWidth, getHeight - mBorderWidth, mBorderPaint)
    }
    for (i <- 0 until mTouches.size) {
      val id: Int = mTouches.keyAt(i)
      val data: TouchHistory = mTouches.valueAt(i)
      if (data != null) {
        drawCircle(canvas, id, data)
      }
    }
  }

  private def initialisePaint(): Unit = {
    val density: Float = getResources.getDisplayMetrics.density
    mCircleRadius = TouchDisplayView.CIRCLE_RADIUS_DP * density
    mCircleHistoricalRadius = TouchDisplayView.CIRCLE_HISTORICAL_RADIUS_DP * density
    mTextPaint.setTextSize(27f)
    mTextPaint.setColor(Color.BLACK)
    mBorderWidth = TouchDisplayView.INACTIVE_BORDER_DP * density
    mBorderPaint.setStrokeWidth(mBorderWidth)
    mBorderPaint.setColor(TouchDisplayView.INACTIVE_BORDER_COLOR)
    mBorderPaint.setStyle(Paint.Style.STROKE)
  }

  protected def drawCircle(canvas: Canvas, id: Int, data: TouchHistory) {
    val color: Int = COLORS(id % COLORS.length)
    mCirclePaint.setColor(color)
    assert(data != null)
    val pressure: Float = Math.min(data.pressure, 1f)
    val radius: Float = pressure * mCircleRadius
    canvas.drawCircle(data.x, data.y - (radius / 2f), radius, mCirclePaint)
    mCirclePaint.setAlpha(125)
    for (j <- 0 to data.history.length if j < data.historyCount) {
      val p: PointF = data.history(j)
      canvas.drawCircle(p.x, p.y, mCircleHistoricalRadius, mCirclePaint)
    }
    canvas.drawText(data.label, data.x + radius, data.y - radius, mTextPaint)
  }
}
