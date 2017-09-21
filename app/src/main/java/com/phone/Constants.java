package com.phone;

/**
 * 定义了一下静态常量，用来作为传递参数的键
 *
 * @author ghc
 */
public class Constants {
    //extra string
    public static final String EXTRA_SELECTED_FILE_PATH = "SELECTED_IMAGE_PATH";
    public static final String EXTRA_STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_CHAT_DATA_IMAGE = "CHAT_PHOTO_LIST";
    public static final String EXTRA_CHAT_DATA_POSITION = "CHAT_PHOTO_POSITION";

    //fragment tag
    public static final String TAG_FRAGMENT_ALBUM = "ALBUM_LIST";
    public static final String TAG_FRAGMENT_PHOTO = "PHOTO_GRID";
    public static final String TAG_FRAGMENT_PAGER = "VIEW_PAGER";
    public static final String TAG_FRAGMENT_EDIT = "VIEW_EDIT";

    //others
    public static int MAX_SELECT_COUNT = 9;

    //actionbar menu
    public static final int TAG_MENU_SELECT = 10;
}
