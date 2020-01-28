package zy18735.example.deliveryMaster.util.image;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {

    public static Bitmap scaleBitmap(Bitmap bitmap, float scale)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;
    }
}
