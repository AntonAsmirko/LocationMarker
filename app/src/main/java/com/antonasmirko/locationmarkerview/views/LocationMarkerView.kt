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
        strokeWidth = 4f
        style = Paint.Style.STROKE
        textSize = 60f
    }
    private val mPaintFill = Paint().apply {
        color = Color.TRANSPARENT
        strokeWidth = 4f
        alpha = 0
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        isAntiAlias = true;
        textSize = 60f
    }
    val mPath = Path()

    var mCenterX = 0f
    var mCenterY = 0f

    val mInitCircleRadius = 70f

    val mDurationFirstTransition = 1400f
    private var mCurrentFirstTransition = 0f
    var mCountFirstTransition = 100f

    private var startFirstTransition = 0f
    val stepFirstTransition = 5f

    val mDurationSecondTransition = 2000f
    private var mCurrentSecondTransition = 0f
    var mCountSecondTransition = 100f

    private var startSecondTransition = 1f
    val stepSecondTransition = 0.1f

    private var startFourthTransition = 0f
    val stepFourthTransition = 270f / (mDurationSecondTransition / mCountSecondTransition)

    val mData = makeMDataFirstOrSecond(width / 2f, height / 2f, mInitCircleRadius)
    val mCtrl = makeMCtrlFirstToSecond(mData, mDifference(mInitCircleRadius))

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

        mPath.reset()

        if (mCurrentFirstTransition <= mDurationFirstTransition) {
            mCurrentFirstTransition += mCountFirstTransition
            drawFirstOrSecond(
                canvas!!,
                mData,
                mCtrl,
                mInitCircleRadius,
                startFirstTransition,
                mPath,
                mPaintFill
            )
            drawFirstOrSecond(
                canvas,
                mData,
                mCtrl,
                mInitCircleRadius,
                startFirstTransition,
                mPath,
                mPaint
            )
            startFirstTransition += stepFirstTransition
            postInvalidateDelayed(15L)
        }


        if (mCurrentFirstTransition > mDurationFirstTransition && mCurrentSecondTransition <= mDurationSecondTransition) {
            mCurrentSecondTransition += mCountSecondTransition
            drawFourthForm(
                canvas!!,
                mData,
                mCtrl,
                mInitCircleRadius,
                0.333333f,
                startSecondTransition,
                startFourthTransition,
                mPath,
                mPaintFill
            )
            drawFourthForm(
                canvas,
                mData,
                mCtrl,
                mInitCircleRadius,
                0.333333f,
                startSecondTransition,
                startFourthTransition,
                mPath,
                mPaint
            )
            startFourthTransition += stepFourthTransition
            startSecondTransition += stepSecondTransition
            postInvalidateDelayed(15L)
        }
        if (mCurrentFirstTransition > mDurationFirstTransition && mCurrentSecondTransition > mDurationSecondTransition) {
            drawFourthForm(
                canvas!!,
                mData,
                mCtrl,
                mInitCircleRadius,
                0.333333f,
                startSecondTransition,
                startFourthTransition,
                mPath,
                mPaintFill
            )
            drawFourthForm(
                canvas!!,
                mData,
                mCtrl,
                mInitCircleRadius,
                0.333333f,
                startSecondTransition,
                startFourthTransition,
                mPath,
                mPaint
            )
        }
    }

    private fun drawFirstOrSecond(
        canvas: Canvas,
        mData: Array<Float>,
        mCtrl: Array<Float>,
        mCircleRadius: Float,
        mStretchVal: Float,
        mPath: Path,
        mPaint: Paint
    ) {
        canvas.drawPath(mPath.apply {
            moveTo(mData[0], mData[1])
            lineTo((mData[2] - mData[6] - mStretchVal) / 2f + mData[6] + mStretchVal, mData[1])
            arcTo(
                mData[6] + mStretchVal,
                mData[1] - 2f * mCircleRadius + mStretchVal,
                mData[2],
                mData[1],
                90f,
                -90f,
                false
            )
            cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
            cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
            arcTo(
                mData[6],
                mData[1] - 2f * mCircleRadius + mStretchVal,
                mData[2] - mStretchVal,
                mData[1],
                180f,
                -90f,
                false
            )
            close()
        }, mPaint)
    }

    private fun drawFourthForm(
        canvas: Canvas,
        mData: Array<Float>,
        mCtrl: Array<Float>,
        mCircleRadius: Float,
        angleSmoothnessRatio: Float,
        upHeight: Float,
        upWidth: Float,
        mPath: Path,
        mPaint: Paint
    ) {
        canvas.drawPath(mPath.apply {
            moveTo(mData[2], mData[3])
            cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
            cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
            lineTo(mData[6] - upWidth / 3f + mCircleRadius * angleSmoothnessRatio, mData[7])
            arcTo(
                mData[6] - upWidth / 3f,
                mData[7],
                mData[6] - upWidth / 3f + mCircleRadius * angleSmoothnessRatio * 2f,
                mData[7] + mCircleRadius * angleSmoothnessRatio * 2f,
                -90f,
                -90f,
                false
            )
            lineTo(
                mData[6] - upWidth / 3f,
                mData[7] + mCircleRadius * upHeight - mCircleRadius * angleSmoothnessRatio
            )
            arcTo(
                mData[6] - upWidth / 3f,
                mCircleRadius * upHeight - mCircleRadius * angleSmoothnessRatio * 2f,
                mData[6] - upWidth / 3f + mCircleRadius * angleSmoothnessRatio * 2f,
                mCircleRadius * upHeight,
                180f,
                -90f,
                false
            )
            lineTo(
                mData[2] + upWidth * 1.5f - mCircleRadius * angleSmoothnessRatio,
                mData[3] + mCircleRadius * upHeight
            )
            arcTo(
                mData[2] + upWidth * 1.5f - mCircleRadius * angleSmoothnessRatio * 2f,
                mCircleRadius * upHeight - mCircleRadius * angleSmoothnessRatio * 2f,
                mData[2] + upWidth * 1.5f,
                mCircleRadius * upHeight,
                90f,
                -90f,
                false
            )
            lineTo(mData[2] + upWidth * 1.5f, mData[3] + mCircleRadius * angleSmoothnessRatio * 2f)
            arcTo(
                mData[2] + upWidth * 1.5f - mCircleRadius * angleSmoothnessRatio * 2f,
                mData[3],
                mData[2] + upWidth * 1.5f,
                mData[3] + mCircleRadius * angleSmoothnessRatio * 2f,
                0f,
                -90f,
                false
            )
            close()
        }, mPaint)
    }
}