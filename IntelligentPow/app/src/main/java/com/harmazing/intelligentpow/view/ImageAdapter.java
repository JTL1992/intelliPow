package com.harmazing.intelligentpow.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.harmazing.intelligentpow.view.CoverFlow;

/**
 * Created by JTL on 2014/9/25.
 * 温度，模式，风速的 图片适配器
 */
public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    private Integer[] mImageIds;
    public CoverFlow.LayoutParams coverFlowSize = new CoverFlow.LayoutParams(250,100);

    public void setCoverFlowSize(CoverFlow.LayoutParams coverFlowSize) {
        this.coverFlowSize = coverFlowSize;
    }

    public ImageAdapter(Context c) {
        mContext = c;
    }
    public ImageAdapter(Context c,Integer[] mImageIds){
        mContext = c;
        this.mImageIds = mImageIds;
    }

    public int getCount() {
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView i = createReflectedImages(mContext,mImageIds[position]);

        i.setLayoutParams(coverFlowSize);
        i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //
        BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
        drawable.setAntiAlias(true);
        return i;
    }

    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
    }

    public ImageView createReflectedImages(Context mContext,int imageId) {

        Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);

        final int reflectionGap = 0;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

//       Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
//                height / 2, width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);

        canvas.drawBitmap(originalImage, 0, 0, null);

        Paint deafaultPaint = new Paint();

        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

//        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage
                .getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);

        //paint.setShader(shader);
        paint.setShadowLayer(20,5,5,Color.BLUE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitmapWithReflection);

        return imageView;
    }

}