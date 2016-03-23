package com.vurtnewk.emsg.util;

import java.io.File;
import java.io.FileInputStream;

public class ImageSizeTool {

	public ImageSizeTool() {

	}

	public long getFileSizes(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		} else {
			f.createNewFile();
			System.out.println("文件夹不存在");
		}

		return s;
	}

	/**
	 * 递归
	 * */
	public long getFileSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 转换文件大小
	 * */
	public int FormentFileSize(long fileS) {
		//		DecimalFormat df = new DecimalFormat("#.00");
		int fileSizeString = 0;
		//		if (fileS < 1024) {
		//			fileSizeString = df.format((double) fileS) + "B";
		//		} else if (fileS < 1048576) {
		fileSizeString = (int)((double) fileS / 1024);
		//		} else if (fileS < 1073741824) {
		//			fileSizeString = df.format((double) fileS / 1048576) + "M";
		//		} else {
		//			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		//		}
		return fileSizeString;
	}

}