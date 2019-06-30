package com.tk4218.grocerylistr;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/*
 * Created by taylo on 3/4/2018.
 */

public class NonScrollRecyclerView extends RecyclerView{

    public NonScrollRecyclerView(Context context) {
        super(context);
    }
    public NonScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
