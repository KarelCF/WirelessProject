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
		
		 /*�������δ���ʹAndroid3.0����ϵͳ������ http����ʹ����UI�߳�,��Ϊ3.0����ϵͳ��UI��Դ��ʹ�ø��ϸ�*/
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
				Toast.makeText(PayActivity.this, "�����붩����", Toast.LENGTH_SHORT).show();
				return;
			}
			// ʹ��Get��ʽ��ѯ������Ϣ
			String url = HttpUtil.BASE_URL + "servlet/QueryOrderServlet?id=" + orderId;
			String resultHtml = HttpUtil.queryStringForGet(url);
			// �˴�������webview������������ⷽ������loadDataWithBaseURL����html�ұ�����Ϊutf-8
			PayActivity.this.showOrderWebView.loadDataWithBaseURL(null, resultHtml, "text/html", "utf-8", null);
			//PayActivity.this.showOrderWebView.loadData(resultHtml, "text/html", "gbk");
		}
	}
	
	private class PayBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String orderId = PayActivity.this.orderIdEditText.getText().toString();
			if (orderId == null || orderId.equals("")) {
				Toast.makeText(PayActivity.this, "�����붩����", Toast.LENGTH_SHORT).show();
				return;
			}
			// ʹ��Get��ʽ�÷��������ؽ��
			String url = HttpUtil.BASE_URL + "servlet/PayServlet?id=" + orderId;
			String result = HttpUtil.queryStringForGet(url);
			Toast.makeText(PayActivity.this, result, Toast.LENGTH_SHORT).show();
		}
	}
	
}
