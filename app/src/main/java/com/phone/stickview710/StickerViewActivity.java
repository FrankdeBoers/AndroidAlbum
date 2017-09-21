package com.phone.stickview710;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phone.R;
import com.phone.SelectPhotoActivity;
import com.phone.stickview710.stickerview.StickerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class StickerViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView cleanTv, nextTv;
    StickerView stickerView;
    //icon link http://www.easyicon.net/iconsearch/iconset:Landscapes-icons/
    int icons[] = new int[]{
            R.drawable.sticker_0, R.drawable.sticker_1, R.drawable.sticker_2, R.drawable.sticker_3
            , R.drawable.sticker_4, R.drawable.sticker_5, R.drawable.sticker_6, R.drawable.sticker_7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticker_activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        cleanTv = (TextView) findViewById(R.id.cleanTv);
        nextTv = (TextView) findViewById(R.id.nextTv);
        stickerView = (StickerView) findViewById(R.id.stickerView);

        String urlPic = getIntent().getStringExtra(SelectPhotoActivity.EDIT_PIC_PATH).substring(7);

        Log.d("StickerViewActivity", "pathPic: " + urlPic);
        stickerView.setImageBitmap(getLoacalBitmap(urlPic));


        stickerView.setMinStickerSizeScale(0.9f);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ImageAdapter imageAdapter = new ImageAdapter();
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                stickerView.addSticker(icons[position]);
            }
        });
        cleanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerView.clearSticker();
            }
        });
        nextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapUtil.FINAL_BITMAP = stickerView.saveSticker();
            }
        });
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> implements View.OnClickListener {

        private OnRecyclerViewItemClickListener mOnItemClickListener = null;

        public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        public ImageAdapter() {
        }

        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sticker_image_item, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(this);
            return vh;
        }

        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.imageView.setImageResource(icons[position]);
            viewHolder.itemView.setTag(position);
        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            return icons.length;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
            }
        }


        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public ViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.imageView);
            }
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }


    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        Bitmap bitmap = null;
        try {
            FileInputStream fis = new FileInputStream(url);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            // 先判断是否已经回收
            if (bitmap != null && !bitmap.isRecycled()) {
                // 回收并且置为null
                bitmap.recycle();
                bitmap = null;
            }
            System.gc();
        }
        if (bitmap == null) {
            // 如果实例化失败 返回默认的Bitmap对象
            Log.d("StickerViewActivity", "bitmap == null");

            return null;
        }
        return bitmap;
    }
}
