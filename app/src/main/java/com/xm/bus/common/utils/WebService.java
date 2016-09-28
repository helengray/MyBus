package com.xm.bus.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class WebService {
	/**
	 * �������ϻ�ȡ����ͼƬ����ʾ
	 * @param path
	 * @return
	 */
	public static byte[] getImage(String path) {
		URL url = null;
		byte[] b = null;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(3000);
			InputStream is = conn.getInputStream();
			b = readInputStream(is);
			if(is!=null){
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * ͨ���������õ��ֽ�����
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream is) throws Exception {
		int temp = 0;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((temp = is.read(buffer)) != -1) {
			out.write(buffer, 0, temp);
		}
		out.close();
		is.close();
		return out.toByteArray();
	}
	/**
	 * 
	 * @param b
	 * @param newWidth
	 * @param newHeight
	 * @return ͼƬ����
	 */
	public static Bitmap zoomImage(Bitmap b,int newWidth,int newHeight){
		int h=b.getHeight();
		int w=b.getWidth();
		float scaleWidth = newWidth/w;
		float scaleHeight=newHeight/h;
		Matrix matrix=new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);//�߶����ŵı����������ű���һ��
		Bitmap bitmap=Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
		return bitmap;
	}
}