package com.moka.adapter;


import com.moka.activities.MainMenuActivity;
import com.moka.activities.OrderActivity;
import com.moka.activities.PayActivity;
import com.moka.activities.R;
import com.moka.activities.UpdateActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageAdapter extends BaseAdapter {
	
	private Context context = null;
	
	// �����ͼƬ��Դ
	private Integer[] functionImgs = { R.drawable.diancai, R.drawable.bingtai, R.drawable.zhuantai,
			R.drawable.chatai, R.drawable.gengxin, R.drawable.shezhi,
			R.drawable.zhuxiao, R.drawable.jietai };
	
	public ImageAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return functionImgs.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ׼����GridView������ImageViewͼƬ����һЩ����
		ImageView imageView = null;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		// ����ͼƬԴ
		imageView.setImageResource(this.functionImgs[position]);
		
		// ���ü����¼�
		switch (position) {
		case 0:
			imageView.setOnClickListener(new OrderListener());
			break;
		case 1:
			imageView.setOnClickListener(new UnionTableListener());
			break;
		case 2:
			imageView.setOnClickListener(new ChangeTableListener());
			break;
		case 3:
			imageView.setOnClickListener(new CheckTableListener());
			break;
		case 4:
			imageView.setOnClickListener(new UpdateListener());
			break;
		case 5:
			imageView.setOnClickListener(new SetupListener());
			break;
		case 6:
			imageView.setOnClickListener(new ExitListener());
			break;
		case 7:
			imageView.setOnClickListener(new PayListener());
			break;
		}
		
		return imageView;
	}
	
	private class OrderListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, OrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		
	}
	private class UnionTableListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(context, "��̨��", Toast.LENGTH_SHORT).show();
		}
		
	}
	private class ChangeTableListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(context, "ת̨��", Toast.LENGTH_SHORT).show();
		}
		
	}
	private class CheckTableListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(context, "��̨��", Toast.LENGTH_SHORT).show();
		}
		
	}
	private class UpdateListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, UpdateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		
	}
	private class SetupListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(context, "���ã�", Toast.LENGTH_SHORT).show();
		}
		
	}
	private class ExitListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(context, "ע����", Toast.LENGTH_SHORT).show();
		}
		
	}
	private class PayListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, PayActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		
	}

}
