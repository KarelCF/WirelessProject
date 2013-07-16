package com.moka.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Tables extends BaseColumns {
	
	 // 注意,此处AUTHORITY一定要和Manifest.xml中的配置完全相同
     public static final String AUTHORITY = "com.moka.tableprovider";
     // 表名
     public static final String TABLE_NAME = "TableTbl";
     // 访问本表所需的URI
     public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
     // 字段名
     public static final String NUM = "num";
     public static final String DESCRIPTION = "description";
     // 排序操作
     public static final String SORT_ORDER = "num DESC";
     
}
