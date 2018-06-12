package com.album.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.album.R;
import com.album.entity.AlbumInfo;
import com.album.entity.PhotoInfo;
import com.album.util.UniversalImageLoader;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ViewPager适配器，继承自PagerAdapter
 *
 * @author ghc
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Activity mActivity;
    private List<PhotoInfo> mPhotoList;
    private boolean showToolbar = false;
    private ActionBar mActionBar;

    private MediaController mediaController;

    boolean isPlay = false;

    private boolean isShowPlayBtn = true;

    private OnStickerClickListener mOnStickerClickListener;

    public interface OnStickerClickListener {
        void onStickerClickListener(String path);

        void onDeleteTouchListener(String path);
    }


    public ViewPagerAdapter(Context context, AlbumInfo info) {
        this.mContext = context;
        this.mPhotoList = info.getPhotoList();
        this.mActivity = (Activity) context;

        mOnStickerClickListener = (OnStickerClickListener) mActivity;

        initView();


    }

    private void initView() {
//        mActionBar = ((MainActivity) mActivity).findViewById(R.id.);
    }

    @Override
    public int getCount() {
        return mPhotoList == null ? 0 : mPhotoList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.item_image_pager, null);
        assert imageLayout != null;

        final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.loading);
        PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.photoview);
        final ImageView imgPlay = (ImageView) imageLayout.findViewById(R.id.img_play);
        final VideoView videoView = (VideoView) imageLayout.findViewById(R.id.video_view);
        final ImageView imgSticker = (ImageView) imageLayout.findViewById(R.id.img_sticker);

        final ImageView imgDelete = (ImageView) imageLayout.findViewById(R.id.img_delete);


//		photoView.setZoomable(false);
//		photoView.setClickable(false);


        PhotoInfo pInfo = mPhotoList.get(position);
        final String uri = pInfo.getImageURI();
        String thumbFromCP = pInfo.getThumbnailPath();

        final String path = pInfo.getImagePath();


        if (isVideoFile(uri)) {
            videoView.setVisibility(View.VISIBLE);
            imgPlay.setVisibility(View.VISIBLE);
            imgPlay.setImageResource(R.drawable.ic_play);
            photoView.setVisibility(View.GONE);
            imgSticker.setVisibility(View.GONE);

//            videoView.setBackgroundDrawable(Drawable.createFromPath(createVideoThumbnail(uri)));
            videoView.setVideoPath(uri);


            Log.d("VV", "thumbFromCP >> " + thumbFromCP);
            videoView.setBackgroundDrawable(Drawable.createFromPath(createVideoThumbnail(thumbFromCP, uri)));

            imgPlay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;

                        case MotionEvent.ACTION_UP:

                            if (!isPlay) {
                                videoView.setBackgroundColor(Color.parseColor("#00000000"));
                                videoView.start();
                                imgPlay.setImageResource(R.drawable.ic_pause);
                                imgPlay.setVisibility(View.GONE);
                                isShowPlayBtn = false;
                                isPlay = true;

                            } else {
                                imgPlay.setImageResource(R.drawable.ic_play);
                                videoView.pause();
                                isPlay = false;
                            }
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            return false;
                    }


                    return false;
                }
            });

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            Log.d("ViewPagerAdapter", "ACTION_DOWN");
                            return true;

                        case MotionEvent.ACTION_UP:
                            Log.d("ViewPagerAdapter", "ACTION_UP");
                            Log.d("ViewPagerAdapter", "isShowPlayBtn:" + isShowPlayBtn);
                            if (isShowPlayBtn) {
                                imgPlay.setVisibility(View.GONE);
                            } else {
                                imgPlay.setVisibility(View.VISIBLE);
                            }
                            isShowPlayBtn = !isShowPlayBtn;


                            return true;

                        case MotionEvent.ACTION_MOVE:
                            return false;
                    }
                    return false;
                }
            });

            videoView.requestFocus();

        } else {
            imgPlay.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            photoView.setVisibility(View.VISIBLE);


            UniversalImageLoader.displayImage(
                    uri,
                    photoView,
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });


            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

                @Override
                public void onPhotoTap(View arg0, float arg1, float arg2) {
                    if (showToolbar) {
                        Log.d("ViewPagerAdapter", "show tool bar");
                    } else {
                        Log.d("ViewPagerAdapter", "hide tool bar");
                    }
                    showToolbar = !showToolbar;
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });

//            photoView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            return true;
//
//                        case MotionEvent.ACTION_UP:
//                            if (showToolbar) {
//                                Log.d("ViewPagerAdapter", "show tool bar");
//                                imgSticker.setVisibility(View.VISIBLE);
//
//                            } else {
//                                Log.d("ViewPagerAdapter", "hide tool bar");
////                                hideEditView();
//                                imgSticker.setVisibility(View.GONE);
//                            }
//                            showToolbar = !showToolbar;
//                            return true;
//                    }
//
//                    return false;
//                }
//            });

            imgSticker.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;

                        case MotionEvent.ACTION_UP:
                            mOnStickerClickListener.onStickerClickListener(uri);


                    }

                    return false;
                }
            });


        }

        imgDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_UP:
                        mOnStickerClickListener.onDeleteTouchListener(path);
                        Log.d("ViewPagerAdapter", "delete button touch . ");
                        notifyDataSetChanged();
                        return true;


                }

                return false;
            }
        });


        container.addView(imageLayout);


        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


    private static boolean isVideoFile(String sourcePath) {
        String type = sourcePath.substring(sourcePath.length() - 4, sourcePath.length());
        return type.equals(".mp4") ? true : false;

    }

    private static final String videoThumbUri = "/storage/emulated/0/DCIM/.thumbnails/";

    private String createVideoThumbnail(String thumbFromCP, String videoPath) {

        String thumbPath = videoThumbUri + videoPath.substring(39, videoPath.length() - 4) + ".jpg";

        return thumbFromCP == null ? thumbPath : thumbFromCP;
    }


    private void DeleteImage(String imgPath) {
        ContentResolver resolver = mActivity.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = mActivity.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }

        if (result) {
            mPhotoList.remove(imgPath);
            notifyDataSetChanged();
            UniversalImageLoader.clearMemoryCache();
            Log.d("ViewPagerAdapter", "delete success ");
        }
    }
}
