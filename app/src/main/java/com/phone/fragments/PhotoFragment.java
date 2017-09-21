package com.phone.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.phone.Constants;
import com.phone.R;
import com.phone.SelectPhotoActivity;
import com.phone.adapter.PhotoAdapter;
import com.phone.entity.AlbumInfo;
import com.phone.entity.PhotoInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 展示某一个相册中图片表格的Fragment
 *
 * @author ghc
 */
public class PhotoFragment extends BaseFragment {

    @BindView(R.id.photo_gridview)
    GridView mGridView;

    @BindView(R.id.send_image_btn1)
    Button mSendBtn;

    @BindView(R.id.relativeLayout)
    RelativeLayout mBottomLayout;

    private AlbumInfo mAlbumInfo;
    private PhotoAdapter mAdapter;
    //    private ActionBar mActionBar;
    private OnGridClickListener mOnGridClickListener;
    private SelectPhotoActivity mActivity;
    private int mEditTag = 0;

    public interface OnGridClickListener {
        void onGridItemClick(AlbumInfo albumInfo, int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SelectPhotoActivity) activity;
        if (getActivity() instanceof OnGridClickListener) {
            mOnGridClickListener = (OnGridClickListener) getActivity();
        } else if (getParentFragment() instanceof OnGridClickListener) {
            mOnGridClickListener = (OnGridClickListener) getParentFragment();
        } else {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_gridview, container, false);
        ButterKnife.bind(this, mFragment);
        initView();
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            mAdapter = new PhotoAdapter(getActivity().getApplicationContext(), mAlbumInfo, mEditTag);
            mGridView.setAdapter(mAdapter);
        }
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            mActionBar.setTitle(mAlbumInfo.getAlbumName());
        }
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

    public void setInfo(AlbumInfo info) {
        this.mAlbumInfo = info;
    }

    public void setEditTag(int tag) {
        this.mEditTag = tag;
    }

    @Override
    public void initView() {
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
//        mActionBar = mActivity.getSupportActionBar();
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setTitle(mAlbumInfo.getAlbumName());

        /*if (getSelectedCount() == 0) {
            mSendBtn.setEnabled(false);
        } else {
            mSendBtn.setEnabled(true);
        }*/
    }

    @Override
    public void initEvent() {
        mGridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//				if (scrollState == SCROLL_STATE_IDLE)
//					UniversalImageLoadTool.resume();
//				else
//					UniversalImageLoadTool.pause();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    if (mOnGridClickListener != null) {
                        mOnGridClickListener.onGridItemClick(mAlbumInfo, position);
                    }
                }
            }
        });

        mAdapter.setOnSelectedPhotoChangeListener(new PhotoAdapter.onSelectedPhotoChangeListener() {

            @Override
            public void onChangedListener(List<PhotoInfo> photoList) {
                invalidate();
            }
        });

        mSendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(Constants.EXTRA_SELECTED_FILE_PATH, (ArrayList<String>) getSelectedPhoto());
                ((SelectPhotoActivity) getActivity()).setResult(Activity.RESULT_OK, intent);
                ((SelectPhotoActivity) getActivity()).finish();
            }
        });

        if (mEditTag == 0) {
            mBottomLayout.setVisibility(View.VISIBLE);
        } else {
            mBottomLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PhotoFragment fragment = (PhotoFragment) fm.findFragmentByTag(getTag());
                if (fragment == null) return false;
                ((SelectPhotoActivity) getActivity()).removeFragment(fragment);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invalidate() {
        mAdapter.notifyDataSetChanged();
        /*int count = getSelectedCount();
        mSendBtn.setText(getString(R.string.button_ok) + " (" + count + ")");
        if (count == 0) {
            mSendBtn.setEnabled(false);
        } else {
            mSendBtn.setEnabled(true);
        }*/
    }

    /**
     * 返回选取的单个图片
     */
    public String getSelectedPhotoPath() {
        List<PhotoInfo> pInfos = mAlbumInfo.getPhotoList();
        for (int i = 0; i < pInfos.size(); i++) {
            PhotoInfo pInfo = pInfos.get(i);
            if (pInfo.isSelected) {
                return pInfo.getImageURI();
            }
        }
        return null;
    }

    /**
     * 返回选取的多个图片
     */
    public List<String> getSelectedPhoto() {
        ArrayList<String> selectedList = new ArrayList<String>();
        List<PhotoInfo> pInfos = mAlbumInfo.getPhotoList();
        for (int i = 0; i < pInfos.size(); i++) {
            PhotoInfo pInfo = pInfos.get(i);
            if (pInfo.isSelected) {
                selectedList.add(pInfo.getImageURI());
            }
        }

        return selectedList;
    }

    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    /*&& event.getAction() == KeyEvent.ACTION_DOWN*/) {
                // ToDo

                getActivity().finish();
                System.exit(0);
                return true;
            }
            return false;
        }
    };
}
