package com.album.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.album.R;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 图片压缩和缓存类，缓存机制LruCache
 *
 * @author ghc
 */
public class BitmapCache {
    private Handler mHandler;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private LruCache<String, Bitmap> mImageLruCache;

    //应用程序最大可用内存
    int maxMemory = (int) Runtime.getRuntime().maxMemory();
    //设置图片缓存大小为maxMemory的1/8
    int cacheSize = maxMemory / 8;

    // add 2014.12.01 start
    private int mDefaultPhoto = R.drawable.icon_display_profile_def;

    private static final String TAG = "BitmapCache";

    public void setDefaultPhoto(int defaultphoto) {
        mDefaultPhoto = defaultphoto;
    }
    // add 2014.12.01 end

    public BitmapCache() {
        mHandler = new Handler();

        mImageLruCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        mThreadPoolExecutor = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS,
                new LIFOLinkedBlockingDeque<Runnable>());
    }

    /**
     * 将图片存储到LruCache
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null && bitmap != null) {
            mImageLruCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache缓存获取图片
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mImageLruCache.get(key);
    }

    public void displayBitmap(final ImageView imageView,
                              final String sourcePath, final ImageCallback callback) {

        if (TextUtils.isEmpty(sourcePath)) {
            return;
        }

        Bitmap bmp = getBitmapFromLruCache(sourcePath);
        if (bmp != null) {
            if (callback != null) {
                callback.imageLoad(imageView, bmp, sourcePath);
            }
        } else {
            imageView.setImageResource(mDefaultPhoto);
            mThreadPoolExecutor.execute(new ThreadPoolTast(sourcePath, imageView, callback));
        }
    }

    //对图片原图进行压缩
    private Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        try {
            while (true) {
                if ((options.outWidth >> i <= 256) && (options.outHeight >> i <= 256)) {
                    in = new BufferedInputStream(new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapUtil.reviewPicRotate(bitmap, path);
    }

    public interface ImageCallback {
        public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
    }

    class ThreadPoolTast implements Runnable {
        Bitmap thumb;
        String sourcePath;
        ImageView imageView;
        ImageCallback callback;

        public ThreadPoolTast(String sourcePath, ImageView imageView, ImageCallback callback) {
            this.sourcePath = sourcePath;
            this.imageView = imageView;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                //对图片原图进行压缩，并添加到LruCache缓存
                if (isVideoFile(sourcePath)) {
                    String pinName = sourcePath.substring(32, sourcePath.length() - 4);

                    Log.d(TAG, "is video file, crete thumbnail. " + sourcePath + " pinName " + pinName);

                    String thumbVideo = getVideoThumbnail(sourcePath);

                    thumb = revitionImageSize(thumbVideo);
                } else {
                    Log.d(TAG, "is image file, revitionImageSize thumbnail. " + sourcePath);
                    thumb = revitionImageSize(sourcePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            addBitmapToLruCache(sourcePath, thumb);

            if (callback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.imageLoad(imageView, thumb, sourcePath);
                    }
                });
            }
        }
    }

    private boolean isVideoFile(String sourcePath) {
        String type = sourcePath.substring(sourcePath.length() - 4, sourcePath.length());
        return type.equals(".mp4") ? true : false;

    }

    public static String getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = null;
        String fileSave = "/storage/emulated/0/DCIM/.thumbnails/";

        String picName = videoPath.substring(32, videoPath.length() - 4) + ".jpg";
        Log.d(TAG, "[getVideoThumbnail] >> picName " + picName);

        File file = new File(fileSave, picName);
        if (file.exists()) {
            Log.d(TAG, "[getVideoThumbnail] >> " + picName + " exit.");
            return fileSave + picName;
        }
        // 避免OOM
        try {
            bitmap = media.getFrameAtTime();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();

            Log.d(TAG, "[getVideoThumbnail] >> create thumb for " + picName);

            return fileSave + picName;
        } catch (OutOfMemoryError e) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
