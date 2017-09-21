package com.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phone.entity.AlbumInfo;
import com.phone.R;
import com.phone.entity.PhotoInfo;
import com.phone.widget.RotateImageViewAware;
import com.phone.util.ThumbnailsUtil;
import com.phone.util.UniversalImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 相册列表适配器
 *
 * @author ghc
 */
public class AlbumAdapter extends BaseAdapter {

    private List<AlbumInfo> mList;
    private ViewHolder mHolder;
    private Context mContext;
    private String mPhotoCountFormat;

    public AlbumAdapter(Context context) {
        super();
        this.mContext = context;
        mPhotoCountFormat = mContext.getString(R.string.album_count);
    }

    @Override
    public int getCount() {
        return (mList == null ? 0 : mList.size());
    }

    @Override
    public Object getItem(int position) {
        return (mList == null ? null : mList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image_list, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else
            mHolder = (ViewHolder) convertView.getTag();

        AlbumInfo aInfo = mList.get(position);
        PhotoInfo pInfo = aInfo.getPhotoList().get(0);
        UniversalImageLoader.displayLocalImage(ThumbnailsUtil.MapgetHashValue(pInfo.getImageURI(), pInfo.getImageURI()),
                new RotateImageViewAware(mHolder.iv_album, pInfo.getImagePath()));

        mHolder.tv_name.setText(aInfo.getAlbumName());

        String sSize = String.format(mPhotoCountFormat, aInfo.getPhotoList().size());
        mHolder.tv_count.setText(sSize);

        return convertView;
    }

    public List<AlbumInfo> getList() {
        return mList;
    }

    public void setList(List<AlbumInfo> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @BindView(R.id.album_iv)
        ImageView iv_album;

        @BindView(R.id.album_name_tv)
        TextView tv_name;

        @BindView(R.id.album_count_tv)
        TextView tv_count;

        ViewHolder(View view) {
//            this.iv_album = view.bin;
            ButterKnife.bind(this, view);
        }
    }
}
