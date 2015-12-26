package com.hankarun.piyangoogren;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class FastScrollRecyclerItemDec extends RecyclerView.ItemDecoration {
    private Context mContext;

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;


    public FastScrollRecyclerItemDec(Context context) {
        mContext = context;
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        mDivider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }


        float scaledWidth = ((FastScrollRecyclerView) parent).scaledWidth;
        float sx = ((FastScrollRecyclerView) parent).sx;
        float scaledHeight = ((FastScrollRecyclerView) parent).scaledHeight;
        float sy = ((FastScrollRecyclerView) parent).sy;
        String[] sections = ((FastScrollRecyclerView) parent).sections;
        String section = ((FastScrollRecyclerView) parent).section;
        boolean showLetter = ((FastScrollRecyclerView) parent).showLetter;

        // We draw the letter in the middle
        if (showLetter & section != null && !section.equals("")) {
            //overlay everything when displaying selected index Letter in the middle
            Paint overlayDark = new Paint();
            overlayDark.setColor(Color.BLACK);
            overlayDark.setAlpha(100);
            canvas.drawRect(0, 0, parent.getWidth(), parent.getHeight(), overlayDark);
            float middleTextSize = mContext.getResources().getDimension(R.dimen.fast_scroll_overlay_text_size);
            Paint middleLetter = new Paint();
            middleLetter.setColor(Color.WHITE);
            middleLetter.setTextSize(middleTextSize);
            middleLetter.setAntiAlias(true);
            middleLetter.setFakeBoldText(true);
            middleLetter.setStyle(Paint.Style.FILL);
            int xPos = (canvas.getWidth() - (int) middleTextSize) / 2;
            int yPos = (int) ((canvas.getHeight() / 2) - ((middleLetter.descent() + middleLetter.ascent()) / 2));


            canvas.drawText(section.toUpperCase(), xPos, yPos, middleLetter);
        }
        // draw indez A-Z

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < sections.length; i++) {
            if (showLetter & section != null && !section.equals("") && section != null
                    && sections[i].toUpperCase().equals(section.toUpperCase())) {
                textPaint.setColor(Color.WHITE);
                textPaint.setAlpha(255);
                textPaint.setFakeBoldText(true);
                textPaint.setTextSize(scaledWidth / 2);
                canvas.drawText(sections[i].toUpperCase(),
                        sx + textPaint.getTextSize() / 2, sy + parent.getPaddingTop()
                                + scaledHeight * (i + 1), textPaint);
                textPaint.setTextSize(scaledWidth);
                canvas.drawText("•",
                        sx - textPaint.getTextSize() / 3, sy + parent.getPaddingTop()
                                + scaledHeight * (i + 1) + scaledHeight / 3, textPaint);

            } else {
                textPaint.setColor(Color.LTGRAY);
                textPaint.setAlpha(200);
                textPaint.setFakeBoldText(false);
                textPaint.setTextSize(scaledWidth / 2);
                canvas.drawText(sections[i].toUpperCase(),
                        sx + textPaint.getTextSize() / 2, sy + parent.getPaddingTop()
                                + scaledHeight * (i + 1), textPaint);
            }

        }


    }
}
