package com.moka.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.moka.provider.Menus;
import com.moka.provider.Tables;
import com.moka.util.HttpUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UpdateActivity extends ListActivity{
	
	ListView listView = null;
	ListAdapter adapter = null;
	String[] items = {"更新桌号信息", "更新菜单信息"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 /*下面两段代码使Android3.0以上系统可以让 http代码使用主UI线程,因为3.0以上系统对UI资源的使用更严格*/
        StrictMode. setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads().detectDiskWrites().detectNetwork()
        .penaltyLog().build());

        StrictMode. setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects().penaltyLog()
        .penaltyDeath().build());

		listView = this.getListView();
		adapter = new ArrayAdapter<String>(UpdateActivity.this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(adapter);
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// 将点击位置声明为一个final值在dialog中的按钮监听方法中使用
		final int pos = position;
		// 弹出dialog提醒是否确定更新, 防止误操作
		Dialog dialog = new AlertDialog.Builder(UpdateActivity.this).setMessage("确定要更新么?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (0 == pos) {
							// 第0个位置, 执行更新桌号方法
							updateTables();
						} else if (1 == pos) {
							// 第1个位置, 执行更新菜单方法
							updateMenus();
						}
					}
				}).setNegativeButton("取消", null).show();
	}
	
	private void updateTables() {
		String urlString = HttpUtil.BASE_URL + "servlet/UpdateTableServlet";
		InputStream is = null;
		URLConnection conn = null;
		
		try {
			// 实例化目标servlet的地址并取得连接的输入流
			URL url = new URL(urlString);
			conn = url.openConnection();
			is = conn.getInputStream();
			
			// 准备读取xml文件所需的所有类实例
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
			// 获取所有table节点装入列表
			NodeList nodeList = doc.getElementsByTagName("table");
			
			// 获得ContentResolver在更新前删除旧内容
			ContentResolver resolver = this.getContentResolver();
			Uri tableProviderURI = Tables.CONTENT_URI;
			resolver.delete(tableProviderURI, null, null);
		
			// 从xml中提取数据并用ContentProvider插入sqlite表中
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element e = (Element) nodeList.item(i);
				ContentValues values = new ContentValues();
				values.put("_id", e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
				values.put("num", e.getElementsByTagName("num").item(0).getFirstChild().getNodeValue());
				values.put("description", e.getElementsByTagName("description").item(0).getFirstChild().getNodeValue());
System.out.println("_id: " + e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
				resolver.insert(tableProviderURI, values);
			}
			Toast.makeText(UpdateActivity.this, "更新桌位成功", Toast.LENGTH_SHORT).show();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateMenus() {
		String urlString = HttpUtil.BASE_URL + "servlet/UpdateMenuServlet";
		InputStream is = null;
		URLConnection conn = null;
		
		try {
			// 实例化目标servlet的地址并取得连接的输入流
			URL url = new URL(urlString);
			conn = url.openConnection();
			is = conn.getInputStream();
			
			// 准备读取xml文件所需的所有类实例
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
			// 获取所有table节点装入列表
			NodeList nodeList = doc.getElementsByTagName("menu");
			
			// 获得ContentResolver在更新前删除旧内容
			ContentResolver resolver = this.getContentResolver();
			Uri menuProviderURI = Menus.CONTENT_URI;
			resolver.delete(menuProviderURI, null, null);
		
			// 从xml中提取数据并用ContentProvider插入sqlite表中
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element e = (Element) nodeList.item(i);
				ContentValues values = new ContentValues();
				values.put("_id", e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
				values.put("price", e.getElementsByTagName("price").item(0).getFirstChild().getNodeValue());
				values.put("typeId", e.getElementsByTagName("typeId").item(0).getFirstChild().getNodeValue());
				values.put("name", e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
				values.put("pic", e.getElementsByTagName("pic").item(0).getFirstChild().getNodeValue());
				values.put("remark", e.getElementsByTagName("remark").item(0).getFirstChild().getNodeValue());
System.out.println("_id: " + e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
				resolver.insert(menuProviderURI, values);
			}
			Toast.makeText(UpdateActivity.this, "更新菜单成功", Toast.LENGTH_SHORT).show();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
