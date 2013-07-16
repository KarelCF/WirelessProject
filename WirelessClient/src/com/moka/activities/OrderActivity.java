package com.moka.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.moka.provider.Menus;
import com.moka.provider.Tables;
import com.moka.util.HttpUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class OrderActivity extends Activity {
	
	// ���������
	private Spinner tableNoSpinner = null;
	private EditText personNumEditText = null;
	private Button startBtn, addMealBtn, orderBtn;
	
	// ��˹��ܵ���Dialog���Զ��岼�����
	private Spinner dishNameSpinner = null;
	private EditText dishNumEditText = null;
	private EditText dishRemarkEditText = null;
	
	// �·���ʾ����Ӳ�Ʒ��ListView����adapter���������List
	private ListView orderDetailListView = null;
	private SimpleAdapter orderDetailAdapter = null;
	private List<Map<String, Object>> orderDetailList = new ArrayList<Map<String, Object>>();

	// ����Ʒ�����б����������
	private List<Map<String, Object>> dishes = null;
	private Map<String, Object> dish = null;
	
	// �����˲˵���������order���
	private String orderId = null;
	
	// �����Ʒ����ѡ��ʱ���ص�λ��
	private int selectedPosition;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
		
		 /*�������δ���ʹAndroid3.0����ϵͳ������ http����ʹ����UI�߳�,��Ϊ3.0����ϵͳ��UI��Դ��ʹ�ø��ϸ�*/
        StrictMode. setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads().detectDiskWrites().detectNetwork()
        .penaltyLog().build());

        StrictMode. setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects().penaltyLog()
        .penaltyDeath().build());


		
		tableNoSpinner = (Spinner) findViewById(R.id.tableNoSpinner);
		personNumEditText = (EditText) findViewById(R.id.personNumEditText);
		startBtn = (Button) findViewById(R.id.startBtn);
		addMealBtn = (Button) findViewById(R.id.addMealBtn);
		orderBtn = (Button) findViewById(R.id.orderBtn);
		orderDetailListView = (ListView) findViewById(R.id.orderDetailListView);
		
		startBtn.setOnClickListener(new StartBtnListener());
		addMealBtn.setOnClickListener(new AddMealBtnListener());
		orderBtn.setOnClickListener(new OrderBtnListener());
		
		
		// �����������б���ֵ
		setTableNoSpinner();
		// Ϊ��ʵ������Ӳ�Ʒ֮����ɾ���Ĺ���,��orderDetailListViewע�������Ĳ˵�
		this.registerForContextMenu(orderDetailListView);
	}
		
	private class StartBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// ���ÿ�������
			startOrder();
		}
	}
	
	private class AddMealBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// ������˹���
			addMeal();
		}
	}
	
	private class OrderBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// ȷ���µ�����
			confirmDetails();
			//insertValueToTableTbl();
		}
	}
	
	/*
	 * �������Ĳ˵���Ӱ�ť
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("����ѡ��");
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "ɾ��");
	}
	
	/*
	 * ʵ��ɾ���ѵ��Ʒ
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// ���˵���Ϣ������ת�ͣ� �ɻ�ȡ��������ListView��Ԫ��position
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		// �жϵ�����ĸ�����ѡ��
				switch (item.getItemId()) {
				case Menu.FIRST + 1:
					// ��list�г�ȥ
					this.orderDetailList.remove(position);
					// ֪ͨadapter���иı�
					this.orderDetailAdapter.notifyDataSetChanged();
					Toast.makeText(OrderActivity.this, "�ɹ�ɾ��", Toast.LENGTH_SHORT).show();
					break;
				}
		return super.onContextItemSelected(item);
	}
	
	/*
	 * ������������б���
	 */
	private void setTableNoSpinner() {
		// ����tableNoQuery��ȡ�����
		Cursor result = tableNoQuery(null) ;
		OrderActivity.this.startManagingCursor(result) ;	
		List<Map<String,Object>> tables = new ArrayList<Map<String,Object>>() ;
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			Map<String,Object> table = new HashMap<String,Object>() ;
			table.put("_id", result.getInt(0)) ;
			table.put("num", result.getInt(1)) ;
			table.put("description", result.getString(2)) ;
			tables.add(table) ;
		}
		List<Integer> tableNoList = new ArrayList<Integer>();
		for (int i = 0; i < tables.size(); i++) {
			Map<String,Object> table = tables.get(i);
			tableNoList.add((Integer) table.get("_id"));
		}
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(OrderActivity.this, android.R.layout.simple_spinner_item, tableNoList);
		tableNoSpinner.setAdapter(adapter);
		
	}
	
	private Cursor tableNoQuery(String id) {
		if(id == null || "".equals(id)) {
			return super.getContentResolver().query(Tables.CONTENT_URI, null, null, null, Tables.SORT_ORDER);
		} else {
			return super.getContentResolver().query(Uri.withAppendedPath(Tables.CONTENT_URI,id), null, null, null, Tables.SORT_ORDER);
		}
	}
	
	/*
	 * ��������
	 */
	private void startOrder() {
		// �õ�����ʱ��
		String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		// �õ������û�id(֮ǰ��½��ʱ�򴢴��ڱ���)
		SharedPreferences sp = getSharedPreferences("user_msg", MODE_WORLD_READABLE);
		String userId = sp.getString("id", "");
		// �õ���ѡ����
		String tableId = this.tableNoSpinner.getSelectedItem().toString();
		// �õ�����
		String personNum = this.personNumEditText.getText().toString();
		if (personNum == null || personNum.equals("")) {
			Toast.makeText(OrderActivity.this, "����������", Toast.LENGTH_SHORT).show();
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderTime", orderTime));
		params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("tableId", tableId));
		params.add(new BasicNameValuePair("personNum", personNum));
		
		// �������������
		String url = HttpUtil.BASE_URL + "servlet/StartTableServlet";
		HttpPost request = HttpUtil.getHttpPost(url); 
		orderId = HttpUtil.queryStringForPost(request, params);
		Toast.makeText(OrderActivity.this, "�����ɹ�, �������: " + orderId, Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * ʵ����˹��ܷ���
	 */
	private void addMeal() {
		// LayoutInflater��һ��xml�����ļ�ʵ����Ϊһ��view����
		LayoutInflater inflater = LayoutInflater.from(OrderActivity.this);
		View view = inflater.inflate(R.layout.order_detail, null)
				;
		// ʵ��������dialog��������������
		dishNameSpinner = (Spinner) view.findViewById(R.id.dishNameSpinner);
		dishNumEditText = (EditText) view.findViewById(R.id.dishNumEditText);
		dishRemarkEditText = (EditText) view.findViewById(R.id.remarkEditText);
		
		// ��������Ʒ����������
		setDishNameSpinner(dishNameSpinner);
		// �󶨼����¼�,��¼��ѡ��Ʒλ��
		dishNameSpinner.setOnItemSelectedListener(new ChooseDishListener());
		// ���õ���Dialog
		Dialog dialog = new AlertDialog.Builder(OrderActivity.this)
			.setTitle("����")
			// �趨�Զ���View
			.setView(view)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ����·�ListView�ľ���ʵ�ַ���
					String dishName = OrderActivity.this.dishNameSpinner.getSelectedItem().toString();
					String dishNum = OrderActivity.this.dishNumEditText.getText().toString();
					String dishRemark = OrderActivity.this.dishRemarkEditText.getText().toString();
					if (dishNum == null || dishNum.equals("")) {
						Toast.makeText(OrderActivity.this, "����������", Toast.LENGTH_SHORT).show();
						return;
					}
					Map<String, Object> map = dishes.get(OrderActivity.this.selectedPosition);
					map.put("dishName", dishName);
					map.put("dishNum", dishNum);
					map.put("dishRemark", dishRemark);
					orderDetailList.add(map);
					orderDetailAdapter = new SimpleAdapter(OrderActivity.this, 
							orderDetailList, 
							R.layout.order_detail_listview, 
							new String[] {"dishName", "dishNum", "dishRemark"}, 
							new int[] {R.id.dishNameText, R.id.dishNumText, R.id.dishRemarkText});
					orderDetailListView.setAdapter(orderDetailAdapter);
				}
			}).setNegativeButton("ȡ��", null)
			.show();
	}
	
	/*
	 * ����Ʒ�������ķ���
	 */
	private void setDishNameSpinner(Spinner dishNameSpinner) {
				//insertValueToMenuTbl();
System.out.println("setDishNameSpinner()");
				// ����dishNameQuery��ȡ�����
				Cursor result = dishNameQuery(null) ;
System.out.println("return to setDishNameSpinner()");
				OrderActivity.this.startManagingCursor(result) ;	
				dishes = new ArrayList<Map<String,Object>>() ;
				for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
					dish = new HashMap<String,Object>() ;
					dish.put("_id", result.getInt(0)) ;
					dish.put("price", result.getInt(1)) ;
					dish.put("typeId", result.getInt(2)) ;
					dish.put("name", result.getString(3)) ;
					dish.put("pic", result.getString(4)) ;
					dish.put("remark", result.getString(5)) ;
					dishes.add(dish) ;
				}
				List<String> dishNameList = new ArrayList<String>();
				for (int i = 0; i < dishes.size(); i++) {
					Map<String,Object> dish = dishes.get(i);
					dishNameList.add(dish.get("name") + ": ��" + dish.get("price"));
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderActivity.this, android.R.layout.simple_spinner_item, dishNameList);
				dishNameSpinner.setAdapter(adapter);
	}
	
	private Cursor dishNameQuery(String id) {
		if(id == null || "".equals(id)) {
System.out.println("dishNameQuery(null)");
			return super.getContentResolver().query(Menus.CONTENT_URI, null, null, null, Menus.SORT_ORDER);
		} else {
			return super.getContentResolver().query(Uri.withAppendedPath(Menus.CONTENT_URI,id), null, null, null, Menus.SORT_ORDER);
		}
	}
	
	/*
	 * ��Ʒ�������İ󶨷���
	 */
	private class ChooseDishListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			// ��¼��ѡλ��
			OrderActivity.this.selectedPosition = position;
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	/*
	 * �µ���������ϸ�����ݴ�����������
	 */
	private void confirmDetails() {
		if (this.orderId == null) {
			Toast.makeText(OrderActivity.this, "���ȿ���", Toast.LENGTH_LONG).show();
			return;
		}
		for (int i = 0; i < orderDetailList.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) orderDetailList.get(i);
			// ��õ����ϸ��Ϣ(����������)
			String menuId = "" + map.get("_id");
			String dishNum = (String) map.get("dishNum");
			String remark = (String) map.get("dishRemark");
			
			// ���Post����
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("orderId", this.orderId));
			params.add(new BasicNameValuePair("menuId", menuId));
			params.add(new BasicNameValuePair("dishNum", dishNum));
			params.add(new BasicNameValuePair("remark", remark));
			
			// �������������
			String url = HttpUtil.BASE_URL + "servlet/OrderDetailServlet";
			HttpPost request = HttpUtil.getHttpPost(url); 
			String result = HttpUtil.queryStringForPost(request, params);
			Toast.makeText(OrderActivity.this, result, Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
  /*  // ��ʼ��ʱ��Ҫ��һЩ���һЩ����
 	private void insertValueToTableTbl() {
		long id = 0 ;
		id = OrderActivity.this.testInsert(0, 0, "GOOD");
	}

	private long testInsert(int num, int flag, String description) {
		ContentResolver contentResolver = super.getContentResolver() ;	
		ContentValues values = new ContentValues() ;
		values.put(Tables.NUM, num) ;
		values.put(Tables.DESCRIPTION, description) ;
		Uri resultUri = contentResolver.insert(
				Tables.CONTENT_URI, values);
		return ContentUris.parseId(resultUri) ;
	}
	
	
	
    // ��ʼ��ʱ��Ҫ��һЩ���һЩ����
 	private void insertValueToMenuTbl() {
		long id = 0 ;
		id = OrderActivity.this.testInsert(10, 1, "���Զ���", "GOOD");
		id = OrderActivity.this.testInsert(30, 2, "�Ǵ���", "GOOD");
		id = OrderActivity.this.testInsert(40, 2, "�����Ź�", "GOOD");
		id = OrderActivity.this.testInsert(8, 1, "����˿", "GOOD");
		id = OrderActivity.this.testInsert(12, 1, "������", "GOOD");
 	}

	private long testInsert(int price, int typeId, String name, String remark) {
		ContentResolver contentResolver = super.getContentResolver() ;	
		ContentValues values = new ContentValues() ;
		values.put(Menus.PRICE, price);
		values.put(Menus.TYPE_ID, typeId);
		values.put(Menus.NAME, name);
		values.put(Menus.REMARK, remark);
		Uri resultUri = contentResolver.insert(
				Menus.CONTENT_URI, values);
		return ContentUris.parseId(resultUri) ;
	}*/
	
}
