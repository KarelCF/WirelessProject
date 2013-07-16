package com.moka.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Menus extends BaseColumns {
	
	 // ע��,�˴�AUTHORITYһ��Ҫ��Manifest.xml�е�������ȫ��ͬ
     public static final String AUTHORITY = "com.moka.menuprovider";
     // ����
     public static final String TABLE_NAME = "MenuTbl";
     // ���ʱ��������URI
     public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
     // �ֶ���
     public static final String PRICE= "price";				// �۸�
     public static final String TYPE_ID = "typeId";			//����
     public static final String NAME= "name";				// ����
     public static final String PIC= "pic";					// ͼƬ
     public static final String REMARK= "remark";			// ��ע
     // �������
     public static final String SORT_ORDER = "_id ASC";
     
}
