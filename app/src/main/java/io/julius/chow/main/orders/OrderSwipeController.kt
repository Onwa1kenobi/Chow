package io.julius.chow.main.orders

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import io.julius.chow.R

class OrderSwipeController(private val actionListener: Actions) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val p = Paint()

    var itemSwipeEnabled = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // We don't want to handle move actions, so we return false
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return itemSwipeEnabled
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        if (direction == ItemTouchHelper.RIGHT) {
            actionListener.onOrderProcessed(position)
        } else {
            actionListener.onOrderRejected(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val icon: Bitmap
        if (actionState == ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3

            if (dX > 0) {
                p.color = ContextCompat.getColor(itemView.context, R.color.green)
                val background = RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    dX,
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, p)
                icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.context.resources.getDrawable(
                        R.drawable.ic_done,
                        itemView.context.theme
                    ).toBitmap()
                } else {
                    itemView.context.resources.getDrawable(R.drawable.ic_done).toBitmap()
                }
                val iconDest = RectF(
                    itemView.left.toFloat() + width,
                    itemView.top.toFloat() + width,
                    itemView.left.toFloat() + 2 * width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, iconDest, p)
            } else {
                p.color = ContextCompat.getColor(itemView.context, R.color.colorAccent)
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, p)
                icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.context.resources.getDrawable(
                        R.drawable.ic_delete_white,
                        itemView.context.theme
                    ).toBitmap()
                } else {
                    itemView.context.resources.getDrawable(R.drawable.ic_delete_white).toBitmap()
                }
                val iconDest = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, iconDest, p)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    }


    interface Actions {
        fun onOrderProcessed(position: Int)
        fun onOrderRejected(position: Int)
    }
}