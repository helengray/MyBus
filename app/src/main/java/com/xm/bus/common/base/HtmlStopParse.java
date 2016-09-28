package com.xm.bus.common.base;

import android.content.Context;

import com.xm.bus.common.Constant;
import com.xm.bus.common.base.HtmlBaseParse.STATE;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlStopParse {
	private Document doc;
	private static HtmlStopParse instance;
	private  List<Map<String, String>> relationStopList;
	private  List<Map<String, String>> relationLineList;
	private static Context c;
	private HtmlStopParse(){}
	
	public static synchronized HtmlStopParse getInstance(Context context){
		if(instance==null){
			instance=new HtmlStopParse();
		}
		c=context;
		return instance;
	}
	/**
	 */
	public  STATE getRelationStop(String stopName) {
		relationStopList = new ArrayList<Map<String, String>>();
		if(stopName.equals("")){
			return STATE.InputError;
		}
		try {
			String name = URLEncoder.encode(stopName, "utf-8");
			doc = Jsoup.connect(Constant.STOP_URL + name).get();
			if (doc.select("div.error").size() == 0) {
				Elements nodes = doc.select("div.cmode");
				/*titleInfo1 = nodes.select("div.tl font").get(0).nextSibling()
						.toString().trim();*/
				Elements nextpages;
				boolean isNext = false;
				do {
					Elements lines = nodes.select("a[href *=" + stopName + "]");
					for (Element node : lines) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("relationStopName", node.text());
						map.put("relationStopUrl", node.attr("href"));
						relationStopList.add(map);
					}
					nextpages = nodes.select("a[href *=query]");
					if (nextpages.size() != 0
							&& nextpages.get(0).text().equals("下一页")) {
						Element nextpage = nextpages.get(0);
						doc = Jsoup.connect(
								Constant.BASE_URL + nextpage.attr("href"))
								.get();
						nodes = doc.select("div.cmode");
						isNext = true;
					} else {
						isNext = false;
					}
				} while (isNext);
				return STATE.Success;
			} else {
				return STATE.LineNotExistError;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			if (e.getClass().getName().equals("java.net.UnknownHostException")
					|| e.getClass().getName()
							.equals("java.net.SocketException")) {
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}

	public  STATE getRelationLine(Map<String, String> relationStopMap) {
		relationLineList = new ArrayList<Map<String, String>>();
		try {
			String url = relationStopMap.get("relationStopUrl").replace(
					relationStopMap.get("relationStopName"),
					URLEncoder.encode(relationStopMap.get("relationStopName"),
							"utf-8"));
			doc = Jsoup.connect(Constant.BASE_URL + url).get();
			Elements nodes = doc.select("div.cmode");
			/*titleInfo2 = nodes.select("div.tl").get(0).text();*/
			Elements nextpages;
			boolean isNext = false;
			do {
				Elements lines = nodes.select("a[href *=nextbus]");
				for (Element line : lines) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("relationLineName", line.text());
					map.put("relationLineUrl", line.attr("href"));
					relationLineList.add(map);
				}
				nextpages = nodes.select("a[href *=stoptoline]");
				if (nextpages.size() != 0
						&& nextpages.get(0).text().equals("下一页")) {
					Element nextpage = nextpages.get(0);
					doc = Jsoup.connect(
							Constant.BASE_URL + nextpage.attr("href")).get();
					nodes = doc.select("div.cmode");
					isNext = true;
				} else {
					isNext = false;
				}
			} while (isNext);
			return STATE.Success;
		} catch (Exception e) {
			// e.printStackTrace();
			if (e.getClass().getName().equals("java.net.UnknownHostException")
					|| e.getClass().getName()
							.equals("java.net.SocketException")) {
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}

	public  STATE getArrivalInfo(Map<String, String> relationLineMap) {
		String url = Constant.BASE_URL + relationLineMap.get("relationLineUrl");
		return  HtmlBaseParse.getInstacne(c).getArrivalInfo(url);
	}

	public List<Map<String, String>> getRelationStopList() {
		return relationStopList;
	}

	public List<Map<String, String>> getRelationLineList() {
		return relationLineList;
	}

	
	
}