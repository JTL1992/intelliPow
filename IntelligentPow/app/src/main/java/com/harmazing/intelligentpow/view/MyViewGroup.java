package com.harmazing.intelligentpow.view;

import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 *Created by JTL on 2015/4/17.
 *这个控件可以屏蔽点击事件
 * 在空调界面下拉菜单时使用遮罩效果
 */
public class MyViewGroup extends RelativeLayout {
    private OnDispatchTouchEvent e;

    public MyViewGroup(android.content.Context context, android.util.AttributeSet attrs){
        super(context,attrs);
    }
    public MyViewGroup(android.content.Context context, android.util.AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }
    public  interface OnDispatchTouchEvent{
        public boolean onDispatch(RelativeLayout viewGroup, MotionEvent me);
    }
    public boolean dispatch(MotionEvent me){
        if (e != null){
            return   e.onDispatch(this,me);
        }
        else
            return false;
    }
    public void setOnDispatch(OnDispatchTouchEvent cl){
        e = cl;
    }
    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {
                super.onLayout(b,i,i2,i3,i4);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
          if (dispatch(ev)){
              return true;
          }
        else
            return super.dispatchTouchEvent(ev);
    }
}
