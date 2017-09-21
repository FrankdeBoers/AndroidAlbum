package com.phone.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.phone.R;
import com.phone.SelectPhotoActivity;
import com.phone.adapter.AlbumAdapter;
import com.phone.entity.AlbumInfo;
import com.phone.entity.PhotoInfo;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 展示相册列表的Fragment
 *
 * @author ghc
 */
public class AlbumFragment extends BaseFragment {

    @BindView(R.id.album_lv)
    ListView mListView;

    @BindView(R.id.loading_photos_progressBar)
    ProgressBar mProgressBar;

    private List<AlbumInfo> mAlbumList = new ArrayList<AlbumInfo>();
    private AlbumAdapter mAdapter;
    //    private ActionBar mActionBar;
    private Map<String, String> mThumbnailList = new HashMap<String, String>();
    private SelectPhotoActivity mActivity;
    private OnAlbumClickListener mOnAlbumClickListener;

    private Calendar cal = Calendar.getInstance();


    private static final String TAG = "IMAlbumFragment";

    Map<String, AlbumInfo> idMap = new HashMap<String, AlbumInfo>();
    AlbumInfo aInfo = new AlbumInfo();
    List<PhotoInfo> mPhotoList = new ArrayList<PhotoInfo>();

    private static final Uri VIDEO_URI = Uri.parse("content://media/external/video/media");


    public interface OnAlbumClickListener {
        public void onListClick(AlbumInfo albumInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SelectPhotoActivity) activity;

        if (getActivity() instanceof PhotoFragment.OnGridClickListener) {
            mOnAlbumClickListener = (OnAlbumClickListener) getActivity();
        } else if (getParentFragment() instanceof PhotoFragment.OnGridClickListener) {
            mOnAlbumClickListener = (OnAlbumClickListener) getParentFragment();
        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_listview, container, false);
        initView();
        ButterKnife.bind(this, mFragment);
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        AlbumAsync task = new AlbumAsync();
        /**
         * 创建一个没有限制的线程池(Executors.newCachedThreadPool())，并提供给AsyncTask。
         * 这样这个AsyncTask实例就有了自己的线程池而不必使用AsyncTask默认的。
         */
        task.executeOnExecutor(Executors.newCachedThreadPool(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        /*if (!hidden) {
            mActionBar.setTitle(R.string.album_title);
        }*/
    }

    /**
     * 读取媒体资源中缩略图资源，以HashMap的方式保存在mThumbnailList中。
     */
    public void getImageThummnail() {
        if (isAdded()) {
            ContentResolver cr = getActivity().getContentResolver();
            String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA};
            Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, Thumbnails.DATA + " desc ");

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    @SuppressWarnings("unused")
                    int _id;
                    int image_id;
                    String image_path;

                    int _idColumn = cursor.getColumnIndex(Thumbnails._ID);
                    int image_idColumn = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
                    int dataColumn = cursor.getColumnIndex(Thumbnails.DATA);

                    // Get the field values
                    _id = cursor.getInt(_idColumn);
                    image_id = cursor.getInt(image_idColumn);
                    image_path = cursor.getString(dataColumn);

                    mThumbnailList.put("" + image_id, image_path);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 异步构建相册数据
     *
     * @author ghc
     */
    class AlbumAsync extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            getImageThummnail();
            getVideoThumbnail();


            getVideoData();
            getImageData();

            // 实现Video、Image的混合排序
            sortPhotoList(mPhotoList);

            return null;
        }

        /**
         * 获取时间（年月日）
         *
         * @return
         */

        public String getTimeInfo(long time) {
            String[] times = new String[7];
            Date date = new Date(time);
            cal.setTime(date);
            Log.d(TAG, "date = " + date + "cal = " + cal);
            times[0] = cal.get(Calendar.YEAR) + "-";
            times[1] = (cal.get(Calendar.MONTH) + 1) + "-";//calendar月份从0-11
            times[2] = cal.get(Calendar.DAY_OF_MONTH) + "-";
            times[3] = cal.get(Calendar.HOUR_OF_DAY) + "-";
            times[4] = cal.get(Calendar.MINUTE) + "-";
            times[5] = cal.get(Calendar.SECOND) + ";";
//            times[6] = cal.get(Calendar.MILLISECOND) + ";";
            String mDateModify = times[0] + times[1] + times[2] + times[3] + times[4] + times[5];

            return mDateModify;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            if (getActivity() != null && mAlbumList != null) {
                mAdapter = new AlbumAdapter(getActivity());
                mAdapter.setList(mAlbumList);
                mListView.setAdapter(mAdapter);

                // add by frank_20170823 start
                // 进入应用，将不再展示相册列表这个fragment，
                // 而是直接展示第一个相册的图片列表
                if (mAdapter != null) {
                    AlbumInfo aInfo = (AlbumInfo) mAdapter.getItem(0);
                    // ((MainActivity) getActivity()).onListClick(aInfo); also
                    // work well
                    if (mOnAlbumClickListener != null)
                        mOnAlbumClickListener.onListClick(aInfo);
                }
                // add by frank_20170823 end
            }
        }
    }

    @Override
    public void initView() {
        /*mActionBar = mActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.album_title);*/
    }

    @Override
    public void initEvent() {
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {

                    // modify by guohongcheng
                    AlbumInfo aInfo = (AlbumInfo) mAdapter.getItem(0);
                    // ((MainActivity) getActivity()).onListClick(aInfo); also
                    // work well
                    if (mOnAlbumClickListener != null)
                        mOnAlbumClickListener.onListClick(aInfo);
                }
            }
        });

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // if (scrollState == SCROLL_STATE_IDLE)
                // UniversalImageLoadTool.resume();
                // else
                // UniversalImageLoadTool.pause();

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

    }

    @Override
    public void invalidate() {
        mAdapter.notifyDataSetChanged();
    }


    private String sortOrder() {
        return "_id" + " ASC";
    }

    private void getVideoThumbnail() {
        final String[] VIDEO_PROJECTION = new String[]{
                MediaStore.Video.Thumbnails._ID,
                MediaStore.Video.Thumbnails.VIDEO_ID,
                MediaStore.Video.Thumbnails.DATA};
        Cursor cursor = MediaStore.Images.Media.query(getActivity().getContentResolver(),
                MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                null, null, sortOrder());

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressWarnings("unused")
                int _id;
                int image_id;
                String image_path;

                int _idColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails._ID);
                int image_idColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID);
                int dataColumn = cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);


                // Get the field values
                _id = cursor.getInt(_idColumn);
                image_id = cursor.getInt(image_idColumn);
                image_path = cursor.getString(dataColumn);

                Log.d(TAG, "video image_path" + image_id + image_path);


                mThumbnailList.put("" + image_id, image_path);
            }
        }
    }


    // 通过ContentResolver获取视频信息
    // 包括视频的ID、相册名字、视频路径、最近修改时间
    private void getVideoData() {

        final String[] VIDEO_PROJECTION_DATA = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_MODIFIED};

        Cursor cursor = MediaStore.Images.Media.query(getActivity().getContentResolver(),
                VIDEO_URI, VIDEO_PROJECTION_DATA,
                null, null, sortOrder());

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // add by guohongcheng_20170824

                if (cursor.getString(2).equals("Camera")) {
                    Log.d(TAG, "[getVideoData] >> sName == Camera");
                    PhotoInfo pInfo = new PhotoInfo();
                    String s_ID = cursor.getString(0);
                    String s_Buck_ID = cursor.getString(1);
                    String sName = cursor.getString(2);
                    String sPath = cursor.getString(3);

                    pInfo.setImageID(s_ID);
                    pInfo.setThumbnailPath(mThumbnailList.get(s_ID));
                    pInfo.setImagePath(sPath);
                    pInfo.setImageURI("file://" + sPath);

                    File file = new File(sPath);
                    if (file.length() == 0) {
                        continue;
                    }

                    long time = file.lastModified();
                    String mDateModify = formatTimeInfo(time);
                    pInfo.setDateModify(mDateModify);

                    if (idMap.containsKey(s_Buck_ID)) {
                        idMap.get(s_Buck_ID).getPhotoList().add(pInfo);
                    } else {
                        mPhotoList.add(pInfo);
                        aInfo.setAlbumName(sName);
                        aInfo.setPhotoList(mPhotoList);
                        mAlbumList.add(aInfo);

                        idMap.put(s_Buck_ID, aInfo);
                    }
                }
                Log.e(TAG, "[getVideoData] >> sName = " + cursor.getString(2) + " ; not camera folder, skip");
            }
        }
    }

    public String formatTimeInfo(long time) {
        String[] times = new String[7];
        Date date = new Date(time);
        cal.setTime(date);
        times[0] = cal.get(Calendar.YEAR) + "-";
        times[1] = (cal.get(Calendar.MONTH) + 1) + "-";
        times[2] = cal.get(Calendar.DAY_OF_MONTH) + "-";
        times[3] = cal.get(Calendar.HOUR_OF_DAY) + "-";
        times[4] = cal.get(Calendar.MINUTE) + "-";
        times[5] = cal.get(Calendar.SECOND) + "";
        String mDateModify = times[0] + times[1] + times[2] + times[3] + times[4] + times[5];
        Log.d(TAG, "[formatTimeInfo] >>> mDateModify " + mDateModify);
        return mDateModify;
    }


    /***
     * 通过ContentResolver获取iamge的信息，
     * 包括图片ID、所属相册、存储路径等
     * *
     */
    private void getImageData() {
        ContentResolver cr = getActivity().getContentResolver();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc ");


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // add by guohongcheng_20170824
                // 由于监听回调onListClick 返回是position(0)
                // 所以限定，只有Camera相册才会被添加，其他相册将略过
                if (cursor.getString(2).equals("Camera")) {
                    Log.d(TAG, "sName == Camera");
                    PhotoInfo pInfo = new PhotoInfo();
                    String s_ID = cursor.getString(0);
                    String s_Buck_ID = cursor.getString(1);
                    String sName = cursor.getString(2);
                    String sPath = cursor.getString(3);

                    pInfo.setImageID(s_ID);
                    pInfo.setThumbnailPath(mThumbnailList.get(s_ID));
                    pInfo.setImagePath(sPath);
                    pInfo.setImageURI("file://" + sPath);

                    File file = new File(sPath);
                    if (file.length() == 0) {
                        continue;
                    }
                    long time = file.lastModified();
                    String mDateModify = formatTimeInfo(time);

                    pInfo.setDateModify(mDateModify);
                    Log.d(TAG, "sDateModify = " + pInfo.getDateModify());

                    if (idMap.containsKey(s_Buck_ID)) {
                        idMap.get(s_Buck_ID).getPhotoList().add(pInfo);
                    } else {
                        mPhotoList.add(pInfo);

                        aInfo.setAlbumName(sName);
                        aInfo.setPhotoList(mPhotoList);
                        mAlbumList.add(aInfo);

                        idMap.put(s_Buck_ID, aInfo);
                    }
                }
                Log.e(TAG, "sName != Camera,  Camera is null!!");
            }
        }
    }

    private void sortPhotoList(List<PhotoInfo> photoInfos) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        Collections.sort(photoInfos, new Comparator<PhotoInfo>() {
            @Override
            public int compare(PhotoInfo t1, PhotoInfo t2) {
                Date d1 = null;
                Date d2 = null;
                Log.d(TAG, "t1.getDateModify() : " + t1.getDateModify());

                try {
                    d1 = df.parse(t1.getDateModify());
                    d2 = df.parse(t2.getDateModify());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == d1 || null == d2) {
                    return 0;
                }
                return d2.compareTo(d1);
            }

        });

        Date d1 = null;
        for (PhotoInfo photoList : mPhotoList) {
            try {
                d1 = df.parse(photoList.getDateModify());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "[sortPhotoList] >> format date: " + photoList.getDateModify() +
                    " ;  " + d1 +
                    " ;  " + photoList.getImagePath());
        }
    }
}