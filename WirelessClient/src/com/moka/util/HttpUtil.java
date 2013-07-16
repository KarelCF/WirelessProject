package com.moka.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	// 基础URL,注意此处若为模拟器实验，ip不可为localhost而需要为需要为10.0.2.2
	public static final String BASE_URL = "http://172.16.2.137:8080/WirelessServer/";

	// 获得Get请求对象request
	public static HttpGet getHttpGet(String url) {
		HttpGet request = new HttpGet(url);
		return request;
	}

	// 获得Post请求对象request
	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);
		return request;
	}

	// 根据请求获得响应对象response
	public static HttpResponse getHttpResponse(HttpGet request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	// 根据请求获得响应对象response
	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	// 发送Post请求，获得响应查询结果
	public static String queryStringForPost(String url, String account,
			String password) {
		// 根据url获得HttpPost对象
		HttpPost request = HttpUtil.getHttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", account));
		params.add(new BasicNameValuePair("password", password));
		String result = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应
				result = EntityUtils.toString(response.getEntity(), "gbk");
				return result;
			} 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
		return null;
	}

	// 发送Post请求，获得响应查询结果
	public static String queryStringForPost(HttpPost request, List<NameValuePair> params) {
		// 根据url获得HttpPost对象
		String result = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应
				result = EntityUtils.toString(response.getEntity(), "gbk");
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
		return null;
	}

	// 发送Get请求，获得响应查询结果
	public static String queryStringForGet(String url) {
		// 获得HttpGet对象
		HttpGet request = HttpUtil.getHttpGet(url);
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应
				result = EntityUtils.toString(response.getEntity(), "gbk");
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
		return null;
	}
	
}
