package com.moka.activities;

import com.moka.util.HttpUtil;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Button cancelBtn, loginBtn;
	private EditText userEditText, pwdEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("��¼ϵͳ");
		setContentView(R.layout.activity_login);
		
		 /*�������δ���ʹAndroid3.0����ϵͳ������ http����ʹ����UI�߳�,��Ϊ3.0����ϵͳ��UI��Դ��ʹ�ø��ϸ�*/
        StrictMode. setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads().detectDiskWrites().detectNetwork()
        .penaltyLog().build());

        StrictMode. setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects().penaltyLog()
        .penaltyDeath().build());

		cancelBtn = (Button) findViewById(R.id.cancelButton);
		loginBtn = (Button) findViewById(R.id.loginButton);
		userEditText = (EditText) findViewById(R.id.userEditText);
		pwdEditText = (EditText) findViewById(R.id.pwdEditText);

		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����û��Ƿ������û�������
				if (validate()) {
					// ����Ƿ��½�ɹ�
					if (login()) {
						Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
						LoginActivity.this.startActivity(intent);
					} else {
						Toast.makeText(LoginActivity.this, "�û����ƻ�������������������룡",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	// ��¼����
	private boolean login() {
		// ����û�����
		String username = userEditText.getText().toString().trim();
		// �������
		String pwd = pwdEditText.getText().toString().trim();
		// ��õ�¼���
		String result = query(username, pwd);
		if (result != null && result.equals("0")) {
			return false;
		} else if (result.equals("�����쳣��")) {
			return false;
		} else {
			// ���˷��������صĴ��û���Ϣ��������
			saveUserMsg(result);
			return true;
		}
	}

	// ���û���Ϣ���浽�����ļ�
	private void saveUserMsg(String msg) {
		// �û����
		String id = "";
		// �û�����
		String name = "";
		// �����Ϣ����
		String[] msgs = msg.split(";");
		int idx = msgs[0].indexOf("=");
		id = msgs[0].substring(idx + 1);
		idx = msgs[1].indexOf("=");
		name = msgs[1].substring(idx + 1);
		// ������Ϣ
		SharedPreferences pre = getSharedPreferences("user_msg",
				MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("id", id);
		editor.putString("name", name);
		editor.commit();
	}

	// ��֤����
	private boolean validate() {
		String username = userEditText.getText().toString();
		if (username.equals("")) {
			Toast.makeText(LoginActivity.this, "�û������Ǳ����", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		String pwd = pwdEditText.getText().toString();
		if (pwd.equals("")) {
			Toast.makeText(LoginActivity.this, "�û������Ǳ����", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	// �����û����������ѯ
	private String query(String account, String password) {
		// ���url,����һ��Ϊ�ڷ����������õ�web.xml�е�url-pattern
		String url = HttpUtil.BASE_URL + "servlet/LoginServlet";
		// ��ѯ���ؽ��
		return HttpUtil.queryStringForPost(url, account, password);
	}
	
}
