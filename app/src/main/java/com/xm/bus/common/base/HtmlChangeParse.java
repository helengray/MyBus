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

public class HtmlChangeParse {
	private Document doc;
	private static HtmlChangeParse instance;
	public  List<Map<String, String>> sourceStopList;
	public  List<Map<String, String>> destinationStopList;
	public  List<Map<String, String>> planLineList;
	public  List<Map<String, String>> changeDetailList;
	private static Context c;
	private HtmlChangeParse(){}
	
	public static synchronized HtmlChangeParse getInstance(Context context){
		if(instance==null){
			instance=new HtmlChangeParse();
		}
		c=context;
		return instance;
	}
	public  STATE getSourceStop(String from) {

		sourceStopList = new ArrayList<Map<String, String>>();
		if (!from.trim().equals("")) {
			STATE state = HtmlStopParse.getInstance(c).getRelationStop(from);
			if (state == STATE.Success) {
				sourceStopList = HtmlStopParse.getInstance(c).getRelationStopList();
			}
			return state;
		}
		return STATE.InputError;
	}

	public  STATE getDestinationStop(String to) {
		destinationStopList = new ArrayList<Map<String, String>>();
		if (!to.trim().equals("")) {
			STATE state = HtmlStopParse.getInstance(c).getRelationStop(to);
			if (state == STATE.Success) {
				destinationStopList = HtmlStopParse.getInstance(c).getRelationStopList();
			}
			return state;
		}

		return STATE.InputError;

	}
	public  STATE getChangePlans(String from, String to) {
		try {
			planLineList = new ArrayList<Map<String, String>>();
			doc = Jsoup.connect(Constant.CHANGE_URL).data("from", from)
					.data("to", to).get();
			if (doc.select("div.error").size() == 0) {
				Elements nodes = doc.select("div.list a[href *=p]");
				for (Element node : nodes) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("changeLineName", node.text());
					map.put("changeLineUrl", node.attr("href"));
					planLineList.add(map);
				}
				return STATE.Success;
			} else {
				return STATE.LineNotExistError;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getClass().getName().equals("java.net.UnknownHostException")
					|| e.getClass().getName()
							.equals("java.net.SocketException")) {
				return STATE.ServerMaintenance;
			}
			return STATE.NetworkError;
		}
	}

	/**
	 * 
	 */
	public  STATE getChangeDetail(Map<String, String> planLineMap,
			String from, String to) {
		try {
			changeDetailList = new ArrayList<Map<String, String>>();
			String fromEncoder = URLEncoder.encode(from, "utf-8");
			String toEncoder = URLEncoder.encode(to, "utf-8");
			String url = planLineMap.get("changeLineUrl")
					.replace(from, fromEncoder).replace(to, toEncoder);
			doc = Jsoup.connect(Constant.BASE_URL + url).get();
			Elements nodes = doc.select("div.list");
			/*titleInfo2 = nodes.select("br").get(0).previousSibling().toString()
					.trim();*/
			Elements links = nodes.select("a[href *=nextbus]");
			for (Element node : links) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("relationLineName", node.text());
				map.put("relationLineUrl", node.attr("href"));
				map.put("up", node.nextElementSibling().nextSibling()
						.toString());
				map.put("down", node.nextElementSibling().nextElementSibling()
						.nextSibling().toString());
				changeDetailList.add(map);
			}
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

	public List<Map<String, String>> getSourceStopList() {
		return sourceStopList;
	}


	public List<Map<String, String>> getDestinationStopList() {
		return destinationStopList;
	}


	public List<Map<String, String>> getPlanLineList() {
		return planLineList;
	}


	public List<Map<String, String>> getChangeDetailList() {
		return changeDetailList;
	}

	
	
}