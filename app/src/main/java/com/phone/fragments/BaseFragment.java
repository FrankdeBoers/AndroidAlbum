package com.phone.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 自定义Fragment的基类，定义了一些初始化方法和mFragment
 *
 * @author ghc
 */
public abstract class BaseFragment extends Fragment {

    public abstract void initView();

    public abstract void initEvent();

    public abstract void invalidate();

    protected View mFragment;
}
