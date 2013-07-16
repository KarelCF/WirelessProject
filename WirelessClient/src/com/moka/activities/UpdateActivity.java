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
	String[] items = {"����������Ϣ", "���²˵���Ϣ"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 /*�������δ���ʹAndroid3.0����ϵͳ������ http����ʹ����UI�߳�,��Ϊ3.0����ϵͳ��UI��Դ��ʹ�ø��ϸ�*/
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
		// �����λ������Ϊһ��finalֵ��dialog�еİ�ť����������ʹ��
		final int pos = position;
		// ����dialog�����Ƿ�ȷ������, ��ֹ�����
		Dialog dialog = new AlertDialog.Builder(UpdateActivity.this).setMessage("ȷ��Ҫ����ô?")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (0 == pos) {
							// ��0��λ��, ִ�и������ŷ���
							updateTables();
						} else if (1 == pos) {
							// ��1��λ��, ִ�и��²˵�����
							updateMenus();
						}
					}
				}).setNegativeButton("ȡ��", null).show();
	}
	
	private void updateTables() {
		String urlString = HttpUtil.BASE_URL + "servlet/UpdateTableServlet";
		InputStream is = null;
		URLConnection conn = null;
		
		try {
			// ʵ����Ŀ��servlet�ĵ�ַ��ȡ�����ӵ�������
			URL url = new URL(urlString);
			conn = url.openConnection();
			is = conn.getInputStream();
			
			// ׼����ȡxml�ļ������������ʵ��
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
			// ��ȡ����table�ڵ�װ���б�
			NodeList nodeList = doc.getElementsByTagName("table");
			
			// ���ContentResolver�ڸ���ǰɾ��������
			ContentResolver resolver = this.getContentResolver();
			Uri tableProviderURI = Tables.CONTENT_URI;
			resolver.delete(tableProviderURI, null, null);
		
			// ��xml����ȡ���ݲ���ContentProvider����sqlite����
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element e = (Element) nodeList.item(i);
				ContentValues values = new ContentValues();
				values.put("_id", e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
				values.put("num", e.getElementsByTagName("num").item(0).getFirstChild().getNodeValue());
				values.put("description", e.getElementsByTagName("description").item(0).getFirstChild().getNodeValue());
System.out.println("_id: " + e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
				resolver.insert(tableProviderURI, values);
			}
			Toast.makeText(UpdateActivity.this, "������λ�ɹ�", Toast.LENGTH_SHORT).show();
			
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
			// ʵ����Ŀ��servlet�ĵ�ַ��ȡ�����ӵ�������
			URL url = new URL(urlString);
			conn = url.openConnection();
			is = conn.getInputStream();
			
			// ׼����ȡxml�ļ������������ʵ��
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
			// ��ȡ����table�ڵ�װ���б�
			NodeList nodeList = doc.getElementsByTagName("menu");
			
			// ���ContentResolver�ڸ���ǰɾ��������
			ContentResolver resolver = this.getContentResolver();
			Uri menuProviderURI = Menus.CONTENT_URI;
			resolver.delete(menuProviderURI, null, null);
		
			// ��xml����ȡ���ݲ���ContentProvider����sqlite����
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
			Toast.makeText(UpdateActivity.this, "���²˵��ɹ�", Toast.LENGTH_SHORT).show();
			
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
