package de.martin.vocabularcardapp.ui

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import de.martin.vocabularcardapp.ui.ButtonsState.*

class SwipeCallback(param: SwipeActions) : ItemTouchHelper.Callback() {
    private var swipeBack = false
    private var buttonShowedState = GONE
    private val buttonWidth = 500f
    private var buttonsActions: SwipeActions? = param

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
            )
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX <= 300 && dX >= -300) {
                    buttonShowedState = GONE
                } else {
                    if (dX < -buttonWidth) buttonShowedState = RIGHT_VISIBLE
                    else if (dX > buttonWidth) buttonShowedState = LEFT_VISIBLE
                }
                if (buttonsActions != null) {
                    if (buttonShowedState == LEFT_VISIBLE) {
                        buttonsActions!!.onRightSwiped(viewHolder.adapterPosition);
                    } else if (buttonShowedState == RIGHT_VISIBLE) {
                        buttonsActions!!.onLeftSwiped(viewHolder.adapterPosition);
                    }
                }
                buttonShowedState = GONE
            }
            false
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }
}

internal enum class ButtonsState {
    GONE, LEFT_VISIBLE, RIGHT_VISIBLE
}
