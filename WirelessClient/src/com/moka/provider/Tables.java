package com.moka.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Tables extends BaseColumns {
	
	 // ע��,�˴�AUTHORITYһ��Ҫ��Manifest.xml�е�������ȫ��ͬ
     public static final String AUTHORITY = "com.moka.tableprovider";
     // ����
     public static final String TABLE_NAME = "TableTbl";
     // ���ʱ��������URI
     public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
     // �ֶ���
     public static final String NUM = "num";
     public static final String DESCRIPTION = "description";
     // �������
     public static final String SORT_ORDER = "num DESC";
     
}
