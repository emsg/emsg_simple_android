package com.vurtnewk.emsg.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapTools {

    public static String getThumbUploadPath(String oldPath, int bitmapMaxWidth)
            throws Exception {
        if (!oldPath.contains("@")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(oldPath, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int reqHeight = 0;
            int reqWidth = bitmapMaxWidth;
            reqHeight = (reqWidth * height) / width;
            // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
            options.inSampleSize = calculateInSampleSize(options,
                    bitmapMaxWidth, reqHeight);
            // System.out.println("calculateInSampleSize(options, 480, 800);==="
            // + calculateInSampleSize(options, 480, 800));
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
            // Log.e("asdasdas",
            // "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);
            Bitmap bmp = compressImage(Bitmap.createScaledBitmap(bitmap,
                    bitmapMaxWidth, reqHeight, false));
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            return saveImg(bmp, timeStamp);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(oldPath.split("@")[0], options);
            int height = options.outHeight;
            int width = options.outWidth;
            int reqHeight = 0;
            int reqWidth = bitmapMaxWidth;
            reqHeight = (reqWidth * height) / width;
            // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
            options.inSampleSize = calculateInSampleSize(options,
                    bitmapMaxWidth, reqHeight);
            // System.out.println("calculateInSampleSize(options, 480, 800);==="
            // + calculateInSampleSize(options, 480, 800));
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(oldPath.split("@")[0], options);
            // Log.e("asdasdas",
            // "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);
            Bitmap bmp = compressImage(Bitmap.createScaledBitmap(bitmap,
                    bitmapMaxWidth, reqHeight, false));
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            return saveImg(bmp, timeStamp + "@pic");
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 4) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 5;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * @param b Bitmap
     * @return 图片存储的位置
     * @throws FileNotFoundException
     */
    public static String saveImg(Bitmap b, String name) throws Exception {
        String path;
        File mediaFile;
        if (name.contains("@")) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/carpooling/photo/";//"mnt/sdcard/pinche/photo/";
            mediaFile = new File(path + getPhotoFileName());
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/carpooling/photo/IGImg/";//"mnt/sdcard/pinche/photo/IGImg/";
            mediaFile = new File(path + getPhotoFileName());
        }
        if (mediaFile.exists()) {
            mediaFile.delete();
        }

        File path_image = new File(path);
        if (!path_image.exists()) {
            System.out.println("创建");
            boolean flag = path_image.mkdirs();
            System.out.println(flag + "flag");
        }
        System.out.println(mediaFile + "______________--");
        mediaFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(mediaFile);
        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        b.recycle();
        b = null;
        System.gc();
        return mediaFile.getPath();
    }

    private static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     * <p/>
     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
     * <p/>
     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
     * <p/>
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     *
     * @param url
     * @return
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 2 * 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[2 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
}
