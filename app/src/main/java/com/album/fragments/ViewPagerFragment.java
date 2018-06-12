package com.album.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.album.Constants;
import com.album.R;
import com.album.MainActivity;
import com.album.adapter.ViewPagerAdapter;
import com.album.entity.AlbumInfo;
import com.album.widget.ViewPagerFixed;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 展示图片大图的Fragment
 *
 * @author ghc
 */
public class ViewPagerFragment extends BaseFragment implements OnPageChangeListener {

    @BindView(R.id.view_pager)
    ViewPagerFixed mViewPager;

/*    @BindView(R.id.send_image_btn1)
    Button mSendBtn;*/

    private AlbumInfo mAlbumInfo;
    private ViewPagerAdapter mAdapter;

//    private ActionBar mActionBar;
//    private ImageView mActionBarSelectIv;
//    private View mMenuItemView;

    private MainActivity mActivity;
    private Context mContext;

    private int mCurPosition = 0;
    private String mToastFormat;
    private int mMaxCount = Constants.MAX_SELECT_COUNT;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity().getApplicationContext(),
                            String.format(mToastFormat, mMaxCount), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    public void setInfo(AlbumInfo info, int position) {
        this.mAlbumInfo = info;
        this.mCurPosition = position;
    }

    /*public int getSelectedCount() {
        int selectedCount = 0;
        for (int i = 0; i < mAlbumInfo.getPhotoList().size(); i++) {
            if (mAlbumInfo.getPhotoList().get(i).isSelected) {
                selectedCount++;
            }
        }
        return selectedCount;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_viewpager, container, false);
        mActivity = (MainActivity) getActivity();
        mContext = getActivity().getApplicationContext();
        ButterKnife.bind(this, mFragment);
        initView();
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity != null) {
            mToastFormat = mActivity.getApplicationContext().getString(R.string.toast_max_count);
            mAdapter = new ViewPagerAdapter(mActivity, mAlbumInfo);
            mViewPager.setAdapter(mAdapter);
        }
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageSelected(mCurPosition);
    }

    @Override
    public void initView() {
//        mActionBar = mActivity.getSupportActionBar();
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mMenuItemView = getIconMenuItem(Constants.TAG_MENU_SELECT, R.drawable.gou_normal, mOnClickListener);
//        int count = getSelectedCount();
//        mSendBtn.setText(getString(R.string.button_ok) + " (" + count + ")");
    }

/*    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (Integer) v.getTag();
            switch (tag) {
                case Constants.TAG_MENU_SELECT:
                    List<PhotoInfo> photoList = mAlbumInfo.getPhotoList();
                    PhotoInfo pInfo = photoList.get(mCurPosition);
                    int selectedCount = getSelectedCount();
                    if (selectedCount < mMaxCount) {
                        pInfo.isSelected = !pInfo.isSelected;

                        if (pInfo.isSelected) {
                            mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
                            selectedCount++;
                        } else {
                            mActionBarSelectIv.setImageResource(R.drawable.gou_normal);
                            selectedCount--;
                        }
                    } else if (selectedCount >= mMaxCount) {
                        if (pInfo.isSelected == true) {
                            pInfo.isSelected = !pInfo.isSelected;
                            mActionBarSelectIv.setImageResource(R.drawable.gou_normal);
                            selectedCount--;
                        } else {
                            Message message = Message.obtain(mHandler, 0);
                            message.sendToTarget();

//						for (int i = 0; i < photoList.size(); i++) {
//							if (photoList.get(i).isSelected) {
//								photoList.get(i).isSelected = false;
//								break;
//							}
//						}
//						
//						pInfo.isSelected = !pInfo.isSelected;
//						mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
                        }
                    }
                    invalidate();

                    break;

                default:
                    break;
            }
        }
    };*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MenuItem recordItem = menu.add(getString(R.string.action_menu_select));
//        setActionViewAlways(recordItem, mMenuItemView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * 设置图片MenuItem
     *
     * @param tag      点击事件标记
     * @param resId    显示图片资源id
     * @param listener 点击事件
     * @return
     */
/*    public View getIconMenuItem(int tag, int resId, OnClickListener listener) {
        View view = View.inflate(mContext, R.layout.actionbar_menu_item_view, null);
        mActionBarSelectIv = (ImageView) view.findViewById(R.id.icon);
        mActionBarSelectIv.setImageResource(resId);
        setViewBackground(view, tag, listener);
        return view;
    }*/

    /**
     * 设置MenuItem一直显示
     *
     * @param item
     */
/*    public void setActionViewAlways(MenuItem item, View view) {
        MenuItemCompat.setActionView(item, view);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    public void setViewBackground(View view, int tag, OnClickListener listener) {
        view.setBackgroundResource(R.drawable.actionbar_menu_selector);
        view.setMinimumWidth(MainActivity.iAcionWidth);
        view.setMinimumHeight(MainActivity.iActionHeight);
        view.setTag(tag);
        view.setOnClickListener(listener);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ViewPagerFragment fragment = (ViewPagerFragment) fm.findFragmentByTag(getTag());
                if (fragment == null) return false;
                ((MainActivity) getActivity()).removeFragment(fragment);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initEvent() {
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(mCurPosition);
//        mActionBar.setTitle(mCurPosition + 1 + "/" + mAdapter.getCount());

        /*mSendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(Constants.EXTRA_SELECTED_FILE_PATH, (ArrayList<String>) getSelectedPhoto());
                ((MainActivity) getActivity()).setResult(Activity.RESULT_OK, intent);
                ((MainActivity) getActivity()).finish();
            }
        });*/
    }

    @Override
    public void invalidate() {
        mAdapter.notifyDataSetChanged();
//        int count = getSelectedCount();
//        mSendBtn.setText(getString(R.string.button_ok) + " (" + count + ")");
//		if (count == 0) {
//			mSendBtn.setEnabled(false);
//			mSendBtn.setTextColor(Color.GRAY);
//		} else {
//			mSendBtn.setEnabled(true);
//			mSendBtn.setTextColor(Color.WHITE);
//		}
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurPosition = position;
//        mActionBar.setTitle(mCurPosition + 1 + "/" + mAdapter.getCount());
/*        PhotoInfo pInfo = mAlbumInfo.getPhotoList().get(position);

        if (pInfo.isSelected) {
            mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
        } else {
            mActionBarSelectIv.setImageResource(R.drawable.gou_normal);
        }*/
    }

    /**
     * 返回选取的单个图片
     */
/*
    public String getSelectedPhotoPath() {
        List<PhotoInfo> pInfos = mAlbumInfo.getPhotoList();
        for (int i = 0; i < pInfos.size(); i++) {
            PhotoInfo pInfo = pInfos.get(i);
            if (pInfo.isSelected) {
                return pInfo.getImageURI();
            }
        }
        return pInfos.get(mCurPosition).getImageURI();
    }
*/

    /**
     * 返回选取的多个图片
     */
/*    public List<String> getSelectedPhoto() {
        ArrayList<String> selectedList = new ArrayList<String>();
        List<PhotoInfo> pInfos = mAlbumInfo.getPhotoList();
        for (int i = 0; i < pInfos.size(); i++) {
            PhotoInfo pInfo = pInfos.get(i);
            if (pInfo.isSelected) {
                selectedList.add(pInfo.getImageURI());
            }
        }
        if (selectedList.size() == 0) {
            selectedList.add(pInfos.get(mCurPosition).getImageURI());
        }
        return selectedList;
    }*/

/*    public String getFileSize(String filePath) {
        File file = new File(filePath);
        return FormatFileSize(file.length());
    }*/

    //get file formated size, example: xxxB,xxxKB,xxxMB,xxxGB
    public String FormatFileSize(long filesize) {
        String sizeStr = null;
        float sizeFloat = 0;
        if (filesize < 1024) {
            sizeStr = Long.toString(filesize);
            sizeStr += "B";
        } else if (filesize < (1 << 20)) {
            sizeFloat = (float) filesize / (1 << 10);
            sizeFloat = (float) (Math.round(sizeFloat * 100)) / 100;
            sizeStr = Float.toString(sizeFloat) + "KB";
        } else if (filesize < (1 << 30)) {
            sizeFloat = (float) filesize / (1 << 20);
            sizeFloat = (float) (Math.round(sizeFloat * 100)) / 100;
            sizeStr = Float.toString(sizeFloat) + "MB";
        } else {
            sizeFloat = (float) filesize / (1 << 30);
            sizeFloat = (float) (Math.round(sizeFloat * 100)) / 100;
            sizeStr = Float.toString(sizeFloat) + "GB";
        }
        return sizeStr;
    }
}
