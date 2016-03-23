package com.vurtnewk.emsgdemo.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;

import com.lidroid.xutils.bitmap.core.BitmapDecoder;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author 蒋洪波
 * @file PicUtils.java
 * @brief 图片工具类，主要处理压缩
 * @date 2015-6-10
 * Copyright (c) 2015, 北京球友圈网络科技有限责任公司
 * All rights reserved.
 */
public class PicUtils {

    /**
     * @param intent
     * @param activity
     * @param reqWidth
     * @param reqHeight
     * @return
     * @brief 压缩为bitmap，filePath和intent2选一
     */
    public static Bitmap compress(final Intent intent, final String filePath, final Activity activity, final int reqWidth, final int reqHeight) {
        Uri uri = null;
        String path = null;
        //获得文件地址
        if (intent != null) {
            uri = intent.getData();
            path = getPath(uri, activity);
        } else if (filePath != null) {
            path = filePath;
        }

        //测出图片大小
        Options options = new Options();
        options.inJustDecodeBounds = true;
        if (path == null)
            return null;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        //计算压缩比例大小，并显示的赋值给OPTION
        int calculateInSampleSize = BitmapDecoder.calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = calculateInSampleSize;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * @param intent
     * @param activity
     * @param reqWidth
     * @param reqHeight
     * @return
     * @brief 压缩到本地文件夹, 请在子线程中调用该方法.filePath和intent 2选一 ，优先使用intent
     */
    public static boolean compressToLocal(final Intent intent, final String filePath, final Activity activity, final int reqWidth, final int reqHeight, final String fileName) {

        Bitmap bitmap = compress(intent, filePath, activity, reqWidth, reqHeight);
        if (bitmap == null)
            return false;

        //准备保存路径
        File sdCard = SettingsConstants.upLoadFileDir;
        if (!SettingsConstants.upLoadFileDir.exists()) {
            boolean mkdirs = SettingsConstants.upLoadFileDir.mkdirs();
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(sdCard + "/" + fileName);
            //先压到内存当中，在把内存中数据转为字节数组通过输出流写入到文件中
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            boolean compress = bitmap.compress(CompressFormat.JPEG, 80, buf);
            byte[] buffer = buf.toByteArray();
            os.write(buffer);
            os.close();
            buf.close();
            if (compress) {
                return true;
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null)
                bitmap.recycle();
        }
        return false;
    }

    /**
     * @param uri
     * @return 返回图片地址
     * @brief 将URI转为本地地址
     */
    public static String getPath(Uri uri, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        if (cursor == null)
            return null;

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
