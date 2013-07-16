package com.moka.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Menus extends BaseColumns {
	
	 // 注意,此处AUTHORITY一定要和Manifest.xml中的配置完全相同
     public static final String AUTHORITY = "com.moka.menuprovider";
     // 表名
     public static final String TABLE_NAME = "MenuTbl";
     // 访问本表所需的URI
     public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
     // 字段名
     public static final String PRICE= "price";				// 价格
     public static final String TYPE_ID = "typeId";			//类型
     public static final String NAME= "name";				// 名称
     public static final String PIC= "pic";					// 图片
     public static final String REMARK= "remark";			// 备注
     // 排序操作
     public static final String SORT_ORDER = "_id ASC";
     
}
