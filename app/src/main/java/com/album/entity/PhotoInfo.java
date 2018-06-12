package com.album.entity;

/**
 * 图片对象实体类的封装
 *
 * @author ghc
 */
public class PhotoInfo {

    public boolean isSelected = false;
    public boolean isOriginal = false;

    /**
     * @brief the image data id
     */
    private String mID = "";

    /**
     * @brief "/storage/sdcard0/..."
     */
    private String mThumbnailPath = "";


    /**
     * @brief "/storage/sdcard0/..."
     */
    private String mImagePath = "";

    /**
     * @brief "file:///storage/sdcard0/..."
     */
    private String mImageURI = "";

    private String mDateModify = "";

    //setter and getter

    public String getImageID() {
        return mID;
    }

    public void setImageID(String id) {
        this.mID = id;
    }

    /**
     * @brief "/storage/sdcard0/..."
     */
    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.mThumbnailPath = thumbnailPath;
    }

    /**
     * @brief "/storage/sdcard0/..."
     */
    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String path) {
        this.mImagePath = path;
    }

    /**
     * @brief "file:///storage/sdcard0/..."
     */
    public String getImageURI() {
        return mImageURI;
    }

    public void setImageURI(String uri) {
        this.mImageURI = uri;
    }

    public void setDateModify(String date) {
        this.mDateModify = date;
    }

    public String getDateModify() {
        return mDateModify;
    }
}
