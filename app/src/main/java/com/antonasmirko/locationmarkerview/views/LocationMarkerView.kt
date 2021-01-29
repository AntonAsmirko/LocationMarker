package com.antonasmirko.locationmarkerview.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class LocationMarkerView(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    companion object {
        private const val C = 0.551915024494f
    }

    private val mPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 24f
        style = Paint.Style.STROKE
        textSize = 60f
    }
    private val mPath = Path()

    private var mCenterX = 0f
    private var mCenterY = 0f

    private val pointF = PointF(0f, 0f)
    private val mCircleRadius = 200f
    private val mDifference = mCircleRadius * C

    private val mData = Array(8) { 1f }
    private val mCtrl = Array(16) { 1f }

    private val mDuration = 1000f
    private var mCurrent = 0f
    private var mCount = 100f
    private var mPiece = mDuration / mCount

    init {
        mData.let {
            it[0] = 0f
            it[1] = 0f

            it[2] = mCircleRadius
            it[3] = 0f

            it[4] = 0f
            it[5] = -mCircleRadius * 3

            it[6] = -mCircleRadius
            it[7] = 0f

//            it[0] = 0f
//            it[1] = mCircleRadius
//
//            it[2] = mCircleRadius
//            it[3] = 0f
//
//            it[4] = 0f
//            it[5] = -mCircleRadius * 3
//
//            it[6] = -mCircleRadius
//            it[7] = 0f
        }

        mCtrl.let {
//            it[0] = mData[0] + mDifference * 2
//            it[1] = mData[1]
//
//            it[2] = mData[2]
//            it[3] = mData[3] + mDifference * 2
//
//            it[4] = mData[2]
//            it[5] = mData[3] - mDifference
//
//            it[6] = mData[4] + mDifference - 100f
//            it[7] = mData[5]
//
//            it[8] = mData[4] - mDifference + 100f
//            it[9] = mData[5]
//
//            it[10] = mData[6]
//            it[11] = mData[7] - mDifference
//
//            it[12] = mData[6]
//            it[13] = mData[7] + mDifference * 2f
//
//            it[14] = mData[0] - mDifference * 2f
//            it[15] = mData[1]
            it[0] = 0f
            it[1] = 0f

            it[2] = mData[2]
            it[3] = mData[3]

            it[4] = mData[2]
            it[5] = mData[3] - mDifference

            it[6] = mData[4] + mDifference - 100f
            it[7] = mData[5]

            it[8] = mData[4] - mDifference + 100f
            it[9] = mData[5]

            it[10] = mData[6]
            it[11] = mData[7] - mDifference

            it[12] = mData[6]
            it[13] = mData[7]

            it[14] = 0f
            it[15] = 0f
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w.toFloat() / 2f
        mCenterY = h.toFloat() / 2f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.translate(mCenterX, mCenterY)
        canvas?.scale(1f, -1f)

        mPath.apply {
            moveTo(mData[0], mData[1])
            cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3])
            cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
            cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
            cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1])
            moveTo(mData[0], mData[1])
            lineTo(mData[6] - mCircleRadius, mData[7])
            arcTo(
                RectF(
                    mData[6] - mCircleRadius - 50f,
                    mData[7],
                    mData[6] - mCircleRadius + 50f,
                    mData[7] + 100f
                ), 270f, -90f
            )
            lineTo(mData[6] - mCircleRadius - 50f, mData[7] + 50f + 700f)
            arcTo(RectF(
                mData[6] - mCircleRadius - 50f,
                mData[7] + 50f + 700f,
                mData[6] - mCircleRadius + 50f,
                mData[7] + 50f + 700f + 100f
            ), 180f, -90f)
            lineTo(mData[6] - mCircleRadius - 50f + 1300f,mData[7] + 50f + 700f + 100f)
            arcTo(RectF(
                mData[6] - mCircleRadius - 50f + 1300f,
                mData[7] + 50f + 700f,
                mData[6] - mCircleRadius + 50f + 1300f,
                mData[7] + 50f + 700f + 100f
            ), 90f, -90f)
            lineTo(mData[6] - mCircleRadius + 50f + 1300f, mData[7] + 100f)
            arcTo(RectF(
                mData[6] - mCircleRadius - 50f + 1300f,
                mData[7] + 100f -100f,
                mData[6] - mCircleRadius + 50f + 1300f,
                mData[7] + 100f
            ), 0f, -90f)
            lineTo(mData[2], mData[3])
        }

        canvas?.drawPath(mPath, mPaint)
        mCurrent += mPiece
        if (mCurrent < mDuration) {
            mData[1] -= 120 / mCount
            mCtrl[7] += 80 / mCount
            mCtrl[9] += 80 / mCount
            mCtrl[4] -= 20 / mCount
            mCtrl[10] += 20 / mCount
            postInvalidateDelayed(mPiece.toLong())
        }
    }
}