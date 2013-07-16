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
		setTitle("登录系统");
		setContentView(R.layout.activity_login);
		
		 /*下面两段代码使Android3.0以上系统可以让 http代码使用主UI线程,因为3.0以上系统对UI资源的使用更严格*/
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
				// 检查用户是否输入用户名密码
				if (validate()) {
					// 检查是否登陆成功
					if (login()) {
						Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
						LoginActivity.this.startActivity(intent);
					} else {
						Toast.makeText(LoginActivity.this, "用户名称或者密码错误，请重新输入！",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	// 登录方法
	private boolean login() {
		// 获得用户名称
		String username = userEditText.getText().toString().trim();
		// 获得密码
		String pwd = pwdEditText.getText().toString().trim();
		// 获得登录结果
		String result = query(username, pwd);
		if (result != null && result.equals("0")) {
			return false;
		} else if (result.equals("网络异常！")) {
			return false;
		} else {
			// 将此服务器返回的此用户信息保存起来
			saveUserMsg(result);
			return true;
		}
	}

	// 将用户信息保存到配置文件
	private void saveUserMsg(String msg) {
		// 用户编号
		String id = "";
		// 用户名称
		String name = "";
		// 获得信息数组
		String[] msgs = msg.split(";");
		int idx = msgs[0].indexOf("=");
		id = msgs[0].substring(idx + 1);
		idx = msgs[1].indexOf("=");
		name = msgs[1].substring(idx + 1);
		// 共享信息
		SharedPreferences pre = getSharedPreferences("user_msg",
				MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("id", id);
		editor.putString("name", name);
		editor.commit();
	}

	// 验证方法
	private boolean validate() {
		String username = userEditText.getText().toString();
		if (username.equals("")) {
			Toast.makeText(LoginActivity.this, "用户名称是必填项！", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		String pwd = pwdEditText.getText().toString();
		if (pwd.equals("")) {
			Toast.makeText(LoginActivity.this, "用户密码是必填项！", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	// 根据用户名称密码查询
	private String query(String account, String password) {
		// 组合url,后面一项为在服务器端配置的web.xml中的url-pattern
		String url = HttpUtil.BASE_URL + "servlet/LoginServlet";
		// 查询返回结果
		return HttpUtil.queryStringForPost(url, account, password);
	}
	
}
