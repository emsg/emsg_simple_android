package com.vurtnewk.emsg.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageBase64 {

	static int i = 0;
	static String newpath;

	public static String base64StringToImage(String base64String) {
		String path = Environment.getExternalStorageDirectory()+"/carpooling/photo/" + i + ".jpg";
		makeRootDirectory(Environment.getExternalStorageDirectory()+"/carpooling/photo/");
		try {
			byte[] bytes1 = Base64.decode(base64String, Base64.DEFAULT);
			File file = new File(path);
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytes1);
			out.close();
			i++;
			try {
				newpath = BitmapTools.getThumbUploadPath(path + "@path", 480);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file.delete();
			return newpath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return null;
		return null;
	}

	public static String getImageBinary(String path) {
		try {
			path=BitmapTools.getThumbUploadPath(path + "@path", 480);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File file = new File(path);
		FileInputStream inputFile;
		try {
			inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {

		}
	}

	public static String bitmaptoString(Bitmap bitmap) {



		// 将Bitmap转换成字符串

		String string = null;

		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);

		byte[] bytes = bStream.toByteArray();

		string = Base64.encodeToString(bytes, Base64.DEFAULT);

		return string;

	}

	public  static Bitmap stringtoBitmap(String string) {

		// 将字符串转换成Bitmap类型

		Bitmap bitmap = null;

		try {

			byte[] bitmapArray;

			bitmapArray = Base64.decode(string, Base64.DEFAULT);

			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,

					bitmapArray.length);

		} catch (Exception e) {

			e.printStackTrace();

		}



		return bitmap;

	}
}