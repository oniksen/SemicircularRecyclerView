package com.onixen.semicircular_layout_manager

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sin


private const val TEST_RADIUS_KOEFF = 2
private const val MILLISECONDS_PER_PX = 55f //default is 25f (bigger = slower)

/**
 * Отобразит максимально воможное количество элементов на экране, основываясь на указанной ширине элемента.
 * ### Для корректного отображения элементов в recycler view, необходимо центрировать по горизонтали содержимое элемента (gravity="center_horizontal").
 * @param itemViewWidthDp Ширина элемента в dp.
 * @param millisecondsPerPx Скорость скролла (измеряется в ms/px) при вызове метода smoothScrollToPosition().
 * */
class SemicircularLayoutManager(private val itemViewWidthDp: Int, private val millisecondsPerPx: Float = 55f) : RecyclerView.LayoutManager() {
    private var itemHorizontalMarginDp: Int? = null
    private var countVisibleItems: Any? = null

    /**
     * Отобразит максимально воможное количество элементов на экране, основываясь на указанной ширине элемента и отступах между элементами.
     * ### Для корректного отображения элементов в recycler view, необходимо центрировать по горизонтали содержимое элемента (gravity="center_horizontal").
     * @param itemViewWidthDp Ширина элемента в dp.
     * @param itemHorizontalMarginDp значение отступа по горизонтали в dp.
     * @param millisecondsPerPx Скорость скролла (измеряется в ms/px) при вызове метода smoothScrollToPosition().
     * */
    constructor(itemViewWidthDp: Int, itemHorizontalMarginDp: Int, millisecondsPerPx: Float = 55f) : this(itemViewWidthDp, millisecondsPerPx) {
        this.itemHorizontalMarginDp = itemHorizontalMarginDp
    }
    /**
     * Отобразит указанное количество элементов.
     * ### Для корректного отображения элементов в recycler view, необходимо центрировать по горизонтали содержимое элемента (gravity="center_horizontal").
     * @param itemViewWidthDp Ширина элемента в dp.
     * @param countVisibleItems Количество видимых на экране элементов. Принимает два типа данных:
     * - Float - Чётко заданное количество элементов. Если указанное количество будет превышать максимально-возможное, то отобразится максимальное с нулевыми отступами.
     * - String - Константа [SHOW_MAX_ITEMS].
     * @param millisecondsPerPx Скорость скролла (измеряется в ms/px) при вызове метода smoothScrollToPosition().
     * @throws IllegalArgumentException [countVisibleItems] является отрицательным числом.
     * @throws ClassNotFoundException [countVisibleItems] имеет не подходящий тип данных.
     * */
    constructor(itemViewWidthDp: Int, countVisibleItems: Any, millisecondsPerPx: Float = 55f) : this(itemViewWidthDp, millisecondsPerPx) {
        this.countVisibleItems = countVisibleItems
    }

    private val computedItemWidthPx: Int get() {
        var resultValue: Int = itemViewWidthDp.toPx()
        countVisibleItems?.let {
            when (it::class) {
                Float::class -> {
                    Log.d(TAG, "countVisibleItems is Float")
                    resultValue = if (it as Float > 0) {
                        val margin = width / it - itemViewWidthDp.toPx()
                        if (margin <= 0) itemViewWidthDp.toPx() else (itemViewWidthDp.toPx() + margin).toInt()
                    } else {
                        throw IllegalArgumentException("Значение [countVisibleItems] не должно быть отрицательным числом")
                    }
                }
                String::class -> {
                    Log.d(TAG, "countVisibleItems is String")
                    if (it == SHOW_MAX_ITEMS) {
                        resultValue = itemViewWidthDp.toPx()
                    }
                }
                else -> {
                    throw ClassNotFoundException("Для параметра 'countVisibleItems' требуется Float или String значение")
                }
            }
        }
        itemHorizontalMarginDp?.let {
            return itemViewWidthDp.toPx() + it.toPx()
        }
        return resultValue
    }

    private val mShrinkAmount = 0.35f
    private val mShrinkDistance = 0.9f

    private var horizontalScrollOffset = 0
    private val maxHorizontalScrollOffset: Int get() = computedItemWidthPx * (itemCount - 1)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams = RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    override fun canScrollHorizontally(): Boolean = true
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        fill(recycler)
        if (itemCount == 0) {
            return 0
        }
        if (horizontalScrollOffset in 0..maxHorizontalScrollOffset) {
            horizontalScrollOffset += dx
            return dx
        } else {
            if (horizontalScrollOffset < 0 && dx < 0) {
                return 0
            }
            if (horizontalScrollOffset < 0 && dx > 0) {
                horizontalScrollOffset += dx
                return dx
            }
            if (horizontalScrollOffset > maxHorizontalScrollOffset && dx > 0) {
                return 0
            }
            if (horizontalScrollOffset > maxHorizontalScrollOffset && dx < 0) {
                horizontalScrollOffset += dx
                return dx
            }
            return 0
        }
    }
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        fill(recycler)
    }
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller: SmoothScroller = CenterSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)

        val viewWidth = computedItemWidthPx
        val startOffset = width.toDouble() / 2 - viewWidth.toDouble() / 2

        val midpoint = width / 2f
        val d0 = 0f
        val d1 = mShrinkDistance * midpoint
        val s0 = 1f
        val s1 = 1f - mShrinkAmount

        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)

            /*val childMidpoint = (getDecoratedRight(view) + getDecoratedLeft(view)) / 2f
            val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
            val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            view.scaleX = scale
            view.scaleY = scale*/

            addView(view)

            val left = (startOffset + viewWidth * i - horizontalScrollOffset).toInt()
            val top = getTopOffset(left + viewWidth / 2)
            val right = left + viewWidth
            val bottom = top + viewWidth

            measureChild(view, viewWidth, viewWidth)

            layoutDecorated(view, left, top, right, bottom)
        }

        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }
    private fun getTopOffset(viewCentreX: Int): Int {
        /*val radius = width.toDouble() * width.toDouble() / (8 * height.toDouble()) + height.toDouble() / 2
        val beta = acos(width.toDouble() / (2 * radius))
        val alpha = beta + position * ((Math.PI - 2 * beta) / NUMBER_OF_VISIBLE_ELEMENTS)
        val y = radius - (radius * sin(alpha))
        return y.toInt()*/

        val s = width.toDouble() / 2
        val h = height.toDouble()
        // val radius = (h*h + s*s) / (h*2) - first variant for radius calculation
        val radiusV2 = ((width.toDouble()*width.toDouble()) / (8*height.toDouble()) + h/2) * TEST_RADIUS_KOEFF

        val xScreenFraction = viewCentreX.toDouble() / width.toDouble()
        val beta = acos(s / radiusV2)

        val alpha = beta + (xScreenFraction * (Math.PI - (2 * beta)))
        val yComponent = radiusV2 - (radiusV2 * sin(alpha))
        return yComponent.toInt()
    }
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private class CenterSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return MILLISECONDS_PER_PX / displayMetrics.densityDpi
        }
    }

    companion object {
        const val TAG = "layout_manager"
        /** Отобразить максимально возможное количество элементов */
        const val SHOW_MAX_ITEMS = "max_items"
    }
}