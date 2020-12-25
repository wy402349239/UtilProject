package com.util.kt.wy.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import com.util.kt.wy.R
import kotlin.math.roundToInt

/**
 * @Description: this is 镂空文字的view，需要版本大于等于5.0
 * @Author: jx_wy
 * @Date: 2020/11/30 7:43 PM
 */
class HollowTextView constructor(context: Context, attributeSet: AttributeSet?) :
        AppCompatTextView(context, attributeSet) {

//    <com.packg.kt.HollowTextView
//    xmlns:hollow="http://schemas.android.com/apk/res/com.packg.kt.std"
//    android:id="@+id/main_hollow"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:paddingStart="15dp"
//    android:paddingEnd="15dp"
//    android:paddingTop="2dp"
//    android:paddingBottom="3dp"
//    android:background="@android:color/holo_orange_dark"
//    app:layout_constraintEnd_toEndOf="parent"
//    app:layout_constraintStart_toStartOf="parent"
//    app:layout_constraintTop_toBottomOf="@+id/main_tv"
//    hollow:name="@string/app_name"
//    hollow:textSize="15sp"
//    hollow:radius="10dp"
//    hollow:bgColor="@android:color/black" />

//    val hollowView = findViewById<HollowTextView>(R.id.main_hollow)
//    hollowView.setBackgroundColor(Color.TRANSPARENT)
//    hollowView.setSize(125f)
//    hollowView.setText("妙啊")
//    hollowView.setBgColor(Color.GRAY)
//    hollowView.setRadius(3f)

    constructor(context: Context) : this(context, null)

    private lateinit var mPaint: Paint
    private var w = 0f
    private var h = 0f
    private var mWidth = 0f
    private var mHeight = 0f
    private lateinit var rectF: RectF

    private var textString: String = ""
    private var mBgColor: Int = 0
    private var mRadius: Float = 0f
    private var mTextSize: Float = 0f
    private var drawY: Float = 0f

    init {
        if (attributeSet != null) {
            val ta: TypedArray =
                    context.obtainStyledAttributes(attributeSet, R.styleable.HollowView)
            if (ta.hasValue(R.styleable.HollowView_name)) {
                textString = ta.getString(R.styleable.HollowView_name).toString()//文字内容
            }
            if (ta.hasValue(R.styleable.HollowView_bgColor)) {//背景色
                mBgColor = ta.getColor(R.styleable.HollowView_bgColor, mBgColor)
            }
            if (ta.hasValue(R.styleable.HollowView_textSize)) {
                // 获取文字大小
                mTextSize = ta.getDimension(R.styleable.HollowView_textSize, mTextSize)
            }
            // 获取圆角半径
            mRadius = ta.getDimension(R.styleable.HollowView_radius, mRadius)
            ta.recycle()
        }
        mPaint = Paint()
        mPaint.textSize = mTextSize
        mPaint.isAntiAlias = true
        mPaint.color = mBgColor
        val fontMetrics = mPaint.fontMetrics
        drawY = 0 - fontMetrics.top / 2 - fontMetrics.bottom / 2   // = (bottom - top) / 2 - bottom
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        meas()
    }

    fun meas() {
        w = mPaint.measureText(textString)
        val fontMetrics = mPaint.getFontMetrics()
        h = fontMetrics.bottom - fontMetrics.top
        mWidth = w + paddingLeft + paddingRight
        mHeight = h + paddingTop + paddingBottom
        rectF = RectF(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2)
        setMeasuredDimension(mWidth.roundToInt(), mHeight.roundToInt())
    }

    fun inva() {
        if (mPaint == null)
            mPaint = Paint()
        mPaint.textSize = mTextSize
        mPaint.isAntiAlias = true
        mPaint.color = mBgColor
        val fontMetrics = mPaint.fontMetrics
        drawY = 0 - fontMetrics.top / 2 - fontMetrics.bottom / 2   // = (bottom - top) / 2 - bottom

        meas()
        invalidate()
    }

    fun setText(text: String) {
        textString = text
        inva()
    }

    fun setBgColor(color: Int) {
        mBgColor = color
        inva()
    }

    fun setRadius(radius: Float) {
        mRadius = radius
        inva()
    }

    fun setSize(size: Float) {
        mTextSize = size
        inva()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null || rectF == null) {
            return
        }
        canvas.translate(mWidth / 2, mHeight / 2)
        val saveLayer = canvas.saveLayer(rectF, mPaint)
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint)//画背景

        mPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OUT))
        canvas.drawText(textString, -w / 2, drawY, mPaint)//镂空

        mPaint.setXfermode(null)//还原
        canvas.restoreToCount(saveLayer)
    }
}