package com.group7.pawdicted.mobile.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private final ColorDrawable divider;
    private final int dividerHeight;
    private final int marginHorizontal;

    public DividerItemDecoration(Context context, int dividerHeightDp, int colorResId, int marginHorizontalDp) {
        divider = new ColorDrawable(ContextCompat.getColor(context, colorResId));
        dividerHeight = dividerHeightDp;
        marginHorizontal = marginHorizontalDp;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft() + marginHorizontal;
        int right = parent.getWidth() - parent.getPaddingRight() - marginHorizontal;

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + dividerHeight;
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) < parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = dividerHeight;
        }
    }
}