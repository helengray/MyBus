package com.xm.bus.common.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xm.bus.common.Constant;
import com.xm.bus.common.MyApplication;
import com.xm.bus.common.utils.WebService;
import com.xm.bus.search.model.ArrivalInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HtmlBaseParse {
	private  Document doc;
	private  ArrivalInfo arrivalInfo;
	private static HtmlBaseParse instance;
	private static MyApplication app;
	public  enum STATE{
		InputError,
		LineNotExistError,
		NetworkError,
		Success,
		ServerMaintenance
	}
	private HtmlBaseParse(){}
	
	public synchronized static HtmlBaseParse  getInstacne(Context context){
		if(instance==null){
			instance=new HtmlBaseParse();
		}
		app=(MyApplication) context.getApplicationContext();
		return instance;
	}
	/**
	 * 
	 * 2013-11-23 ????7:37:17
	 * @return
	 * ArrivalInfo
	 * TODO ???????????????
	 */
	public  STATE getArrivalInfo(String url){
		arrivalInfo=new ArrivalInfo();
		try {
			doc=Jsoup.connect(url).get();
			Elements elements=doc.select("div.cmode");
			//
			Element currStop=elements.select("div.tl font span").get(0);
			arrivalInfo.setCurrentStation(currStop.text());
			//
			Element buspic=elements.select("img[src *=buspic]").get(0);
			byte[] b=WebService.getImage(Constant.BASE_URL+buspic.attr("src"));
			Bitmap image=BitmapFactory.decodeByteArray(b, 0, b.length);
			image=WebService.zoomImage(image, app.getWidth(), app.getHeight());
			arrivalInfo.setPic(image);
			//
			Elements nextTimeNodes=elements.select("img[src *=next]");
			Elements nodes=elements.select("font[color *=#0000FF]");
			//
			arrivalInfo.setCurrLine(nodes.get(0).text());
			//
			if(nextTimeNodes.size()!=0){
				Element nextTime=nextTimeNodes.get(0);
				arrivalInfo.setNextTime(nextTime.nextSibling().toString().replace("&nbsp;", ""));
				if(nodes.size()==1){//?????????????????
					arrivalInfo.setStopsNum("");
					arrivalInfo.setKilometers("");
				}else{
					//????????????
					arrivalInfo.setStopsNum(nodes.get(1).text());
					//????????????????
					arrivalInfo.setKilometers(nodes.get(2).text());
				}
			}else{//????????????
				arrivalInfo.setNextTime("");
				if(nodes.size()>1){
				//????????????
				arrivalInfo.setStopsNum(nodes.get(1).text());
				//????????????????
				arrivalInfo.setKilometers(nodes.get(2).text());
				}else{
					arrivalInfo.setStopsNum("0");
					arrivalInfo.setKilometers("0");
				}
			}
			return STATE.Success;
		} catch (Exception e) {
			//e.printStackTrace();
			if(e.getClass().getName().equals("java.net.UnknownHostException")){
				//?????????
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}

	public ArrivalInfo getArrivalInfo() {
		return arrivalInfo;
	}

	public void setArrivalInfo(ArrivalInfo arrivalInfo) {
		this.arrivalInfo = arrivalInfo;
	}
	
	

	
}