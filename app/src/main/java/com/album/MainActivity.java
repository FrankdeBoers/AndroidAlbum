package com.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;

import com.album.adapter.ViewPagerAdapter;
import com.album.entity.AlbumInfo;
import com.album.entity.PhotoInfo;
import com.album.fragments.AlbumFragment;
import com.album.fragments.AlbumFragment.OnAlbumClickListener;
import com.album.fragments.PhotoFragment;
import com.album.fragments.PhotoFragment.OnGridClickListener;
import com.album.fragments.ViewPagerFragment;
import com.album.stickview.DeleteActivity;
import com.album.stickview.StickerViewActivity;
import com.album.util.CheckImageLoaderConfiguration;
import com.album.util.UniversalImageLoader;

import java.io.IOException;
import java.util.List;

/**
 * 选择图片的界面，继承自ActionBarActivity，为了与主工程的主题保持一致。<br>
 * 实现了两个自定义接口，接收列表中条目被点击的响应事件
 *
 * @author ghc
 */
public class MainActivity extends AppCompatActivity
        implements OnAlbumClickListener, OnGridClickListener, ViewPagerAdapter.OnStickerClickListener {

    private AlbumFragment mAlbumFragment;
    private PhotoFragment mPhotoFragment;
    private ViewPagerFragment mPagerFragment;
    //    private EditPhotoFragment mEditPhotoFragment;
    private FragmentManager mFragmentManager;
    private int mEditTag = 0;

    public final static String EDIT_PIC_PATH = "com.select.pic.edit";

    /**
     * actionItem的宽度
     */
    public static int iAcionWidth = 0;
    /**
     * actionItem的高度
     */
    public static int iActionHeight = 0;
    public static String COUNT = "count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "SPA onCreate() >> ");

        Bundle savedInstanceStates = null;
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.select_photo_main);

        if (savedInstanceState == null && mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }


        initData();
        initView();

        getActionDimen();

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        UniversalImageLoader.clearMemoryCache();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        if (getIntent().hasExtra(COUNT)) {
            Constants.MAX_SELECT_COUNT = getIntent().getIntExtra(COUNT, 1);
        }

        if (getIntent().hasExtra("edit_photo")) {
            mEditTag = getIntent().getIntExtra("edit_photo", 0);
        }

        int i = getIntent().getIntExtra("sticker", 0);
        if (i == 1) {

        }
    }

    private void initView() {
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            mAlbumFragment = (AlbumFragment) mFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT_ALBUM);
            if (mAlbumFragment == null) {
                mAlbumFragment = new AlbumFragment();
                transaction.add(R.id.selectphoto_content, mAlbumFragment, Constants.TAG_FRAGMENT_ALBUM);
            } else {
                mAlbumFragment.invalidate();
                transaction.show(mAlbumFragment);
            }

            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPhotoFragment != null) {
            mPhotoFragment.invalidate();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setFullScreen(false);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * GridView的Item点击的事件响应--图片列表的点击事件
     */
    @Override
    public void onGridItemClick(AlbumInfo albumInfo, final int position) {
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mPhotoFragment);

            if (mEditTag == 0) {
                mPagerFragment = (ViewPagerFragment) mFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT_PAGER);
                if (mPagerFragment == null) {
                    mPagerFragment = new ViewPagerFragment();
                    mPagerFragment.setInfo(albumInfo, position);

                    transaction.add(R.id.selectphoto_content, mPagerFragment, Constants.TAG_FRAGMENT_PAGER);
                    transaction.addToBackStack(null);
                } else {
                    mPagerFragment.setInfo(albumInfo, position);
                    transaction.show(mPagerFragment);
                }
            } /*else {
                mEditPhotoFragment = (EditPhotoFragment) mFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT_PAGER);
                String uri = albumInfo.getPhotoList().get(position).getImageURI();
                if (mEditPhotoFragment == null) {
                    mEditPhotoFragment = new EditPhotoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("image-uri", uri);
                    mEditPhotoFragment.setArguments(bundle);
                    transaction.add(R.id.selectphoto_content, mEditPhotoFragment, Constants.TAG_FRAGMENT_EDIT);
                    transaction.addToBackStack(null);
                } else {
                    transaction.show(mEditPhotoFragment);
                }
            }*/

            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * ListView的Item点击的事件响应--相册列表的点击事件
     */
    @Override
    public void onListClick(AlbumInfo albumInfo) {
        if (albumInfo != null) {
            if (mFragmentManager != null && resetDataStatus(albumInfo)) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.hide(mAlbumFragment);

                mPhotoFragment = (PhotoFragment) mFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT_PHOTO);
                if (mPhotoFragment == null) {
                    mPhotoFragment = new PhotoFragment();
                    mPhotoFragment.setInfo(albumInfo);
                    mPhotoFragment.setEditTag(mEditTag);

                    transaction.add(R.id.selectphoto_content, mPhotoFragment, Constants.TAG_FRAGMENT_PHOTO);
                } else {
                    //添加贴纸后，通知更新
                    mPhotoFragment.invalidate();
                    mPhotoFragment.setInfo(albumInfo);
                    transaction.show(mPhotoFragment);
                }

                transaction.commitAllowingStateLoss();
            }
        }
    }

    public boolean removeFragment(Fragment fragment) {
        if (null == fragment) return false;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().popBackStack();
        if (fragment instanceof ViewPagerFragment) {
            mPhotoFragment.invalidate();
        }
        return true;
    }

    public void setFullScreen(boolean noTitle) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (noTitle) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private boolean resetDataStatus(AlbumInfo aInfo) {
        List<PhotoInfo> pInfos = aInfo.getPhotoList();
        for (int i = 0; i < pInfos.size(); i++) {
            pInfos.get(i).isSelected = false;
            pInfos.get(i).isOriginal = false;
        }
        return true;
    }

    public void getActionDimen() {
        iAcionWidth = getResources().getDimensionPixelSize(R.dimen.DefaultActionbarWidth);
        iActionHeight = getResources().getDimensionPixelSize(R.dimen.DefaultActionbarHeightPort);
    }

    @Override
    public void onStickerClickListener(String path) {
        Intent intent = new Intent(this, StickerViewActivity.class);
        intent.putExtra(EDIT_PIC_PATH, path);
        startActivity(intent);
    }

    @Override
    public void onDeleteTouchListener(String path) {
        Intent intent = new Intent(this, DeleteActivity.class);
        intent.putExtra(EDIT_PIC_PATH, path);
        startActivity(intent);
    }
}
