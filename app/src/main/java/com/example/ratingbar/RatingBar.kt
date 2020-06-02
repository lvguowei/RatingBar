package com.example.ratingbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.ceil

class RatingBar @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  private var mEmptyBitmap: Bitmap
  private var mFillBitmap: Bitmap
  private var mTotalGrade: Int = 5
  private var mGrade: Int = 0
  private var mSpace: Int = 0

  init {
    val array = context.obtainStyledAttributes(attrs, R.styleable.RatingBar)
    val emptyId = array.getResourceId(R.styleable.RatingBar_starEmpty, -1)
    if (emptyId == -1) {
      throw RuntimeException("starEmpty not specified")
    }
    mEmptyBitmap = BitmapFactory.decodeResource(resources, emptyId)

    val fillId = array.getResourceId(R.styleable.RatingBar_starFill, -1)
    if (fillId == -1) {
      throw RuntimeException("starFill not specified")
    }
    mFillBitmap = BitmapFactory.decodeResource(resources, fillId)
    mTotalGrade = array.getInt(R.styleable.RatingBar_grade, 5)
    mSpace = array.getDimension(R.styleable.RatingBar_space, 0f).toInt()

    array.recycle()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val height = mEmptyBitmap.height
    val width = mEmptyBitmap.width * mTotalGrade + mSpace * (mTotalGrade - 1)
    setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(height, heightMeasureSpec)
    )
  }

  override fun onDraw(canvas: Canvas) {
    for (i in 0..mTotalGrade) {
      val x = mEmptyBitmap.width * i + mSpace * i
      if (i + 1 > mGrade) {
        canvas.drawBitmap(mEmptyBitmap, x.toFloat(), 0f, null)
      } else {
        canvas.drawBitmap(mFillBitmap, x.toFloat(), 0f, null)
      }
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
        val grade = ceil(event.x / (mEmptyBitmap.width + mSpace)).toInt()
        if (mGrade == grade) return true
        mGrade = when {
          grade > mTotalGrade -> mTotalGrade
          grade < 0 -> 0
          else -> grade
        }
        invalidate()
      }
    }
    return true
  }
}