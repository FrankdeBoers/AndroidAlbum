package com.album.stickview;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.album.MainActivity;
import com.album.R;
import com.album.util.UniversalImageLoader;

import java.io.File;

/**
 * Created by guohongcheng on 2017/8/31.
 */

public class DeleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.delete_layout);


        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = 550;
        lp.height = 550;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    public void confirm_delete(View view) {
        String pathDelete = getIntent().getStringExtra(MainActivity.EDIT_PIC_PATH);
        DeleteImage(pathDelete);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Intent intent = new Intent(DeleteActivity.this, MainActivity.class);
                startActivity(intent);
                return false;
            }
        }).sendEmptyMessageDelayed(0, 500);
    }

    public void cancel_delete(View view) {
        this.finish();
    }

    private void DeleteImage(String imgPath) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }

        if (result) {
            /*mPhotoList.remove(imgPath);
            notifyDataSetChanged();*/
            UniversalImageLoader.clearMemoryCache();
            Log.d("ViewPagerAdapter", "delete success ");
        }
    }
}
