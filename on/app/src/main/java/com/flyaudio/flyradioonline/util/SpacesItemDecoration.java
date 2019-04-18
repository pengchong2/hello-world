package com.flyaudio.flyradioonline.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by liuzehao on 18-4-25.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int leftSpace;
    private int topSpace;
    private int tag;
    public SpacesItemDecoration(int left, int top, int tag) {
        this.leftSpace = left;
        this.topSpace = top;
        this.tag = tag;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        if(pos <= 2){
            outRect.top = 0;
        }else{
            outRect.top = topSpace;
        }

        if((pos % 3) == 0 && topSpace != 0){
            outRect.left = leftSpace + 12;
        }else if((pos % 3) == 2 && topSpace != 0){
            outRect.left = /*24*/0;
        }else{
            outRect.left = leftSpace /*+ 12*/;
        }
        /*if((pos % 3) == 2){
            outRect.right = 36;
        }*/
    }
}
