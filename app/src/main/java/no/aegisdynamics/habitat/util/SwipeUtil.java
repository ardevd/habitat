package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import no.aegisdynamics.habitat.R;

public abstract class SwipeUtil extends ItemTouchHelper.SimpleCallback {

    private Drawable background;
    private Drawable deleteIcon;

    private int xMarkMargin;

    private boolean initiated;
    private final Context context;

    private int leftColorCode;

    protected SwipeUtil(int dragDirs, int swipeDirs, Context context) {
        super(dragDirs, swipeDirs);
        this.context = context;
    }

    private void init() {
        background = new ColorDrawable();
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
        deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        initiated = true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        if (!initiated) {
            init();
        }

        int itemHeight = itemView.getBottom() - itemView.getTop();

        //Setting Swipe Background
        ((ColorDrawable) background).setColor(getLeftColorCode());
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        int intrinsicWidth = deleteIcon.getIntrinsicWidth();
        int intrinsicHeight = deleteIcon.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;


        //Setting Swipe Icon
        deleteIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom);
        deleteIcon.draw(c);


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private int getLeftColorCode() {
        return leftColorCode;
    }

    public void setLeftColorCode(int leftColorCode) {
        this.leftColorCode = leftColorCode;
    }

}
