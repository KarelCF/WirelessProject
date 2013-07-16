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
	
	// 主界面组件
	private Spinner tableNoSpinner = null;
	private EditText personNumEditText = null;
	private Button startBtn, addMealBtn, orderBtn;
	
	// 添菜功能弹出Dialog的自定义布局组件
	private Spinner dishNameSpinner = null;
	private EditText dishNumEditText = null;
	private EditText dishRemarkEditText = null;
	
	// 下方显示已添加菜品的ListView及其adapter与填充数据List
	private ListView orderDetailListView = null;
	private SimpleAdapter orderDetailAdapter = null;
	private List<Map<String, Object>> orderDetailList = new ArrayList<Map<String, Object>>();

	// 填充菜品下拉列表所需的数据
	private List<Map<String, Object>> dishes = null;
	private Map<String, Object> dish = null;
	
	// 声明此菜单所属的主order编号
	private String orderId = null;
	
	// 点击菜品下拉选单时返回的位置
	private int selectedPosition;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
		
		 /*下面两段代码使Android3.0以上系统可以让 http代码使用主UI线程,因为3.0以上系统对UI资源的使用更严格*/
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
		
		
		// 给桌号下拉列表填值
		setTableNoSpinner();
		// 为了实现在添加菜品之后将其删除的功能,给orderDetailListView注册上下文菜单
		this.registerForContextMenu(orderDetailListView);
	}
		
	private class StartBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 调用开桌方法
			startOrder();
		}
	}
	
	private class AddMealBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 调用添菜功能
			addMeal();
		}
	}
	
	private class OrderBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 确认下单功能
			confirmDetails();
			//insertValueToTableTbl();
		}
	}
	
	/*
	 * 给上下文菜单添加按钮
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("操作选项");
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "删除");
	}
	
	/*
	 * 实现删除已点菜品
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 将菜单信息作如下转型， 可获取所长按的ListView中元素position
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		// 判断点击了哪个操作选项
				switch (item.getItemId()) {
				case Menu.FIRST + 1:
					// 从list中除去
					this.orderDetailList.remove(position);
					// 通知adapter进行改变
					this.orderDetailAdapter.notifyDataSetChanged();
					Toast.makeText(OrderActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
					break;
				}
		return super.onContextItemSelected(item);
	}
	
	/*
	 * 填充桌号下拉列表方法
	 */
	private void setTableNoSpinner() {
		// 调用tableNoQuery获取结果集
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
	 * 开桌方法
	 */
	private void startOrder() {
		// 得到开桌时间
		String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		// 得到操作用户id(之前登陆的时候储存在本地)
		SharedPreferences sp = getSharedPreferences("user_msg", MODE_WORLD_READABLE);
		String userId = sp.getString("id", "");
		// 得到所选桌号
		String tableId = this.tableNoSpinner.getSelectedItem().toString();
		// 得到人数
		String personNum = this.personNumEditText.getText().toString();
		if (personNum == null || personNum.equals("")) {
			Toast.makeText(OrderActivity.this, "请输入人数", Toast.LENGTH_SHORT).show();
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderTime", orderTime));
		params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("tableId", tableId));
		params.add(new BasicNameValuePair("personNum", personNum));
		
		// 向服务器传数据
		String url = HttpUtil.BASE_URL + "servlet/StartTableServlet";
		HttpPost request = HttpUtil.getHttpPost(url); 
		orderId = HttpUtil.queryStringForPost(request, params);
		Toast.makeText(OrderActivity.this, "开桌成功, 订单编号: " + orderId, Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * 实现添菜功能方法
	 */
	private void addMeal() {
		// LayoutInflater将一个xml布局文件实例化为一个view对象
		LayoutInflater inflater = LayoutInflater.from(OrderActivity.this);
		View view = inflater.inflate(R.layout.order_detail, null)
				;
		// 实例化弹出dialog中所需的三个组件
		dishNameSpinner = (Spinner) view.findViewById(R.id.dishNameSpinner);
		dishNumEditText = (EditText) view.findViewById(R.id.dishNumEditText);
		dishRemarkEditText = (EditText) view.findViewById(R.id.remarkEditText);
		
		// 调用填充菜品下拉单方法
		setDishNameSpinner(dishNameSpinner);
		// 绑定监听事件,记录所选菜品位置
		dishNameSpinner.setOnItemSelectedListener(new ChooseDishListener());
		// 设置弹出Dialog
		Dialog dialog = new AlertDialog.Builder(OrderActivity.this)
			.setTitle("请点餐")
			// 设定自定义View
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 填充下方ListView的具体实现方法
					String dishName = OrderActivity.this.dishNameSpinner.getSelectedItem().toString();
					String dishNum = OrderActivity.this.dishNumEditText.getText().toString();
					String dishRemark = OrderActivity.this.dishRemarkEditText.getText().toString();
					if (dishNum == null || dishNum.equals("")) {
						Toast.makeText(OrderActivity.this, "请输入数量", Toast.LENGTH_SHORT).show();
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
			}).setNegativeButton("取消", null)
			.show();
	}
	
	/*
	 * 填充菜品下拉单的方法
	 */
	private void setDishNameSpinner(Spinner dishNameSpinner) {
				//insertValueToMenuTbl();
System.out.println("setDishNameSpinner()");
				// 调用dishNameQuery获取结果集
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
					dishNameList.add(dish.get("name") + ": ￥" + dish.get("price"));
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
	 * 菜品下拉单的绑定方法
	 */
	private class ChooseDishListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			// 记录所选位置
			OrderActivity.this.selectedPosition = position;
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	/*
	 * 下单方法，将细节数据传到服务器端
	 */
	private void confirmDetails() {
		if (this.orderId == null) {
			Toast.makeText(OrderActivity.this, "请先开桌", Toast.LENGTH_LONG).show();
			return;
		}
		for (int i = 0; i < orderDetailList.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) orderDetailList.get(i);
			// 获得点菜详细信息(传给服务器)
			String menuId = "" + map.get("_id");
			String dishNum = (String) map.get("dishNum");
			String remark = (String) map.get("dishRemark");
			
			// 添加Post参数
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("orderId", this.orderId));
			params.add(new BasicNameValuePair("menuId", menuId));
			params.add(new BasicNameValuePair("dishNum", dishNum));
			params.add(new BasicNameValuePair("remark", remark));
			
			// 向服务器传数据
			String url = HttpUtil.BASE_URL + "servlet/OrderDetailServlet";
			HttpPost request = HttpUtil.getHttpPost(url); 
			String result = HttpUtil.queryStringForPost(request, params);
			Toast.makeText(OrderActivity.this, result, Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
  /*  // 初始化时需要开一些添加一些桌号
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
	
	
	
    // 初始化时需要开一些添加一些菜名
 	private void insertValueToMenuTbl() {
		long id = 0 ;
		id = OrderActivity.this.testInsert(10, 1, "干煸豆角", "GOOD");
		id = OrderActivity.this.testInsert(30, 2, "糖醋鱼", "GOOD");
		id = OrderActivity.this.testInsert(40, 2, "红烧排骨", "GOOD");
		id = OrderActivity.this.testInsert(8, 1, "土豆丝", "GOOD");
		id = OrderActivity.this.testInsert(12, 1, "烧茄子", "GOOD");
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
