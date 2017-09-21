package com.phone.adapter;

/**
 * Created by guohongcheng on 2017/8/30.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.phone.R;

import java.util.ArrayList;
import java.util.List;

public class GridStickerAdapter extends BaseAdapter {
    private Context context;

    private List<Picture> pictures = new ArrayList<Picture>();

    public GridStickerAdapter(Integer[] images, Context context) {
        super();
        this.context = context;


        for (int i = 0; i < images.length; i++) {
            Picture picture = new Picture(images[i]);
            pictures.add(picture);
        }

    }

    @Override
    public int getCount() {

        if (null != pictures) {
            return pictures.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {

        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            // 获得容器
            convertView = LayoutInflater.from(this.context).inflate(R.layout.grid_choose_sticker_item, null);

            // 初始化组件
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            // 给converHolder附加一个对象
            convertView.setTag(viewHolder);
        } else {
            // 取得converHolder附加的对象
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 给组件设置资源
        Picture picture = pictures.get(position);
        viewHolder.image.setImageResource(picture.getImageId());

        return convertView;
    }

    class ViewHolder {
        public ImageView image;
    }

    class Picture {

        private int imageId;

        public Picture(Integer imageId) {
            this.imageId = imageId;
        }


        public int getImageId() {
            return imageId;
        }

    }
}

