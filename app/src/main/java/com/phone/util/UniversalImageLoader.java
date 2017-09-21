package com.phone.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.phone.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 通用图片加载程序（UIL）的显示方法工具类
 */
public class UniversalImageLoader {

    private static final String TAG = "UniversalImageLoader";
    private static final String videoThumbUri = "file:///storage/emulated/0/DCIM/.thumbnails/";

    private static ImageLoader imageLoader = ImageLoader.getInstance();

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static boolean checkImageLoader() {
        return imageLoader.isInited();
    }

    /**
     * 加载显示listview gridview本地图库里图片
     *
     * @param uri
     * @param imageAware
     */
    public static void displayLocalImage(String uri, ImageAware imageAware) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_display_profile_def)
                .showImageForEmptyUri(R.drawable.ic_error)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        //System.out.println("cccc----display local image 111");
        imageLoader.displayImage(uri, imageAware, options);
    }

    /**
     * 加载显示viewpager大图片
     *
     * @param uri
     * @param imageView
     * @param listener
     */
    public static void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        //System.out.println("cccc----display big image 222");
        Log.d(TAG, "[displayImage] >>> uri : " + uri);
        if (isVideoFile(uri)) {
            String videoThumb = createVideoThumbnail(uri);
            Log.d(TAG, "[displayImage] >>> thumbPath " + videoThumb);
            imageLoader.displayImage(videoThumb, imageView, options, listener);
        } else {
            imageLoader.displayImage(uri, imageView, options, listener);

        }
    }


    private static String createVideoThumbnail(String videoPath) {

        String thumbPath = videoThumbUri + videoPath.substring(39, videoPath.length() - 4) + ".jpg";

        return thumbPath;
    }

    private static boolean isVideoFile(String sourcePath) {
        String type = sourcePath.substring(sourcePath.length() - 4, sourcePath.length());
        return type.equals(".mp4") ? true : false;

    }

    /**
     * 加载显示百度地图位置图片
     *
     * @param uri
     * @param imageView
     */
    public static void displayBaiduMap(String uri, ImageView imageView, ImageLoadingListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_location_proview)
                .showImageForEmptyUri(R.drawable.icon_location_proview)
                .showImageOnFail(R.drawable.icon_location_proview)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        //System.out.println("cccc----display baiduMap image 333");
        imageLoader.displayImage(uri, imageView, options, listener);
    }

    /**
     * 加载网络图片
     *
     * @param uri
     * @param imageView
     */
    public static void displayNetImage(String uri, ImageAware imageView) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_display_profile_def)
                .showImageForEmptyUri(R.drawable.ic_error)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();

        //System.out.println("cccc----display net image 444");
        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 清除缓存
     */
    public static void clear() {
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
    }

    /**
     * 清除内存缓存
     */
    public static void clearMemoryCache() {
        imageLoader.clearMemoryCache();
    }

    /**
     * 清除磁盘缓存
     */
    public static void clearDiskCache() {
        imageLoader.clearDiskCache();
    }

    /**
     * 恢复加载
     */
    public static void resume() {
        imageLoader.resume();
    }

    /**
     * 暂停加载
     */
    public static void pause() {
        imageLoader.pause();
    }

    /**
     * 停止加载
     */
    public static void stop() {
        imageLoader.stop();
    }

    /**
     * 销毁加载
     */
    public static void destroy() {
        imageLoader.destroy();
    }
}
