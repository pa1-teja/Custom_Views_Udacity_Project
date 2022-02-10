package com.example.customviewsudacityproject.customViews

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import com.example.customviewsudacityproject.R
import timber.log.Timber
import java.io.Serializable
import kotlin.properties.Delegates


class LoadingIndicatorButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Serializable {

    private var WIDGET_DEFAULT_BACKGROUND_COLOR: Int =0
    private var LOADING_COMPLETED: Boolean = true
    private var WIDGET_TEXT_DIRECTION: Int =0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //Widget color attributes
    var INITIAL_STATE_COLOR = 0
    var IN_PROGRESS_COLOR = 0
    var FAILED_STATE_COLOR = 0
    var FINISH_STATE_COLOR = 0

        //Widget size attributes
    var WIDGET_WIDTH = 0
    var WIDGET_HEIGHT = 0

    var WIDGET_ON_CLICK: Boolean = false

    //Widget Text Attributes
    var WIDGET_TEXT_ALIGNMENT =0
    lateinit var WIDGET_TEXT: String
    var WIDGET_TEXT_SIZE = 0
    var WIDGET_TEXT_ALL_CAPS = false
    var WIDGET_TEXT_COLOR = 0
    var WIDGET_TEXT_STYLE = 0

    private var ANIMATED_WIDGET_WIDTH: Float = 0.0f
    private var ANIMATED_CIRCLE_ANGLE: Float = 0.0f

        //Circular Indicator Attributes
    var SHOW_CIRCULAR_PROGRESS_INDICATOR = false
    var CIRCULAR_INDICATOR_COLOR = 0

    private lateinit var buttonBGAnimator: ValueAnimator
    private lateinit var buttonCircleAnimator: ValueAnimator

   internal var loadStatus: LoadingButtonUtils by Delegates.observable(LoadingButtonUtils.ON_CLICK_DOWNLOAD){
            initialValue,oldValue,newValue ->

        when(newValue){
            LoadingButtonUtils.DOWNLOAD_IN_PROGRESS -> {
                WIDGET_ON_CLICK = true
                WIDGET_TEXT = context.getString(R.string.button_loading)
                invalidate()
            }
            LoadingButtonUtils.DOWNLOAD_FINISHED ->{
                WIDGET_TEXT = context.getString(R.string.button_download_finished)
                LoadingButtonUtils.ON_CLICK_DOWNLOAD
                buttonBGAnimator.cancel()
                buttonCircleAnimator.cancel()
                invalidate()
            }
            LoadingButtonUtils.DOWNLOAD_ERROR -> {
                WIDGET_TEXT = context.getString(R.string.button_download_error)
                LoadingButtonUtils.ON_CLICK_DOWNLOAD
                buttonBGAnimator.cancel()
                buttonCircleAnimator.cancel()
                invalidate()
            }
            LoadingButtonUtils.ON_CLICK_DOWNLOAD ->{
                WIDGET_ON_CLICK = false
                ANIMATED_WIDGET_WIDTH = 0f
                ANIMATED_CIRCLE_ANGLE = 0f
                WIDGET_TEXT = context.getString(R.string.button_name)
            }
        }
    }

    init {
        loadStatus = LoadingButtonUtils.ON_CLICK_DOWNLOAD
        isClickable = true
        context.withStyledAttributes(attrs,R.styleable.LoadingIndicatorButton){
            INITIAL_STATE_COLOR = getColor(R.styleable.LoadingIndicatorButton_android_background,0)
            IN_PROGRESS_COLOR = getColor(R.styleable.LoadingIndicatorButton_inProgressIndicatorColor, 0)
            FAILED_STATE_COLOR = getColor(R.styleable.LoadingIndicatorButton_failureIndicatorColor, 0)
            FINISH_STATE_COLOR = getColor(R.styleable.LoadingIndicatorButton_finishIndicatorColor, 0)
            WIDGET_DEFAULT_BACKGROUND_COLOR = getColor(R.styleable.LoadingIndicatorButton_android_background,0)

            WIDGET_TEXT_ALIGNMENT = getInt(R.styleable.LoadingIndicatorButton_android_textAlignment,0)
            WIDGET_TEXT = getString(R.styleable.LoadingIndicatorButton_android_text).toString()
            WIDGET_TEXT_SIZE = getDimensionPixelSize(R.styleable.LoadingIndicatorButton_android_textSize,16)
            WIDGET_TEXT_ALL_CAPS = getBoolean(R.styleable.LoadingIndicatorButton_textAllCaps,false)
            WIDGET_TEXT_COLOR = getColor(R.styleable.LoadingIndicatorButton_android_textColor,0)
            WIDGET_TEXT_STYLE = getInt(R.styleable.LoadingIndicatorButton_android_textStyle,0)

            SHOW_CIRCULAR_PROGRESS_INDICATOR = getBoolean(R.styleable.LoadingIndicatorButton_showCircularIndicator,false)
            CIRCULAR_INDICATOR_COLOR = getColor(R.styleable.LoadingIndicatorButton_circularIndicatorColor,0)

        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Timber.i("onAttachedToWindow : Called when the view is attached to a window.")
    }

    override fun performClick(): Boolean {
        super.performClick()
        loadStatus = LoadingButtonUtils.DOWNLOAD_IN_PROGRESS
        buttonBGAnimator = ValueAnimator.ofFloat(0.0f,WIDGET_WIDTH.toFloat()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener {
                ANIMATED_WIDGET_WIDTH = it.animatedValue as Float
                invalidate()
            }
        }
        buttonBGAnimator.start()

        if (SHOW_CIRCULAR_PROGRESS_INDICATOR){
            buttonCircleAnimator = ValueAnimator.ofFloat(0f,360f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                interpolator = LinearInterpolator()
                addUpdateListener {
                    ANIMATED_CIRCLE_ANGLE = it.animatedValue as Float
                    invalidate()
                }
            }
            buttonCircleAnimator.start()
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.d("onMeasure : Button dimensions - width: $width height: $height  widthMeasureSpec: $widthMeasureSpec heightMeasureSpec: $heightMeasureSpec")
        Timber.i("onMeasure : Called to determine the size requirements for this view and all of its children.")
            WIDGET_WIDTH = when (MeasureSpec.getMode(widthMeasureSpec)) {
                MeasureSpec.UNSPECIFIED -> 130
                MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
                MeasureSpec.AT_MOST -> 130.coerceAtMost(MeasureSpec.getSize(widthMeasureSpec))
                else -> 0
            }
            WIDGET_HEIGHT = when (MeasureSpec.getMode(heightMeasureSpec)) {
                MeasureSpec.UNSPECIFIED -> 150
                MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
                MeasureSpec.AT_MOST -> 150.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
                else -> 0
            }
        setMeasuredDimension(WIDGET_WIDTH,WIDGET_HEIGHT)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        Timber.d("onSizeChanged : Button dimensions - width: $width height: $height oldWidth: $oldWidth oldHeight: $oldHeight")
        WIDGET_HEIGHT = height
        WIDGET_WIDTH = width
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.save()

        when(loadStatus){
            LoadingButtonUtils.ON_CLICK_DOWNLOAD -> {
                clipAndDrawButtonDimensions(canvas)
                drawTextOnButton(canvas,WIDGET_TEXT)
            }
            LoadingButtonUtils.DOWNLOAD_IN_PROGRESS -> {
                clipAndDrawAnimButtonDimensions(canvas)
                drawTextOnButton(canvas, WIDGET_TEXT)

                if (SHOW_CIRCULAR_PROGRESS_INDICATOR){
                    showLoadingCircle(canvas)
                }

            }

            LoadingButtonUtils.DOWNLOAD_ERROR -> {
                clipAndDrawButtonDimensions(canvas)
                drawTextOnButton(canvas,WIDGET_TEXT)
            }
            LoadingButtonUtils.DOWNLOAD_FINISHED -> {
                clipAndDrawButtonDimensions(canvas)
                drawTextOnButton(canvas,WIDGET_TEXT)
            }
        }

        canvas?.restore()
    }
    override fun setTextDirection(textDirection: Int) {
        WIDGET_TEXT_DIRECTION = textDirection
    }

    private fun clipAndDrawAnimButtonDimensions(canvas: Canvas?){
        val rectF = RectF(canvas?.clipBounds!!)
        paint.color = IN_PROGRESS_COLOR
        canvas.drawRect(rectF.left,rectF.top, ANIMATED_WIDGET_WIDTH.toFloat(), WIDGET_HEIGHT.toFloat(),paint)
    }

    private fun clipAndDrawButtonDimensions(canvas: Canvas?){
        val rectF = RectF(canvas?.clipBounds!!)
        paint.color = WIDGET_DEFAULT_BACKGROUND_COLOR
        canvas.clipRect(rectF).apply {
            textAlignment  = WIDGET_TEXT_ALIGNMENT
            textDirection = WIDGET_TEXT_DIRECTION
        }
        canvas.drawRect(rectF,paint)
    }

    private fun drawTextOnButton(canvas: Canvas?, text:String){
        paint.setColor(WIDGET_TEXT_COLOR)
        paint.textSize = WIDGET_TEXT_SIZE.toFloat()
        canvas?.drawText(text, (WIDGET_WIDTH/4).toFloat(), (WIDGET_HEIGHT/1.5).toFloat(),paint)
    }

    private fun showLoadingCircle(canvas: Canvas?) {
        paint.color = CIRCULAR_INDICATOR_COLOR

//        if(ANIMATED_CIRCLE_ANGLE < 360f) ANIMATED_CIRCLE_ANGLE += 2 else ANIMATED_CIRCLE_ANGLE = 2f

        canvas?.drawArc(
            WIDGET_WIDTH/1.3f,
            WIDGET_HEIGHT/3f,
            (3f/4f)*WIDGET_WIDTH + (1f/10f)*WIDGET_WIDTH,
            3f/4f*WIDGET_HEIGHT,
            0f,
            ANIMATED_CIRCLE_ANGLE,
            true,
            paint
        )
    }

}