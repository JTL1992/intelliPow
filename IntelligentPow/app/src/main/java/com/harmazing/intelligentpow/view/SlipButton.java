package com.harmazing.intelligentpow.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.harmazing.intelligentpow.R;

/**
 * Created by JTL on 2014/11/3.
 * 滑动按钮实现
 */
public class SlipButton extends View implements View.OnTouchListener
    {
        private boolean nowChoose = false;
        private boolean isChecked;
        private boolean onSlip = false;
        private float downX, nowX;
        private Rect rectOn, rectOff;
        private boolean isChgLsnOn = false;
        private OnChangedListener onChangedListener;
        private Bitmap bgOn, bgOff, btnSlip, btnChoosen, btnUnchoosen;
        public SlipButton(Context context)
        {
            super(context);
            init();
        }

        public SlipButton(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            init();
        }

        public SlipButton(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
            init();
        }

        private void init()
        {

            bgOn = BitmapFactory.decodeResource(getResources(), R.drawable.split_left_1);
            bgOff = BitmapFactory.decodeResource(getResources(), R.drawable.split_right_1);
            btnSlip = BitmapFactory.decodeResource(getResources(), R.drawable.icon_circle);
            btnChoosen = BitmapFactory.decodeResource(getResources(), R.drawable.icon_choosen_circle);
            btnUnchoosen = BitmapFactory.decodeResource(getResources(), R.drawable.icon_unchoosen_circle);
            rectOn = new Rect(0, 0, btnSlip.getWidth(), btnSlip.getHeight());
            rectOff = new Rect(bgOff.getWidth() - btnSlip.getWidth(), 0, bgOff.getWidth(),
                    btnSlip.getHeight());
            setOnTouchListener(this);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {

            super.onDraw(canvas);

            Matrix matrix = new Matrix();
            Paint paint = new Paint();
            float x;

            if (nowX < (bgOn.getWidth() / 2))
            {
                x = nowX - btnSlip.getWidth() / 2;
                canvas.drawBitmap(bgOff, matrix, paint);
            }

            else
            {
                x = bgOn.getWidth() - btnSlip.getWidth() / 2;
                canvas.drawBitmap(bgOn, matrix, paint);
            }

            if (onSlip)

            {
                if (nowX >= bgOn.getWidth())

                    x = bgOn.getWidth() - btnSlip.getWidth() / 2;

                else if (nowX < 0)
                {
                    x = 0;
                }
                else
                {
                    x = nowX - btnSlip.getWidth() / 2;
                }
//                canvas.drawBitmap(btnSlip, x, 0, paint);
            }
            else
            {

                if (nowChoose)
                {
                    x = rectOff.left;
                    canvas.drawBitmap(bgOn, matrix, paint);
                }
                else{
                    x = rectOn.left;
                    canvas.drawBitmap(bgOff, matrix, paint);
                }
            }
            if (isChecked)
            {
                canvas.drawBitmap(bgOn, matrix, paint);
                 x = rectOff.left;
                isChecked = !isChecked;

            }

            if (x < 0){
                x = 0;
//                canvas.drawBitmap(btnChoosen, x, 0, paint);
            }
            else if (x > bgOn.getWidth() - btnSlip.getWidth()){
                x = bgOn.getWidth() - btnSlip.getWidth();
//                canvas.drawBitmap(btnUnchoosen, x, 0, paint);
            }
            canvas.drawBitmap(btnSlip, x, 0, paint);

        }

        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())


            {
                case MotionEvent.ACTION_MOVE:
                    nowX = event.getX();
                    break;

                case MotionEvent.ACTION_DOWN:

                    if (event.getX() > bgOn.getWidth() || event.getY() > bgOn.getHeight())
                        return false;
                    onSlip = true;
                    downX = event.getX();
                    nowX = downX;
                    break;

                case MotionEvent.ACTION_CANCEL:

                    onSlip = false;
                    boolean choose = nowChoose;
                    if (nowX >= (bgOn.getWidth() / 2))
                    {
                        nowX = bgOn.getWidth() - btnSlip.getWidth() / 2;
                        nowChoose = true;
                    }
                    else
                    {
                        nowX = nowX - btnSlip.getWidth() / 2;
                        nowChoose = false;
                    }
                    if (isChgLsnOn && (choose != nowChoose))
                        onChangedListener.OnChanged(nowChoose);
                    break;
                case MotionEvent.ACTION_UP:

                    onSlip = false;
                    boolean LastChoose = nowChoose;

                    if (event.getX() >= (bgOn.getWidth() / 2))
                    {
                        nowX = bgOn.getWidth() - btnSlip.getWidth() / 2;
                        nowChoose = true;
                    }

                    else
                    {
                        nowX = nowX - btnSlip.getWidth() / 2;
                        nowChoose = false;
                    }

                    if (isChgLsnOn && (LastChoose != nowChoose)) //

                        onChangedListener.OnChanged(nowChoose);
                    break;
                default:
            }
            invalidate();
            return true;
        }

        public void setOnChangedListener(OnChangedListener l)
        {//
            isChgLsnOn = true;
            onChangedListener = l;
        }

        public interface OnChangedListener
        {
            abstract void OnChanged(boolean CheckState);
        }

        public void setCheck(boolean isChecked)
        {
            this.isChecked = isChecked;
            nowChoose = isChecked;
            invalidate();
        }
    }
