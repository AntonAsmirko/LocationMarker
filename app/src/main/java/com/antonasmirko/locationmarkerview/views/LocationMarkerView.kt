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
        strokeWidth = 8f
        style = Paint.Style.FILL
        textSize = 60f
    }
    private val mPath = Path()

    private var mCenterX = 0f
    private var mCenterY = 0f

    private val pointF = PointF(0f, 0f)
    private val mInitCircleRadius = 200f

    private val mDurationFirstTransition = 5000f
    private var mCurrentFirstTransition = 0f
    private var mCountFirstTransition = 100f

    private var startFirstTransition = 1f
    private val stepFirstTransition = 0.02f

    private val mDurationSecondTransition = 6000f
    private var mCurrentSecondTransition = 0f
    private var mCountSecondTransition = 100f

    private val stepSecondTransition = 10f

    private val mDataConst = makeMDataFirstOrSecond(0f, 0f, mInitCircleRadius)
    private val mCtrlConst = makeMCtrlFirstToSecond(mDataConst, mDifference(mInitCircleRadius))
    private val mData = makeMDataFirstOrSecond(0f, 0f, mInitCircleRadius)
    private val mCtrl = makeMCtrlFirstToSecond(mData, mDifference(mInitCircleRadius))

    private fun mDifference(mCircleRadius: Float) = mCircleRadius * C

    private fun makeMDataFirstOrSecond(
        originX: Float,
        originY: Float,
        mCircleRadius: Float
    ): Array<Float> = Array(8) { 0f }.also {
        it[0] = originX
        it[1] = originY + mCircleRadius

        it[2] = originX + mCircleRadius
        it[3] = originY

        it[4] = originX
        it[5] = originY - mCircleRadius * 3

        it[6] = originX - mCircleRadius
        it[7] = originY
    }

    private fun makeMCtrlFirstToSecond(
        mData: Array<Float>,
        mDifference: Float
    ): Array<Float> =
        Array(16) { 0f }.also {
            it[0] = mData[0] + mDifference
            it[1] = mData[1]

            it[2] = mData[2]
            it[3] = mData[3] + mDifference

            it[4] = mData[2]
            it[5] = mData[3] - mDifference

            it[6] = mData[4] + mDifference
            it[7] = mData[5]

            it[8] = mData[4] - mDifference
            it[9] = mData[5]

            it[10] = mData[6]
            it[11] = mData[7] - mDifference

            it[12] = mData[6]
            it[13] = mData[7] + mDifference

            it[14] = mData[0] - mDifference
            it[15] = mData[1]
        }

    private fun makeMDataFourth(
        originX: Float,
        originY: Float,
        mCircleRadius: Float
    ): Array<Float> = Array(12) { 0f }.also {
        it[0] = originX
        it[1] = originY

        it[2] = originX + mCircleRadius
        it[3] = originY

        it[4] = originX
        it[5] = originY - mCircleRadius * 3

        it[6] = originX - mCircleRadius
        it[7] = originY

        it[8] = originX
        it[9] = originY

        it[10] = originX - mCircleRadius
        it[11] = originY
    }

    private fun makeMCtrlFourth(
        originX: Float,
        originY: Float,
        mData: Array<Float>,
        mDifference: Float
    ): Array<Float> =
        Array(16) { 0f }.also {
            it[0] = originX
            it[1] = originY

            it[2] = mData[2]
            it[3] = mData[3]

            it[4] = mData[2]
            it[5] = mData[3] - mDifference

            it[6] = mData[4] + mDifference
            it[7] = mData[5]

            it[8] = mData[4] - mDifference
            it[9] = mData[5]

            it[10] = mData[6]
            it[11] = mData[7] - mDifference

            it[12] = mData[6]
            it[13] = mData[7]

            it[14] = originX
            it[15] = originY
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

        if (mCurrentFirstTransition <= mDurationFirstTransition) {
            mCurrentFirstTransition += mCountFirstTransition
            drawFirstOrSecond(canvas!!, mData, mCtrl, mPath)

            mCtrl[0] = mCtrlConst[0] * startFirstTransition
            mCtrl[3] = mCtrlConst[3] * startFirstTransition
            mCtrl[13] = mCtrlConst[13] * startFirstTransition
            mCtrl[14] = mCtrlConst[14] * startFirstTransition

            startFirstTransition += stepFirstTransition
            postInvalidateDelayed(15L)
        }

        if (mCurrentFirstTransition > mDurationFirstTransition) drawThirdForm(
            canvas!!,
            mData,
            mCtrl,
            mInitCircleRadius,
            0.333333f,
            3f,
            mPath
        )


    }

    private fun drawFirstOrSecond(
        canvas: Canvas,
        mData: Array<Float>,
        mCtrl: Array<Float>,
        mPath: Path
    ) {
        canvas.drawPath(mPath.apply {
            moveTo(mData[0], mData[1])
            cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3])
            cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
            cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
            cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1])
        }, mPaint)
    }

    private fun drawThirdForm(
        canvas: Canvas,
        mData: Array<Float>,
        mCtrl: Array<Float>,
        mCircleRadius: Float,
        angleSmoothnessRatio: Float,
        upHeight: Float,
        mPath: Path
    ) {
        canvas.drawPath(mPath.apply {
            moveTo(mData[2], mData[3])
            cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
            cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
            lineTo(mData[6], mData[7] + mCircleRadius * upHeight - mCircleRadius * angleSmoothnessRatio)
            arcTo(
                mData[6],
                mCircleRadius * upHeight - mCircleRadius * angleSmoothnessRatio * 2f,
                mData[6] + mCircleRadius * angleSmoothnessRatio * 2f,
                mCircleRadius * upHeight,
                180f,
                -90f,
                false
            )
            lineTo(
                mData[2] - mCircleRadius * angleSmoothnessRatio,
                mData[3] + mCircleRadius * upHeight
            )
            arcTo(
                mData[2] - mCircleRadius * angleSmoothnessRatio * 2f,
                mCircleRadius * upHeight - mCircleRadius * angleSmoothnessRatio * 2f,
                mData[2],
                mCircleRadius * upHeight,
                90f,
                -90f,
                false
            )
        }, mPaint)
    }

    private fun drawFourthForm(
        canvas: Canvas,
        mData: Array<Float>,
        mCtrl: Array<Float>,
        mCircleRadius: Float,
        mPath: Path
    ) {
        val BALANCE_CONST_HEIGHT = 0.285714f
        val BALANCE_CONST_WIDTH = 0.153848f
        canvas.drawPath(mPath.apply {
            moveTo(mData[2], mData[3])
            cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
            cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
            lineTo(mData[6] - mCircleRadius, mData[7])
            arcTo(
                RectF(
                    mData[6] - mCircleRadius - 50f,
                    mData[7],
                    mData[6] - mCircleRadius + 50f,
                    mData[7] + 100f
                ), 270f, -90f
            )
            lineTo(
                mData[6] - mCircleRadius - 50f,
                mData[7] + 50f + mCircleRadius / BALANCE_CONST_HEIGHT
            )
            arcTo(
                RectF(
                    mData[6] - mCircleRadius - 50f,
                    mData[7] + 50f + mCircleRadius / BALANCE_CONST_HEIGHT,
                    mData[6] - mCircleRadius + 50f,
                    mData[7] + 150f + mCircleRadius / BALANCE_CONST_HEIGHT
                ), 180f, -90f
            )
            lineTo(
                mData[6] - mCircleRadius - 50f + mCircleRadius / BALANCE_CONST_WIDTH,
                mData[7] + 150f + mCircleRadius / BALANCE_CONST_HEIGHT
            )
            arcTo(
                RectF(
                    mData[6] - mCircleRadius - 50f + mCircleRadius / BALANCE_CONST_WIDTH,
                    mData[7] + 50f + mCircleRadius / BALANCE_CONST_HEIGHT,
                    mData[6] - mCircleRadius + 50f + mCircleRadius / BALANCE_CONST_WIDTH,
                    mData[7] + 150f + mCircleRadius / BALANCE_CONST_HEIGHT
                ), 90f, -90f
            )
            lineTo(
                mData[6] - mCircleRadius + 50f + mCircleRadius / BALANCE_CONST_WIDTH,
                mData[7] + 100f
            )
            arcTo(
                RectF(
                    mData[6] - mCircleRadius - 50f + mCircleRadius / BALANCE_CONST_WIDTH,
                    mData[7],
                    mData[6] - mCircleRadius + 50f + mCircleRadius / BALANCE_CONST_WIDTH,
                    mData[7] + 100f
                ), 0f, -90f
            )
            lineTo(mData[2], mData[3])
        }, mPaint)
    }
}