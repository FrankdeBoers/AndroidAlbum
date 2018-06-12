package com.album.util;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;

/**
 * 使用UIL第三方开源库必须做的配置检查，详情参见GitHub上关于UIL的WIKI和使用说明
 */
public class CheckImageLoaderConfiguration {

    public static void checkImageLoaderConfiguration(Context context) throws IOException {
        if (!UniversalImageLoader.checkImageLoader()) {

            File cacheDir = StorageUtils.getCacheDirectory(context, true);

            //应用程序最大可用内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();

            //应用程序已获得内存
            int totleMemory = (int) Runtime.getRuntime().totalMemory();

            //应用程序已获得内存中未使用内存
            int freeMemory = (int) Runtime.getRuntime().freeMemory();

            //设置图片缓存大小为应用程序可用内存的1/4
            int cacheSize = (maxMemory - totleMemory) / 4;

//			System.out.println("cccc----maxMemory = " + maxMemory / 1024 / 1024);
//			System.out.println("cccc----totleMem = " + totleMemory / 1024 / 1024);
//			System.out.println("cccc----freeMem = " + freeMemory / 1024 / 1024);
//			System.out.println("cccc----cacheSize = " + cacheSize / 1024 / 1024);
//			System.out.println("cccc----cacheDir = " + cacheDir.getAbsolutePath());

            int cacheMemMaxSize = cacheSize;
            long cacheDiskMaxSize = 200 * 1024 * 1024;//200MB

//			ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);默认配置也很好用哦！！！
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .threadPoolSize(4)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(cacheMemMaxSize))
                    .diskCache(new LruDiscCache(cacheDir, new Md5FileNameGenerator(), cacheDiskMaxSize))
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    .build();

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config);
        }
    }
}
