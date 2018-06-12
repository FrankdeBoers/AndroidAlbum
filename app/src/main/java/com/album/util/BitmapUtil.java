package com.album.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * 图片处理工具类，主要是图片角度旋转。
 */
public class BitmapUtil {

    /**
     * 获取图片文件的信息，是否旋转了90度，如果是则反转
     *
     * @param bitmap 需要旋转的图片
     * @param path   图片的路径
     */
    public static Bitmap reviewPicRotate(Bitmap bitmap, String path) {
        int degree = getPicRotate(path);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(degree); // 旋转angle度
            try {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Matrix rotateBitmap(String filePath, Bitmap bitmap) {
        int rotate = getPicRotate(filePath);

        Matrix matrix = new Matrix();

        if (rotate != 0 && bitmap != null) {
            matrix.setRotate(rotate);
        }

        return matrix;
    }

    /**
     * 读取图片文件旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片旋转的角度
     */
    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 得到实际宽高和目标宽高的比率
     *
     * @param options
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @param isScale   倍数还是压缩
     * @return
     */
    public static float inSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, boolean isScale) {
        // 源图片的高度和宽度
        //竖
        int height = options.outHeight;
        int width = options.outWidth;
        //横
        if (options.outHeight < options.outWidth) {
            height = options.outWidth;
            width = options.outHeight;
        }

        float inSampleSize = 1f;

        float heightRatio = 1f;
        float widthRatio = 1f;

        if (isScale) {
            //目标宽高和实际宽高的比例， 要比例最小的
            if (height > reqHeight && width > reqWidth) {

                widthRatio = (float) reqWidth / width;
                heightRatio = (float) reqHeight / height;

                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
                // 一定都会小于等于目标的宽和高
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

        } else {
            // 计算出实际宽高和目标宽高的比率，要最大的压缩比
            if (height > reqHeight && width > reqWidth) {
                // 计算出实际宽高和目标宽高的比率
                widthRatio = (float) width / reqWidth;
                heightRatio = (float) height / reqHeight;

                // 选择宽和高中最大的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
                // 一定都会小于等于目标的宽和高
                inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
            }
            // 计算出实际宽高和目标宽高的比率，要最大的压缩比
//			else if (height < reqHeight && width < reqWidth) {
//				// 计算出实际宽高和目标宽高的比率
//
//				widthRatio = (float) reqWidth / width;
//				heightRatio = (float) reqHeight / height;
//
//				// 选择宽和高中最大的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
//				// 一定都会小于等于目标的宽和高
//				inSampleSize = heightRatio > widthRatio ? heightRatio
//						: widthRatio;
//			}
        }

        return inSampleSize;
    }
}
