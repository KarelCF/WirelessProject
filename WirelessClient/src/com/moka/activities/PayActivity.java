package com.moka.activities;

import com.moka.util.HttpUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PayActivity extends Activity {
	
	private EditText orderIdEditText = null;
	private Button queryOrderBtn = null;
	private WebView showOrderWebView = null;
	private Button payBtn = null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		
		 /*下面两段代码使Android3.0以上系统可以让 http代码使用主UI线程,因为3.0以上系统对UI资源的使用更严格*/
        StrictMode. setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads().detectDiskWrites().detectNetwork()
        .penaltyLog().build());

        StrictMode. setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects().penaltyLog()
        .penaltyDeath().build());
		
		orderIdEditText = (EditText) findViewById(R.id.orderIdEditText);
		queryOrderBtn = (Button) findViewById(R.id.queryOrderBtn);
		showOrderWebView = (WebView) findViewById(R.id.showOrderWebView);
		payBtn = (Button) findViewById(R.id.payBtn);
		
		queryOrderBtn.setOnClickListener(new QueryOrderBtnListener());
		payBtn.setOnClickListener(new PayBtnListener());
		
	}
	
	private class QueryOrderBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String orderId = PayActivity.this.orderIdEditText.getText().toString();
			if (orderId == null || orderId.equals("")) {
				Toast.makeText(PayActivity.this, "请输入订单号", Toast.LENGTH_SHORT).show();
				return;
			}
			// 使用Get方式查询订单信息
			String url = HttpUtil.BASE_URL + "servlet/QueryOrderServlet?id=" + orderId;
			String resultHtml = HttpUtil.queryStringForGet(url);
			// 此处解决真机webview中文乱码的问题方法是用loadDataWithBaseURL加载html且编码设为utf-8
			PayActivity.this.showOrderWebView.loadDataWithBaseURL(null, resultHtml, "text/html", "utf-8", null);
			//PayActivity.this.showOrderWebView.loadData(resultHtml, "text/html", "gbk");
		}
	}
	
	private class PayBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String orderId = PayActivity.this.orderIdEditText.getText().toString();
			if (orderId == null || orderId.equals("")) {
				Toast.makeText(PayActivity.this, "请输入订单号", Toast.LENGTH_SHORT).show();
				return;
			}
			// 使用Get方式得服务器返回结果
			String url = HttpUtil.BASE_URL + "servlet/PayServlet?id=" + orderId;
			String result = HttpUtil.queryStringForGet(url);
			Toast.makeText(PayActivity.this, result, Toast.LENGTH_SHORT).show();
		}
	}
	
}
