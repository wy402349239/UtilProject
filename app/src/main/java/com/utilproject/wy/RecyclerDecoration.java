package com.utilproject.wy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author jx_wy
 *
 * time:2019年03月04日16:42
 *
 * RecyclerView rv = findViewById(R.id.demo_main_recycler);
 *         LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
 *         rv.setLayoutManager(manager);
 *         RecyclerDecoration rd = new RecyclerDecoration();
 *         rd.setDriverWidth(3);
 *         rd.setDiver(MainActivity.this.getResources().getDrawable(R.drawable.jb_statu));
 *         rv.addItemDecoration(rd);
 *         PopuUtil.TextAdapter adapter = new PopuUtil.TextAdapter(mItems);
 *         rv.setAdapter(adapter);
 *
 * RecyclerView nRv = popuView.findViewById(R.id.popu_recycler);
 *         GridLayoutManager manager = new GridLayoutManager(context, 2);
 *         nRv.setLayoutManager(manager);
 *         nRv.addItemDecoration(new RecyclerDecoration());
 *         TextAdapter adapter = new TextAdapter(strs);
 *         adapter.setEvent(event);
 *         nRv.setAdapter(adapter);
 */
public class RecyclerDecoration extends RecyclerView.ItemDecoration {

    Drawable mDiver;
    private int mDriverWidth = 1;//分割线宽度,默认1

    public RecyclerDecoration() {
        mDiver = new ColorDrawable(Color.GRAY);//分割线背景,默认灰色
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = getSpanCount(parent);
        if (isLastRaw(parent, spanCount, view)) {
            //最后一行
            outRect.set(0, 0, mDiver.getIntrinsicWidth(), 0);
        } else if (isLastColum(parent, spanCount, view)) {
            //最后一列
            outRect.set(0, 0, 0, mDiver.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDiver.getIntrinsicWidth(), mDiver.getIntrinsicHeight());
        }
    }

    /**
     * 设置分割线背景
     *
     * @param diver 背景
     */
    public void setDiver(Drawable diver) {
        if (diver != null) {
            this.mDiver = diver;
        }
    }

    /**
     * 设置分割线宽度(高度)
     *
     * @param width 宽高
     */
    public void setDriverWidth(int width) {
        this.mDriverWidth = width;
    }

    private int getSpanCount(RecyclerView rv) {
        int spanCount = -1;
        RecyclerView.LayoutManager manager = rv.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) manager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastRaw(RecyclerView rv, int spancount, View view) {
        RecyclerView.LayoutManager manager = rv
                .getLayoutManager();
        int position = manager.getPosition(view);
        if (manager instanceof GridLayoutManager) {
            int itemCount = rv.getAdapter().getItemCount();
            if (itemCount % spancount == 0) {
                //没有多余，一行一行显示
                if (itemCount == spancount) {
                    return true;//只有一行
                } else if (position >= itemCount - spancount) {
                    return true;//最后一行
                }
            } else {
                if (position >= itemCount - itemCount % spancount) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLastColum(RecyclerView rv, int spancount, View view) {
        RecyclerView.LayoutManager manager = rv
                .getLayoutManager();
        int position = manager.getPosition(view);
        if (manager instanceof GridLayoutManager) {
            if (position % spancount == spancount - 1) {
                return true;//最后一列
            }
        }
        return false;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin - mDriverWidth / 2;
            final int bottom = top + mDriverWidth;
            mDiver.setBounds(left, top, right, bottom);
            mDiver.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin - mDriverWidth / 2;
            final int right = left + mDriverWidth;

            mDiver.setBounds(left, top, right, bottom);
            mDiver.draw(c);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            drawVertical(c, parent);//只有gridlayout，才需要花竖直方向上的线
        }
    }
}

